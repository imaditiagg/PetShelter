package com.example.aditi.petshelter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewsAdapter extends ArrayAdapter {
    ArrayList<Reviews> reviews;
    Context mContext;
    View view;
    LayoutInflater inflater;
    public ReviewsAdapter(@NonNull Context context, ArrayList<Reviews> reviews) {
        super(context, 0,reviews);
        this.reviews=reviews;
        mContext=context;
        inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

       view=convertView;
       if(view==null){
           view = inflater.inflate(R.layout.review_row_layout, parent, false);
           TextView t1= view.findViewById(R.id.userName);
           TextView t2= view.findViewById(R.id.userReview);
           TextView t3 = view.findViewById(R.id.rating);
           ImageView imageView=view.findViewById(R.id.image);
           ReviewHolder holder = new ReviewHolder();
           holder.name= t1;
           holder.review=t2;
           holder.rating=t3;
           holder.imageView=imageView;

           view.setTag(holder);
       }
       final Reviews review = reviews.get(position);
       ReviewHolder holder= (ReviewHolder) view.getTag();
       holder.name.setText(review.author_name); //set author name

       int length = review.text.length();
       SpannableString text = new SpannableString(" .... Read More");
       if(length>60 && (Math.abs(length-60))>10){
           SpannableStringBuilder spannableStringBuilder=new SpannableStringBuilder(review.text,0,45);
           spannableStringBuilder.append(text);
           holder.review.setText(spannableStringBuilder.toString());
       }
       else {
           holder.review.setText(review.text); //set content of review
       }

       holder.rating.setText("Rating : " + String.valueOf(review.rating));
       if(review.profile_photo_url!=null)
          Picasso.get().load(review.profile_photo_url).into(holder.imageView);


       return view;
    }
}
