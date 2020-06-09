package com.example.aditi.petshelter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OwnerDetailActivity extends AppCompatActivity {
    Intent intent;
    String obtainedUserId;
    //String obtainedBookId;
    DatabaseReference databaseReference;
    TextView nameView,emailView,phoneNoView,locationView;
    Button email,call,msg;
    Button moreBooks;
    ImageView profileView;
    String e,c;
    User user;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_owner_details_toolbar);
        setSupportActionBar(toolbar);


        Firebase.setAndroidContext(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {
                                                     finish();
                                                 }
                                             }

        );
        setTitle("Owner Detail");
        nameView= findViewById(R.id.owner_name);
        emailView=findViewById(R.id.owner_email);
        phoneNoView=findViewById(R.id.owner_contact);
        locationView=findViewById(R.id.owner_location);
        email=findViewById(R.id.email_owner_button);
        call=findViewById(R.id.call_owner_button);
        msg=findViewById(R.id.msg_owner_button);
        profileView = findViewById(R.id.profilepic);
        lottieAnimationView=findViewById(R.id.ownerDetails_progress_bar);

        intent=getIntent();
        obtainedUserId=intent.getStringExtra(Constants.USERID);
       // obtainedBookId= intent.getStringExtra(Constants.BOOK_ID1);
        Glide.with(getApplicationContext()).load(getResources().getDrawable(R.drawable.user2)).into(profileView);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(obtainedUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lottieAnimationView.setVisibility(View.GONE);
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    nameView.setText(user.name);
                    emailView.setText(user.email);
                    phoneNoView.setText(user.phone);
                    locationView.setText(user.city + ", " + user.state);
                    if(!user.Imageurl.equals("")) {
                        Log.i("lalala img", user.Imageurl);
                        Glide.with(getApplicationContext()).load(user.Imageurl).into(profileView);
                    }


                    e=user.email;
                    c=user.phone;
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO); //Only For Mail
                String em = "mailto:"+e;
                Uri u =Uri.parse(em);

                intent.setData(u);
                startActivity(intent);
            }
        });

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO);
                String no = "smsto:"+c;
                Uri uri = Uri.parse(no);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                String no= "tel:"+c;
                Uri uri = Uri.parse(no);
                intent.setData(uri);
                startActivity(intent);
            }
        });

    }
    }



