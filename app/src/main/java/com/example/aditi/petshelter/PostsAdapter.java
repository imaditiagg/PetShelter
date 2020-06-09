package com.example.aditi.petshelter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsHolder> {

    LayoutInflater inflater;
    Context context;
    ArrayList<Pet> items;
    Firebase databaseReferenceUser;
    String college,state,pincode;


    public PostsAdapter(Context context, ArrayList<Pet> items) {

        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View output= inflater.inflate(R.layout.posts_row,null,false);
        return new PostsHolder(output);

    }

    @Override
    public void onBindViewHolder(@NonNull final PostsHolder holder, int position) {

        final Pet pet = items.get(position);

        holder.petcategory.setText(pet.category);

        String s = pet.name;
        int length =s.length();
        Log.d("Detailtextlength",Integer.toString(length));

        SpannableString text = new SpannableString("....");

        if(length > 120 && (Math.abs(length - 120)) > 10){

            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(s,0,100);
            spannableStringBuilder.append(text);

            holder.petname.setText(spannableStringBuilder.toString());

        }

        else{

            holder.petname.setText(s);
        }

        String url=pet.imageURL;
        if(!url.equals(""))
                Glide.with(context).load(url).into(holder.petpic);


        if(pet.adopted){

            holder.sold.setText("");
            LinearLayout.LayoutParams Parameter = new LinearLayout.LayoutParams(175, 120);
            holder.sold.setLayoutParams(Parameter);
            Parameter.setMargins(10,90,0,0);
            holder.sold.setBackground(context.getResources().getDrawable(R.drawable.adopted3));
        }
        else{

            holder.sold.setText("MARK AS ADOPTED");
            holder.sold.setTextSize(10);
            holder.sold.setTypeface(null, Typeface.BOLD);
            holder.sold.setTextColor(context.getResources().getColor(R.color.color2));
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pet.id!=null) {
                    Intent intent = new Intent(context, PetDetailActivity.class);
                    Log.i("Id", pet.id);
                    intent.putExtra(Constants.PETID, pet.id);
                    intent.putExtra(Constants.USERID, pet.userID);
                    context.startActivity(intent);
                }

            }});




        holder.sold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Adopted");
                builder.setMessage("Do you want to mark as Adopted ?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        holder.sold.setBackground(context.getResources().getDrawable(R.drawable.adopted3));
                        holder.sold.setEnabled(false);

                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String UserUID = currentFirebaseUser.getUid();
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(UserUID);
                        databaseRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                                User user = (User) dataSnapshot.getValue(User.class);
                                state=user.state;
                                pincode=user.pincode;
                                holder.sold.setEnabled(false);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }) ;

                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Pet").child(pet.id);
                        DatabaseReference databaseReferenceUser= FirebaseDatabase.getInstance().getReference("users").child(UserUID).child("petid").child(pet.id);
                        Pet nPet=new Pet(pet.id,pet.category,pet.name,pet.species,pet.gender,pet.imageURL,pincode,true,state,pet.userID);
                        databaseReference.setValue(nPet);
                        databaseReferenceUser.setValue(nPet);



                    }

                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                final View output = inflater.inflate(R.layout.delete_alert_layout, null, false);
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(output);
                final AlertDialog dialog = builder.create();
                dialog.show();

                Button button = (Button) output.findViewById(R.id.b1);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String UserUID = currentFirebaseUser.getUid();
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Pet").child(pet.id);
                        DatabaseReference databaseReferenceUser= FirebaseDatabase.getInstance().getReference("users").child(UserUID).child("petid").child(pet.id);
                        databaseReference.removeValue();
                        databaseReferenceUser.removeValue();
                        Toast.makeText(context,"Pet Deleted Successfully",Toast.LENGTH_SHORT).show();
                    }
                });

                Button button1 = (Button) output.findViewById(R.id.b2);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

                builder.setCancelable(false);

            }




        });




    }

    @Override
    public int getItemCount() {

        return items.size();
    }


}

