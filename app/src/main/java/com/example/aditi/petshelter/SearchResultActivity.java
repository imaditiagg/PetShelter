package com.example.aditi.petshelter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    String category,species,gender,pincode,state;
    RecyclerView recyclerView;
    Intent intent;
    LottieAnimationView progressBar;
    TextView textView;
    ArrayList<Pet> searchPet = new ArrayList<>();
    PetAdapter adapter;
    FrameLayout layout;
    boolean var1,var2;
    Firebase databaseReferenceUser;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_result_toolbar);
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
        recyclerView = findViewById(R.id.search_result_recyclerView);
        progressBar = findViewById(R.id.activity_search_progress_bar);
        textView=findViewById(R.id.search_item_not_found_view);

        intent=getIntent();
        code = intent.getStringExtra(Constants.code);
        if(code.equals("search")) {
            setTitle("Search Result");
            category = intent.getStringExtra(Constants.CATEGORY);
            gender = intent.getStringExtra(Constants.GENDER);
            species = intent.getStringExtra(Constants.SPECIES);
            state = intent.getStringExtra(Constants.STATE);
            pincode = intent.getStringExtra(Constants.PINCODE);
        }
        else if(code.equals("view")){
            setTitle("Pet Gallery");
        }

        searchPet=new ArrayList<>();

        adapter=new PetAdapter(searchPet,this);
        layout=findViewById(R.id.search_layout);
        textView=findViewById(R.id.search_item_not_found_view);


        recyclerView.setItemAnimator(new DefaultItemAnimator());
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        searchPets();

    }

    void searchPets() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        if(code.equals("search")) {
            DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            Query query = mFirebaseDatabaseReference.child("Pet").orderByChild("category");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    searchPet.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        var1 = false;
                        var2 = false;
                        Pet p = dataSnapshot1.getValue(Pet.class);
                        if (!p.adopted && p.userPinCode.equals(pincode) && p.category.equals(category) && p.state.equals(state)) {
                            if (!species.equals("")) {
                                if (p.species.equals(species)) {
                                    var1 = true;
                                } else {
                                    var1 = false;
                                }
                            } else {
                                var1 = true;
                            }

                            if (gender != null) {
                                if (p.gender.equals(gender)) {
                                    var2 = true;
                                } else {
                                    var2 = false;
                                }
                            } else {
                                var2 = true;
                            }


                            if (var1 && var2) {

                                searchPet.add(p);
                                Log.i("lalala 1", searchPet + "");
                            }


                        }
                    }


                    if (!searchPet.isEmpty()) {
                        Log.i("lalala 2", searchPet + "");
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        //no result found
                        Log.i("lalala 3", searchPet + "");
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        LottieAnimationView animationView = new LottieAnimationView(SearchResultActivity.this);
                        animationView.setAnimation(R.raw.empty_list);
                        layout.addView(animationView);
                        animationView.playAnimation();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }
        else if(code.equals("view")){
            DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            Query query = mFirebaseDatabaseReference.child("Pet").orderByChild("adopted").equalTo(false);
            query.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    searchPet.clear();

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        Pet p=dataSnapshot1.getValue(Pet.class);
                        searchPet.add(p);

                    }

                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        }

    }
}


