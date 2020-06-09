package com.example.aditi.petshelter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    PetAdapter adapter;
    ArrayList<Pet> pets;
    Firebase databaseReferenceUser;
    TextView textView;
    DatabaseReference databaseReference;
    User user;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);


        recyclerView=findViewById(R.id.petRecyclerView);
        pets=new ArrayList<>();
        adapter = new PetAdapter(pets,this);
        recyclerView.setAdapter(adapter);
        textView=findViewById(R.id.viewAll);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,SearchResultActivity.class);
                intent.putExtra(Constants.code,"view");
                startActivity(intent);
            }
        });
        //recyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
       /// GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        databaseReferenceUser=new Firebase("https://petshelter-4e61e.firebaseio.com/Pet");
        Query query = databaseReferenceUser.orderByChild("adopted").equalTo(false);
        query.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pets.clear();
                //Log.i("lala snap",dataSnapshot+"");

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    Pet newPet;
                    newPet=(Pet) dataSnapshot1.getValue(Pet.class);
                    pets.add(newPet);


                }

                Log.i("lalala pets",pets+"");
                adapter.notifyDataSetChanged();
               // lottieAnimationView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent =new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);*/
                check();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.rescuePet) {
            Intent intent=new Intent(MainActivity.this,LocationActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.addPet) {
            Intent intent=new Intent(MainActivity.this,AddActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.myProfile) {
            Intent intent=new Intent(MainActivity.this,MyProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.myPet) {
            Intent intent=new Intent(MainActivity.this,MyPetsActivity.class);
            startActivity(intent);

        } else if (id == R.id.adoptPet) {
            Intent intent=new Intent(MainActivity.this,AdoptPetActivity.class);
            startActivity(intent);

        } else if (id == R.id.about) {
            Intent intent=new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);

        }
        else if(id==R.id.deleteAccount){
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(MainActivity.this.LAYOUT_INFLATER_SERVICE);

            final View output = inflater.inflate(R.layout.delete_dialoge, null, false);
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(output);
            final AlertDialog dialog1 = builder.create();
            dialog1.show();
            Button  yes= output.findViewById(R.id.b1);
            Button no = output.findViewById(R.id.b2);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();

                    LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(MainActivity.this.LAYOUT_INFLATER_SERVICE);

                    final View output = inflater.inflate(R.layout.delete_user_auth, null, false);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setView(output);
                    final AlertDialog dialog2 = builder.create();
                    dialog2.show();

                    final EditText Emai1 = (EditText) output.findViewById(R.id.EmaileditText);
                    final EditText Pass= output.findViewById(R.id.PasseditText);
                    Button button3 = (Button) output.findViewById(R.id.button);

                    button3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Emai1.getText().toString().length() != 0 && Pass.getText().toString().length() != 0) {
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(Emai1.getText().toString(), Pass.getText().toString());
                                // Prompt the user to re-provide their sign-in credentials
                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                user.delete()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getApplicationContext(), "You have successfully deleted your account", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(MainActivity.this, loginActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }
                            else{
                                Toast.makeText(MainActivity.this,"Enter email and password",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog1.dismiss();

                }

            });



        }
        else if(id==R.id.logout){
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(MainActivity.this.LAYOUT_INFLATER_SERVICE);

            final View output = inflater.inflate(R.layout.logout_alert, null, false);
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(output);
            final AlertDialog dialog1 = builder.create();
            dialog1.show();
            Button  yes= output.findViewById(R.id.b1);
            Button no = output.findViewById(R.id.b2);

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent=new Intent(MainActivity.this,loginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                }
            });



        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("onStart","lalala");
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String UserUID = currentFirebaseUser.getUid();

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
    }

    public void check(){
        if (user.city.equals("") || user.pincode.equals("") || user.state.equals("") || user.phone.equals("")) {
            //Log.i("profile","lalala");
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(MainActivity.this.LAYOUT_INFLATER_SERVICE);

            final View output = inflater.inflate(R.layout.profile_alert, null, false);

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(output);
            final AlertDialog dialog = builder.create();
            dialog.show();


            Button button = (Button) output.findViewById(R.id.button1);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent1 = new Intent(MainActivity.this, MyProfileActivity.class);
                    //intent1.putExtra(Constants.CODE,"add");
                    dialog.dismiss();
                    startActivity(intent1);
                }
            });

            Button button1 = (Button) output.findViewById(R.id.button2);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });

            builder.setCancelable(false);


        }
        else {
            //Log.i("sell","lalala");
            Intent intent1 = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent1);
        }


    }


}
