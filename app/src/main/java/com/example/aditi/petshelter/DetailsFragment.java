package com.example.aditi.petshelter;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailsFragment extends android.support.v4.app.Fragment {

    TextView name,address,contact,rating;
    String place_id;
    ProgressBar progressBar;
    ImageView locationIcon;
    Button direction,callButton;
    ImageView star;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_details,container,false);
        Bundle b= getArguments();
        place_id=b.getString(Constants.PLACE_ID);

        name=view.findViewById(R.id.name);
        address=view.findViewById(R.id.address);
        contact=view.findViewById(R.id.contact);
        progressBar=view.findViewById(R.id.detailsProgressBar);
        rating=view.findViewById(R.id.rating);
        locationIcon=view.findViewById(R.id.locationicon);
        direction=view.findViewById(R.id.directions);
        star=view.findViewById(R.id.star);
        callButton=view.findViewById(R.id.call_owner_button);
        fetchDetails();
        return view;
    }

    void fetchDetails(){
        progressBar.setVisibility(View.VISIBLE);
        Call<ShelterDetailClass> call = ApiClient.getPetService().getDetails(getResources().getString(R.string.google_places_key),place_id);
        call.enqueue(new Callback<ShelterDetailClass>() {
            @Override
            public void onResponse(Call<ShelterDetailClass> call, Response<ShelterDetailClass> response) {
                try {
                    final ShelterDetailClass shelterDetailClass = response.body();

                    if (shelterDetailClass.result != null) {
                        progressBar.setVisibility(View.GONE);
                        star.setVisibility(View.VISIBLE);
                        direction.setVisibility(View.VISIBLE);
                        locationIcon.setVisibility(View.VISIBLE);

                       if(shelterDetailClass.result.name!=null)
                       name.setText(shelterDetailClass.result.name);
                       if(shelterDetailClass.result.formatted_address!=null)
                       address.setText(shelterDetailClass.result.formatted_address);
                       if(shelterDetailClass.result.formatted_phone_number!=null) {
                           contact.setText("Contact: " + shelterDetailClass.result.formatted_phone_number);
                           callButton.setVisibility(View.VISIBLE);
                           callButton.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent intent = new Intent();
                                   intent.setAction(Intent.ACTION_DIAL);
                                   String no= "tel:"+shelterDetailClass.result.formatted_phone_number;
                                   Uri uri = Uri.parse(no);
                                   intent.setData(uri);
                                   startActivity(intent);
                               }
                           });

                       }
                       rating.setText(shelterDetailClass.result.rating + "/10");
                       if(shelterDetailClass.result.url!=null)
                       {
                           direction.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent intent = new Intent();
                                   intent.setAction(Intent.ACTION_VIEW);
                                   Uri uri = Uri.parse(shelterDetailClass.result.url);
                                   intent.setData(uri);
                                   startActivity(intent);
                               }
                           });
                       }



                        } else {

                        Toast.makeText(getContext(),"Not found",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        locationIcon.setVisibility(View.GONE);
                        name.setVisibility(View.GONE);
                        direction.setVisibility(View.GONE);
                        star.setVisibility(View.GONE);
                        callButton.setVisibility(View.GONE);


                        }
                    }

                catch (Exception e){
                    Toast.makeText(getContext(),"Not found",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    locationIcon.setVisibility(View.GONE);
                    name.setVisibility(View.GONE);
                    direction.setVisibility(View.GONE);
                    star.setVisibility(View.GONE);
                    callButton.setVisibility(View.GONE);
                }

            }
            @Override
            public void onFailure(Call<ShelterDetailClass> call, Throwable t) {

            }
        });

    }

}
