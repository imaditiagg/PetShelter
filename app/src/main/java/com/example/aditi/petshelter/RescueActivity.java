package com.example.aditi.petshelter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RescueActivity extends AppCompatActivity  {
    RecyclerView recyclerView;
    TextView textView;
    LottieAnimationView progressBar;
    ShelterAdapter shelterAdapter;
    ArrayList<ShelterResult> shelterResults;
    Double latitude,longitude;
    FrameLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue);
        Toolbar toolbar = findViewById(R.id.rescue_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {
                                                     finish();
                                                 }
                                             }

        );
        setTitle("Rescue Pet");
        Intent intent=getIntent();
        latitude = intent.getDoubleExtra("lat",0);
        longitude = intent.getDoubleExtra("long",0);
        shelterResults=new ArrayList<>();
        shelterAdapter=new ShelterAdapter(this,shelterResults);
        recyclerView=findViewById(R.id.petShelter_recyclerView);
        progressBar=findViewById(R.id.activity_rescue_progress_bar);
        textView=findViewById(R.id.shelter_not_found_view);
        layout=findViewById(R.id.rescue_layout);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager =new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        //GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(shelterAdapter);
        search();
    }
    void search(){
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        StringBuilder stringBuilder=new StringBuilder("nearbysearch");
        String url = stringBuilder.toString();
        //Log.i("url",url);

        Call<Shelter> call = ApiClient.getPetService().getDetails(url,latitude+","+longitude,30000,"animalshelter",getResources().getString(R.string.google_places_key));
       // Log.i("lala call",call+"");
        call.enqueue(new Callback<Shelter>() {
            @Override
            public void onResponse(Call<Shelter> call, Response<Shelter> response) {
               // Log.i("lalala result",response+"");
                Shelter shelters= response.body();

                ArrayList<ShelterResult> s = shelters.results;
                //Log.i("lalala result1",s.size()+"");
                for(int i=0;i<s.size();i++){
                   shelterResults.add(s.get(i));
                }
                if(!shelterResults.isEmpty()) {
                    shelterAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    LottieAnimationView animationView = new LottieAnimationView(RescueActivity.this);
                    animationView.setAnimation(R.raw.empty_list);
                    layout.addView(animationView);
                    animationView.playAnimation();
                }


            }

            @Override
            public void onFailure(Call<Shelter> call, Throwable t) {

            }
        });


    }
}
