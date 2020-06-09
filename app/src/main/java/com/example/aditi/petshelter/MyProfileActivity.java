package com.example.aditi.petshelter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MyProfileActivity extends AppCompatActivity {
    EditText name;
    EditText email;
    EditText contact;
    EditText pin;
    EditText city;
    EditText pass;
    ImageView userImage;
    Spinner stateSpinner;
    String stateSpinnerItem;
    Button saveButton;
    android.support.v7.app.AlertDialog dialog;
    private FirebaseAuth mAuth;
    LottieAnimationView progressBar;
    StorageReference storageReference;
    TextView nameView;
    LinearLayout layout;
    private Uri resultUri;
    Bitmap bitmap;
    boolean var;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    String imageURL,oldURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarprofile);
        setSupportActionBar(toolbar);
        setTitle("My Profile");
        Firebase.setAndroidContext(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {
                                                     finish();
                                                 }
                                             }

        );
        storageReference= FirebaseStorage.getInstance().getReference();
        name = (EditText) findViewById(R.id.editname);
        email = findViewById(R.id.editemail);
        contact = findViewById(R.id.editcontact);

        pin = findViewById(R.id.editpin);
        city = findViewById(R.id.editcity);
        pass = findViewById(R.id.editpass);
        stateSpinner=findViewById(R.id.profile_state_spinner);
        nameView=findViewById(R.id.name);
        userImage=findViewById(R.id.profile);
        layout=findViewById(R.id.profileLayout);
        progressBar=findViewById(R.id.profile_progress_bar);
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
                progressBar.setVisibility(View.GONE);
                name.setText(user.name);
                nameView.setText(user.name);
                email.setText(user.email);
                contact.setText(user.phone);
                String state = user.state;
                oldURL=user.Imageurl;


                if(!user.Imageurl.equals("")) {

                    Glide.with(getApplicationContext()).load(user.Imageurl).into(userImage);
                }
                else{
                    //Log.i("lalala img",user.Imageurl);
                    Glide.with(getApplicationContext()).load(getResources().getDrawable(R.drawable.profile1)).into(userImage);
                }

                int stateSelected=0;
                String jsonString= loadJSONFromAsset();
                try {
                    JSONArray array = new JSONArray(jsonString);
                    for(int i=0;i<array.length();i++) {
                        JSONObject object =(JSONObject) array.get(i);
                        if(state.equals(object.getString("name"))) {
                            stateSelected = i+1;
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                stateSpinner.setSelection(stateSelected);
                pin.setText(user.pincode);
                city.setText(user.city);
                pass.setText(user.password);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        saveButton=findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    progressBar.setVisibility(View.VISIBLE);

                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    final String UserUID = currentFirebaseUser.getUid();
                    if (resultUri != null) {
                        final StorageReference riversRef = storageReference.child("UserImages/" + UserUID + ".jpg");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                        byte[] data = baos.toByteArray();
                        UploadTask uploadTask = riversRef.putBytes(data);

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return riversRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    imageURL = task.getResult().toString();

                                    User user = new User(UserUID, name.getText().toString(), email.getText().toString(), pass.getText().toString(), city.getText().toString(),
                                            pin.getText().toString(), stateSpinnerItem, contact.getText().toString(), imageURL);
                                    databaseReference.child(UserUID).setValue(user);


                                    //change in Pet
                                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                                    Query q = mRef.child("Pet").orderByChild("userID").equalTo(UserUID);
                                    q.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                Pet b = dataSnapshot1.getValue(Pet.class);

                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Pet").child(b.id);
                                                Pet pet = new Pet(b.id, b.category, b.name, b.species, b.gender, b.imageURL, pin.getText().toString(), b.adopted, stateSpinnerItem, b.userID);
                                                databaseReference.setValue(pet);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    //now change in Pet in user as well
                                    DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                                    Query query = mFirebaseDatabaseReference.child("Pet").orderByChild("userID").equalTo(UserUID);
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                Pet b = dataSnapshot1.getValue(Pet.class);
                                                Firebase databaseUserBook = new Firebase("https://petshelter-4e61e.firebaseio.com/users/" + UserUID + "/petid");
                                                databaseUserBook.child(b.id).setValue(b);
                                               // Log.i("lalalala", "pet in user changed");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    Toast.makeText(getApplicationContext(), "Changes Saved", Toast.LENGTH_SHORT).show();

                                    MyProfileActivity.this.finish();


                                }
                            }
                            });
                        }

                                 else {
                        //change in user
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(UserUID);
                        User user = new User(UserUID, name.getText().toString(), email.getText().toString(), pass.getText().toString(), city.getText().toString(),
                                pin.getText().toString(), stateSpinnerItem, contact.getText().toString(), oldURL);
                        databaseReference.setValue(user);

                        //change in Pet
                        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                        Query q = mRef.child("Pet").orderByChild("userID").equalTo(UserUID);
                        q.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    Pet b = dataSnapshot1.getValue(Pet.class);

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Pet").child(b.id);
                                    Pet pet = new Pet(b.id, b.category, b.name, b.species, b.gender, b.imageURL, pin.getText().toString(), b.adopted, stateSpinnerItem, b.userID);
                                    databaseReference.setValue(pet);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });




                        //now change in Pet in user as well
                        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                        Query query = mFirebaseDatabaseReference.child("Pet").orderByChild("userID");
                        query.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    Pet b = dataSnapshot1.getValue(Pet.class);
                                    if(b.userID.equals(UserUID)) {
                                        Firebase databaseUserBook = new Firebase("https://petshelter-4e61e.firebaseio.com/users/" + UserUID + "/petid");
                                        databaseUserBook.child(b.id).setValue(b);
                                        Log.i("lalalala", "pet in user changed");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Toast.makeText(getApplicationContext(), "Changes Saved", Toast.LENGTH_SHORT).show();


                        MyProfileActivity.this.finish();

                    }
                }
            }
        });



        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });


        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProfilePic();
            }
        });

    }
    public void addProfilePic(){

            LayoutInflater layoutInflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            View view= layoutInflater.inflate(R.layout.add_image_dialog_layout,null,false);

            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setView(view);
            Button galleryButton= view.findViewById(R.id.galleryButton);
            galleryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( !(ActivityCompat.checkSelfPermission(MyProfileActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MyProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MyProfileActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED )) {

                        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA};
                        ActivityCompat.requestPermissions(MyProfileActivity.this, permissions, 1001);
                    }
                    else {
                        getImageFromGallery();
                    }
                }
            });
            Button galleryButton2 = view.findViewById(R.id.galleryButton2);
            galleryButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Glide.with(getApplicationContext()).load(getResources().getDrawable(R.drawable.profile1)).into(userImage);
                    oldURL="";
                    dialog.cancel();
                }
            });
            dialog=builder.create();
            dialog.show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1001){
            int readPermission = grantResults[0];
            int writePermission = grantResults[1];
            int cameraPermission = grantResults[2];

            if( readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED && cameraPermission == PackageManager.PERMISSION_GRANTED){
                getImageFromGallery();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                resultUri = result.getUri();
                // Log.i("lalala uri",resultUri+"");
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        try {
            dialog.cancel();
            if(resultUri!=null) {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                userImage.setImageBitmap(bitmap);

                //Log.i("lalala var",var+"");
            }
            else{
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void getImageFromGallery(){
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);

    }



    public void pass(View view)
    {
        changePassword(); }

    public void changePassword(){
        LayoutInflater layoutInflater = (LayoutInflater) MyProfileActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_forget_pass, null, false);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MyProfileActivity.this);
        builder.setView(view);

        final EditText Email1forResetPass = (EditText) view.findViewById(R.id.EmaileditText);

        Button forgetPass = (Button) view.findViewById(R.id.button);
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Email1forResetPass.getText().toString().length()!=0) {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.sendPasswordResetEmail(Email1forResetPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Password reset mail sent on entered email", Toast.LENGTH_SHORT).show();
                                dialog.cancel();

                            } else {
                                Toast.makeText(getApplicationContext(), "Entered Email is not registered", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
                else{
                    Toast.makeText(MyProfileActivity.this,"Enter email",Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog = builder.create();
        dialog.show();

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            /**
             * It gets into the above IF-BLOCK if anywhere the screen is touched.
             */

            View v = getCurrentFocus();
            if ( v instanceof EditText) {


                /**
                 * Now, it gets into the above IF-BLOCK if an EditText is already in focus, and you tap somewhere else
                 * to take the focus away from that particular EditText. It could have 2 cases after tapping:
                 * 1. No EditText has focus
                 * 2. Focus is just shifted to the other EditText
                 */

                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
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
    public boolean validate(){
        if(name.getText().toString().length()==0) {
            name.setError("Enter Name");
            return false;
        }
        else if(email.getText().toString().length()==0) {
            email.setError("Enter Email");
            return false;
        }
        else if(contact.getText().toString().length()==0 ) {
            contact.setError("Enter Contact");
            return false;
        }
        else if(contact.getText().toString().length()!= 10){
            contact.setError("Please Enter 10 Digit Mobile Number");
            return false;
        }

        else if(stateSpinnerItem.equals("Select State")) {
            ((TextView) stateSpinner.getSelectedView()).setError("Select State");
            return false;
        }
        else if(city.getText().toString().length()==0) {
            city.setError("Enter City");
            return false;
        }
        else if(pin.getText().toString().length()==0) {
            pin.setError("Enter PinCode");
            return false;
        }

        else if(pin.getText().toString().length() != 6) {
            pin.setError("Enter valid Pin-Code");
            return false;
        }

        return true;


    }

}
