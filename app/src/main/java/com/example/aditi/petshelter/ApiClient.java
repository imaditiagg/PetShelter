package com.example.aditi.petshelter;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;

    private static PetService service;

    static Retrofit getInstance(){
        if (retrofit == null) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/maps/api/place/")
                    .addConverterFactory(GsonConverterFactory.create());
            retrofit = builder.build();
        }
        return retrofit;
    }

    static PetService getPetService(){
        if(service == null){
            service = getInstance().create(PetService.class);
        }
        return service;
    }
}
