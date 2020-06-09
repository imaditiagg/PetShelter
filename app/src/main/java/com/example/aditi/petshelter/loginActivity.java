package com.example.aditi.petshelter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class loginActivity extends AppCompatActivity {

    TextView SignUpTextView;
    EditText emailEditText;
    EditText passEditText;
    private FirebaseAuth mAuth;
    // SignInButton GoogleSignInButton;
    Button GoogleSignInButton;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG="Main Activity";
    Toolbar toolbar;
    TextView forgetPass;
    GoogleSignInClient mGoogleSignInClient;
    android.support.v7.app.AlertDialog dialog;

    LottieAnimationView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Sign in");
        FirebaseApp.initializeApp(this);
        SignUpTextView = (TextView) findViewById(R.id.SignUptextView);
        emailEditText = (EditText) findViewById(R.id.EmaileditText);
        passEditText = (EditText) findViewById(R.id.PasswordeditText);
        //GoogleSignInButton = findViewById(R.id.GoogleSignIn);
        forgetPass = (TextView) findViewById(R.id.textView4);
        progressBar =findViewById(R.id.signin_progress_bar);

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(HomeActivity.this,ForgetPass.class));
                LayoutInflater layoutInflater = (LayoutInflater) loginActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.activity_forget_pass, null, false);

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(loginActivity.this);
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
                        else
                            Toast.makeText(loginActivity.this,"Enter email",Toast.LENGTH_SHORT).show();

                    }
                });

                dialog = builder.create();
                dialog.show();


            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getEmail());
                    //Toast.makeText(getApplicationContext(), "Successfully signed in with " + user.getUid(), Toast.LENGTH_SHORT).show();

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                }
            }
        };


        if (mAuth.getCurrentUser() != null) {
            logIn(); //already logged in
        }
        setTitle("Sign In");
    }

    public void SignUpText(View view){
        Intent intent=new Intent(this,SignupActivity.class);
        startActivity(intent);

    }
    public void LoginClicked(View view){
        if (emailEditText.getText().toString().equals("")){
            emailEditText.setError("Enter Email!");
        }
        else if(passEditText.getText().toString().equals("")){
            passEditText.setError("Enter password!");
        }
        else {
            progressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passEditText.getText().toString())
                    .addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (mAuth.getCurrentUser().isEmailVerified())
                                {
                                    logIn();
                                    progressBar.setVisibility(View.GONE);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Verify email first",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                            } else {
                                Toast.makeText(getApplicationContext(),"Either E-mail or Password is incorrect",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    });
        }

    }
    public void logIn(){
        if (mAuth.getCurrentUser()!=null&&mAuth.getCurrentUser().isEmailVerified()){
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }




    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            /**
             * It gets into the above IF-BLOCK if anywhere the screen is touched.
             */

            View v = getCurrentFocus();
            if (v instanceof EditText) {


                /**
                 * Now, it gets into the above IF-BLOCK if an EditText is already in focus, and you tap somewhere else
                 * to take the focus away from that particular EditText. It could have 2 cases after tapping:
                 * 1. No EditText has focus
                 * 2. Focus is just shifted to the other EditText
                 */

                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    }
