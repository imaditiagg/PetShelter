package com.example.aditi.petshelter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddActivity extends AppCompatActivity {

    Button submitButton;
    EditText category,name,species;
    RadioGroup rg;
    RadioButton male,female;
    ImageView img;
    AlertDialog dialog;
    Uri uri;
    Bitmap bitmap;
    TextView addPhotoTextView;
    DatabaseReference databaseReference;
    Firebase databaseUser;
    String id,imageURL,gender="";
    StorageReference storageReference;
    LottieAnimationView lottieAnimationView;
    FirebaseUser currentFirebaseUser;
    String UserUID;
    String stateOfUser,pincodeOfUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Sell Pet");
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
        lottieAnimationView=findViewById(R.id.addpet_progress_bar);

        category=findViewById(R.id.category);
        name=findViewById(R.id.name);
        species=findViewById(R.id.species);
        submitButton=findViewById(R.id.submitButton);
        rg=findViewById(R.id.radioGroup);
        female=findViewById(R.id.female);
        img=findViewById(R.id.petImage);
        addPhotoTextView=findViewById(R.id.addPhotoTextView);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton=(RadioButton) findViewById(checkedId);
                gender = radioButton.getText().toString();
            }
        });

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UserUID = currentFirebaseUser.getUid();
        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("users");
        databaseReference1.child(UserUID).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                stateOfUser=user.state;
                pincodeOfUser=user.pincode;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    databaseReference = FirebaseDatabase.getInstance().getReference("Pet");
                    id = databaseReference.push().getKey();
                    Log.i("lalala id",id);
                    if (uri != null) {
                        final StorageReference riversRef = storageReference.child("PetImages/" + id + ".jpg");
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
                                    //get current user ID
                                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    String UserUID = currentFirebaseUser.getUid();
                                    Pet pet = new Pet(id, category.getText().toString().toLowerCase().trim(), name.getText().toString().toLowerCase().trim(), species.getText().toString().toLowerCase().trim(), gender, imageURL,pincodeOfUser,false,stateOfUser,UserUID);

                                    databaseUser = new Firebase("https://petshelter-4e61e.firebaseio.com/users/" + UserUID + "/petid");
                                    databaseReference.child(id).setValue(pet);
                                    databaseUser.child(id).setValue(pet);

                                    finish();

                                    lottieAnimationView.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "You have successfully added your book", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });


                    }
                    else{
                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String UserUID = currentFirebaseUser.getUid();
                        Pet pet = new Pet(id, category.getText().toString().toLowerCase().trim(), name.getText().toString().toLowerCase().trim(), species.getText().toString().toLowerCase().trim(), gender, "",pincodeOfUser,false,stateOfUser,UserUID);
                        databaseUser = new Firebase("https://petshelter-4e61e.firebaseio.com/users/" + UserUID + "/petid");
                        databaseReference.child(id).setValue(pet);
                        databaseUser.child(id).setValue(pet);

                            Intent bookIdIntent = new Intent(getApplicationContext(), MainActivity.class);
                            finish();
                            startActivity(bookIdIntent);
                            Toast.makeText(getApplicationContext(), "You have successfully added your book", Toast.LENGTH_SHORT).show();

                    }

                    }
                }

        });
        }



    public void radioGroupClick(View view){
         //TODO
    }

    public void addImage(){
        LayoutInflater layoutInflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view= layoutInflater.inflate(R.layout.add_image_dialog_layout,null,false);

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view);

        Button galleryButton= view.findViewById(R.id.galleryButton);
        // Button cameraButton =view.findViewById(R.id.cameraButton);

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !(ActivityCompat.checkSelfPermission(AddActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(AddActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(AddActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ) ) {

                    String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA};
                    ActivityCompat.requestPermissions(AddActivity.this, permissions, 1001);
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
                addPhotoTextView.setVisibility(View.VISIBLE);
                img.setBackgroundColor(getResources().getColor(R.color.grey));
                img.setImageDrawable(null);
                img.setImageBitmap(null);
                uri=null;

                dialog.cancel();
            }
        });

        dialog = builder.create();
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
                uri = result.getUri();
                // Log.i("lalala uri",resultUri+"");
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        try {
            dialog.cancel();
            if(uri!=null) {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                img.setImageBitmap(bitmap);
                addPhotoTextView.setVisibility(View.GONE);

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

    public boolean validate(){
        if( name.getText().toString().length() == 0 ) {
            name.setError("Enter Name!");
            return false;
        }
        if( category.getText().toString().length() == 0 ) {
            category.setError("Enter Category!");
            return false;
        }
        if( gender.length() ==0 ) {
            female.setError("Select Gender!");
            return false;
        }
        return  true;
    }

}
