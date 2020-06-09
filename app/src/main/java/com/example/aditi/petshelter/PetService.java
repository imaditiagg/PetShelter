package com.example.aditi.petshelter;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PetService {
    @GET("{path}/json")
    Call<Shelter> getDetails(@Path("path") String path,
                             @Query("location") String location,
                             @Query("radius") int radius,
                             @Query("keyword") String keyword,
                             @Query("key") String key

    );
    @GET("details/json")
    Call<ShelterDetailClass> getDetails(@Query("key") String key,
                                        @Query("placeid") String placeid);

}
