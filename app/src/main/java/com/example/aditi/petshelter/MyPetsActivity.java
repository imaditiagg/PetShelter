package com.example.aditi.petshelter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPetsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    PostsAdapter adapter;
    Firebase databaseReferenceUser;
    ArrayList<Pet> posts;
    LottieAnimationView animationView;
    TextView textView;
    Toolbar toolbar;
    FloatingActionButton fab;
    DatabaseReference databaseReference;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pets);
        toolbar = findViewById(R.id.myposts_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("My Pets");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {
                                                     finish();
                                                 }
                                             }

        );
        recyclerView = findViewById(R.id.postsrecyclerview);
        animationView = findViewById(R.id.activity_posts_progress_bar);
        textView = findViewById(R.id.post_item_not_found_view);
        fab = findViewById(R.id.MyPostsFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();

            }
        });

        posts = new ArrayList<>();
        adapter = new PostsAdapter(MyPetsActivity.this, posts);


        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        posts.clear();
        recyclerView.setVisibility(View.GONE);
        animationView.setVisibility(View.VISIBLE);

        //Log.d("hehehe","OnCreatePost");
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String UserUID = currentFirebaseUser != null ? currentFirebaseUser.getUid() : null;
        databaseReferenceUser = new Firebase("https://petshelter-4e61e.firebaseio.com/users/" + UserUID + "/petid");

        databaseReferenceUser.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Pet pet;
                    pet = dataSnapshot1.getValue(Pet.class);
                    //Log.d("qwerty ", book.bookName);
                    posts.add(pet);

                }
                if (!posts.isEmpty()) {
                    adapter.notifyDataSetChanged();
                    animationView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    animationView.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String UserUID = currentFirebaseUser != null ? currentFirebaseUser.getUid() : null;

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(UserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                fab.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adapter.notifyDataSetChanged();
    }
    private void check() {
        if (user.city.equals("") || user.pincode.equals("") || user.state.equals("") || user.phone.equals("")) {
            //Log.i("profile","lalala");
            LayoutInflater inflater = (LayoutInflater) MyPetsActivity.this.getSystemService(MyPetsActivity.this.LAYOUT_INFLATER_SERVICE);

            final View output = inflater.inflate(R.layout.profile_alert, null, false);

            final AlertDialog.Builder builder = new AlertDialog.Builder(MyPetsActivity.this);
            builder.setView(output);
            final AlertDialog dialog = builder.create();
            dialog.show();


            Button button =  output.findViewById(R.id.button1);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent1 = new Intent(MyPetsActivity.this, MyProfileActivity.class);
                    //intent1.putExtra(Constants.CODE,"add");
                    dialog.dismiss();
                    startActivity(intent1);
                }
            });

            Button button1 =  output.findViewById(R.id.button2);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });

            builder.setCancelable(false);


        } else {
            Intent intent = new Intent(MyPetsActivity.this, AddActivity.class);
            startActivity(intent);
        }
    }
}
