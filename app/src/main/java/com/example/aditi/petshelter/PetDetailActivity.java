package com.example.aditi.petshelter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class PetDetailActivity extends AppCompatActivity {
    TextView nameView,speciesView,stateView,genderView;
    Button contactOwnerButton;
    Intent intent;
    Firebase mRef;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetailActivity);
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
        setTitle("Pet Detail");

        nameView=findViewById(R.id.name);
        speciesView=findViewById(R.id.species);
        stateView=findViewById(R.id.location);
        imageView=findViewById(R.id.petImage);
        genderView=findViewById(R.id.gender);
        intent=getIntent();
        final String petId = intent.getStringExtra(Constants.PETID);
        contactOwnerButton=findViewById(R.id.contactOwnerButton);
        mRef = new Firebase("https://petshelter-4e61e.firebaseio.com/Pet");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot pets = dataSnapshot.child(petId);
                Pet pet = pets.getValue(Pet.class);
                if(pet!=null){
                    nameView.setText(nameView.getText()+" "+pet.name);
                    if(!pet.species.equals(""))
                        speciesView.setText("Species : "+pet.species);
                    stateView.setText(pet.state);
                    if(pet.gender!=null)
                        genderView.setText(pet.gender);
                    String url = pet.imageURL;
                    if(!url.equals(""))
                       Glide.with(getApplicationContext()).load(url).into(imageView);
                    else
                        Glide.with(getApplicationContext()).load(getResources().getDrawable(R.drawable.pet1)).into(imageView);

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        contactOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent newIntent = new Intent(PetDetailActivity.this,OwnerDetailActivity.class);
               newIntent.putExtra(Constants.USERID,intent.getStringExtra(Constants.USERID));
               startActivity(newIntent);
            }
        });

    }
}
