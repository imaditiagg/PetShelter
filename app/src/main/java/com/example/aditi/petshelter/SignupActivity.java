package com.example.aditi.petshelter;

import android.app.Dialog;
import android.content.ContentResolver;
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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    EditText nameEdittext;
    EditText emailEdittext;
    EditText passEdittext;
    EditText confirmpassEdittext;
    DatabaseReference databaseReference;
    AlertDialog dialog;
    private Uri resultUri;
    private FirebaseAuth mAuth;

    com.mikhaellopez.circularimageview.CircularImageView circularImageView;
    Button loginButton;
    StorageReference storageReference ;
    String imageUrl;
    //final String defaultUrl = "https://firebasestorage.googleapis.com/v0/b/sharebook-3edd9.appspot.com/o/profile1.png?alt=media&token=c7346ef0-82e9-4a22-a9ba-f98b077e20f1";
    //final String defaultUrl="https://firebasestorage.googleapis.com/v0/b/sharebook-3edd9.appspot.com/o/profile1.png?alt=media&token=6396d8d2-b3fc-42fd-9792-ba5e5cc8fe1b";
    LottieAnimationView progressBar;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.signup_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Sign up");
        nameEdittext=(EditText)findViewById(R.id.nameeditText);
        emailEdittext=(EditText)findViewById(R.id.EmaileditText);
        passEdittext=(EditText)findViewById(R.id.PasswordeditText);
        mAuth=FirebaseAuth.getInstance();
        loginButton=(Button)findViewById(R.id.Loginbutton);
        circularImageView=findViewById(R.id.addprofilepic);
        progressBar=findViewById(R.id.signup_progress_bar);
        confirmpassEdittext=(EditText)findViewById(R.id.ConfirmPasswordeditText);
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        storageReference= FirebaseStorage.getInstance().getReference();
        setTitle("SignUp");
        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProfilePic();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gotoLogin();//sign up
            }
        });


    }


    public void writeNewUser() {
        final String id = databaseReference.push().getKey();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String UserUID = currentFirebaseUser.getUid();

        if (resultUri != null) {
            Log.i("lala", "img");

            final StorageReference riversRef = storageReference.child("UserImages/" + UserUID + "." + getFileExtension(resultUri));
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
                    //Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    imageUrl = task.getResult().toString();

                    User user = new User(UserUID, nameEdittext.getText().toString(), emailEdittext.getText().toString(),
                            passEdittext.getText().toString(), "", "", "", "", imageUrl);
                    databaseReference.child(UserUID).setValue(user);
                }
            });

        }

        else{

            User user = new User(UserUID, nameEdittext.getText().toString(), emailEdittext.getText().toString(),
                    passEdittext.getText().toString(), "", "", "", "","");
            //Log.i("lalala",user+"");
            databaseReference.child(UserUID).setValue(user);


        }
    }
    public String getFileExtension(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void gotoLogin() {
        if (nameEdittext.getText().toString().equals("")) {
            nameEdittext.setError("Enter Name!");

        } else if (emailEdittext.getText().toString().equals("")) {
            emailEdittext.setError("Enter Email!");
        } else if (!isEmailValid(emailEdittext.getText().toString())) {
            emailEdittext.setError("Enter Valid Email!");
        } else if (passEdittext.getText().toString().equals("")) {
            passEdittext.setError("Enter Password!");
        } else if (confirmpassEdittext.getText().toString().equals("")) {
            confirmpassEdittext.setError("Confirm Password!");
        } else if (!passEdittext.getText().toString().equals(confirmpassEdittext.getText().toString())) {
            confirmpassEdittext.setError("Password does not match!");
        }
        else if(passEdittext.getText().toString().length()<=7){
            passEdittext.setError("Length of password must be greater than or equal to 8 characters");
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(emailEdittext.getText().toString(), passEdittext.getText().toString())
                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful()) {
                                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                if (user!=null) {
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                final Dialog dialog=new Dialog(SignupActivity.this);
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.setContentView(R.layout.email_dialogue);

                                                Button button=dialog.findViewById(R.id.b1);
                                                button.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        dialog.dismiss();
                                                      //  Toast.makeText(getApplicationContext(), "Check your email for verification", Toast.LENGTH_SHORT).show();
                                                        FirebaseAuth.getInstance().signOut();

                                                        Toast.makeText(getApplicationContext(), "Congrats..You have been successfully registered !!", Toast.LENGTH_SHORT).show();

                                                        finish();
                                                    }

                                                });


                                                dialog.show();


                                                dialog.setCancelable(false);
                                            }
                                        }
                                    });

                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                                writeNewUser();
                                progressBar.setVisibility(View.GONE);


                            } else {
                                // If sign in fails, display a message to the user.
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                        }

                    });

        }
    }


    public void LoginText(View view){


        Intent intent = new Intent(this, loginActivity.class);
        startActivity(intent);
        finish();

    }

    public void addProfilePic(){
        Log.i("lalala","add pic method");
        LayoutInflater layoutInflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view= layoutInflater.inflate(R.layout.add_image_dialog_layout,null,false);

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view);

        Button galleryButton= view.findViewById(R.id.galleryButton);


        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("lalala","on click method");
                if( !(ActivityCompat.checkSelfPermission(SignupActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(SignupActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignupActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED )) {

                    String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA};
                    ActivityCompat.requestPermissions(SignupActivity.this, permissions, 1001);
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
                Glide.with(getApplicationContext()).load(getResources().getDrawable(R.drawable.user2)).into(circularImageView);
                resultUri=null;
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


    public void getImageFromGallery(){
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
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
                circularImageView.setImageBitmap(bitmap);
            }
            else{
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static boolean isEmailValid(String email) {

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
