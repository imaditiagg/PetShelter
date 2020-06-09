package com.example.aditi.petshelter;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AdoptPetActivity extends AppCompatActivity {

    EditText categoryEditText,speciedEditText;
    RadioGroup radioGroup;
    String gender,category,species,pincode,stateSpinnerItem;
    Button button;
    Spinner stateSpinner;
    EditText cityEditText,pincodeEditText;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt_pet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.adoptPet_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Search Pets for Adoption");
        Firebase.setAndroidContext(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {
                                                     finish();
                                                 }
                                             }

        );
        categoryEditText=findViewById(R.id.search_petCategory);
        speciedEditText=findViewById(R.id.search_petSpecies);
        button=findViewById(R.id.SearchButton);
        radioGroup=findViewById(R.id.radioGroup);
        pincodeEditText=findViewById(R.id.location_search_pincode);
        cityEditText=findViewById(R.id.location_search_city);
        stateSpinner=findViewById(R.id.location_search_state_spinner);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton=(RadioButton) findViewById(checkedId);
                gender = radioButton.getText().toString();
            }
        });

        ArrayList<String> states=new ArrayList<String>();
        states.add("Select State");
        String jsonString= loadJSONFromAsset();
        try {
            JSONArray array = new JSONArray(jsonString);
            for(int i=0;i<array.length();i++) {
                JSONObject object =(JSONObject) array.get(i);
                states.add(object.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, states);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setSelection(0);
        stateSpinner.setAdapter(dataAdapter2);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateSpinnerItem=parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String UserUID = currentFirebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(UserUID).addValueEventListener(new ValueEventListener() {
                                                                   @Override
                                                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                       User user = dataSnapshot.getValue(User.class);
                                                                       pincodeEditText.setText(user.pincode);
                                                                       cityEditText.setText(user.city);
                                                                       int stateSelected=0;
                                                                       String jsonString= loadJSONFromAsset();
                                                                       try {
                                                                           JSONArray array = new JSONArray(jsonString);
                                                                           for(int i=0;i<array.length();i++) {
                                                                               JSONObject object =(JSONObject) array.get(i);
                                                                               if(user.state.equals(object.getString("name"))) {
                                                                                   stateSelected = i+1;
                                                                                   break;
                                                                               }
                                                                           }
                                                                       } catch (JSONException e) {
                                                                           e.printStackTrace();
                                                                       }
                                                                       stateSpinner.setSelection(stateSelected);
                                                                   }

                                                                   @Override
                                                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                   }
                                                               });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validate()) {
                            category = categoryEditText.getText().toString().toLowerCase().trim();
                            species = speciedEditText.getText().toString().toLowerCase().trim();
                            pincode = pincodeEditText.getText().toString();
                            Intent intent = new Intent(AdoptPetActivity.this, SearchResultActivity.class);
                            intent.putExtra(Constants.CATEGORY, category);
                            intent.putExtra(Constants.SPECIES, species);
                            intent.putExtra(Constants.GENDER, gender);
                            intent.putExtra(Constants.PINCODE, pincode);
                            intent.putExtra(Constants.STATE,stateSpinnerItem);
                            intent.putExtra(Constants.code,"search");
                            startActivity(intent);
                        }
                    }
                });

    }

    boolean validate(){
        if(categoryEditText.getText().toString().equals("")){
            categoryEditText.setError("Enter Pet Category");
            return false;
        }
        else if(stateSpinnerItem.equals("Select State")) {
            ((TextView) stateSpinner.getSelectedView()).setError("Select State");
            return false;
        }
        else if(pincodeEditText.getText().toString().length()==0) {
            pincodeEditText.setError("Enter PinCode");
            return false;
        }

        else if(pincodeEditText.getText().toString().length() != 6) {
            pincodeEditText.setError("Enter valid Pin-Code");
            return false;
        }
        else if(cityEditText.getText().toString().length()==0) {
            cityEditText.setError("Enter City");
            return false;
        }
        return true;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("IndianStates.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }



}
