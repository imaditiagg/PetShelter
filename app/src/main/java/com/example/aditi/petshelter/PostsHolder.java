package com.example.aditi.petshelter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PostsHolder extends RecyclerView.ViewHolder{

    public ImageView petpic ;
    public TextView petname ;
    public TextView petcategory;
    public Button edit;
    public Button sold;
    public Button delete;
    public CardView cardView;
    public View itemview;

    public PostsHolder(View itemview) {

        super(itemview);
        this.itemview = itemView;

        petpic = itemView.findViewById(R.id.petpic);
        petname = itemView.findViewById(R.id.petName);
        petcategory = itemView.findViewById(R.id.petCategory);
        sold = itemview.findViewById(R.id.sell);
        delete = itemview.findViewById(R.id.delete);
        cardView = itemView.findViewById(R.id.card);

    }


}
