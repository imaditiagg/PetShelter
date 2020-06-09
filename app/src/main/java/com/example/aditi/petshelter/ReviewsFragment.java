package com.example.aditi.petshelter;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsFragment extends android.support.v4.app.Fragment {
    ArrayList<Reviews> reviews;
    ReviewsAdapter adapter;
    ListView reviewsView;
    ProgressBar progressBar;
    TextView noReviewsView;
    FrameLayout frameLayout;
    String place_id;
    LottieAnimationView lottieAnimationView;



    public ReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle b= getArguments();
        place_id=b.getString(Constants.PLACE_ID);
        View view= inflater.inflate(R.layout.fragment_reviews, container, false);

        progressBar=view.findViewById(R.id.reviewsProgressBar);
        reviewsView=view.findViewById(R.id.reviewsList);
        frameLayout=view.findViewById(R.id.reviews_root_layout);
        ViewCompat.setNestedScrollingEnabled(reviewsView,true);
        noReviewsView=view.findViewById(R.id.no_reviews);
        lottieAnimationView=view.findViewById(R.id.empty_animation);
        reviews=new ArrayList<>();
        adapter=new ReviewsAdapter(getContext(),reviews);
        reviewsView.setAdapter(adapter);
        reviewsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Reviews review = reviews.get(position);
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
                View v = inflater.inflate(R.layout.review_dialog_view,null,false);
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setView(v);
                builder.setCancelable(true);
                TextView name = v.findViewById(R.id.userName);
                TextView content =v.findViewById(R.id.userReview);
                ImageView imageView =v.findViewById(R.id.image);
                TextView rating=v.findViewById(R.id.Rating);
                name.setText(review.author_name);
                content.setText(review.text);
                rating.setText(rating.getText()+String.valueOf(review.rating));
                if(review.profile_photo_url!=null)
                       Picasso.get().load(review.profile_photo_url).into(imageView);

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        fetchReviews();
        return view;
    }
    public void fetchReviews(){

            progressBar.setVisibility(View.VISIBLE);
            reviewsView.setVisibility(View.GONE);
            lottieAnimationView.setVisibility(View.GONE);
            noReviewsView.setVisibility(View.GONE);

        Call<ShelterDetailClass> call = ApiClient.getPetService().getDetails(getResources().getString(R.string.google_places_key),place_id);
        call.enqueue(new Callback<ShelterDetailClass>() {
            @Override
            public void onResponse(Call<ShelterDetailClass> call, Response<ShelterDetailClass> response) {
                try {
                    ShelterDetailClass shelterDetailClass = response.body();
                    if (shelterDetailClass.result != null) {
                        Log.i("lalala 1", shelterDetailClass + "");
                        ArrayList<Reviews> review = shelterDetailClass.result.reviews;
                        Log.i("lalala 2", shelterDetailClass.result.name + shelterDetailClass.result.formatted_address);

                        if (reviews != null) {
                            Log.i("lalala reviews", reviews + "");
                            for (int i = 0; i < review.size(); i++) {
                                reviews.add(review.get(i));

                            }
                            adapter.notifyDataSetChanged();
                            noReviewsView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            reviewsView.setVisibility(View.VISIBLE);
                            lottieAnimationView.setVisibility(View.GONE);


                        } else {

                            progressBar.setVisibility(View.GONE);
                            reviewsView.setVisibility(View.GONE);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            noReviewsView.setVisibility(View.VISIBLE);


                        }
                    } else {

                        progressBar.setVisibility(View.GONE);
                        reviewsView.setVisibility(View.GONE);
                        lottieAnimationView.setVisibility(View.VISIBLE);
                        noReviewsView.setVisibility(View.VISIBLE);


                    }
                }
                catch (Exception e){
                    progressBar.setVisibility(View.GONE);
                    reviewsView.setVisibility(View.GONE);
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    noReviewsView.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onFailure(Call<ShelterDetailClass> call, Throwable t) {

            }
        });

    }

}
