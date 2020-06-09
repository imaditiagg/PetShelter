package com.example.aditi.petshelter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PetHolder extends RecyclerView.ViewHolder {

    public ImageView petpic ;
    public TextView petname ;
    public TextView petCategory;
    public TextView location;
    public CardView cardView;
    public View itemView;


    public PetHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;

        petpic = itemView.findViewById(R.id.petpic);
        petname = itemView.findViewById(R.id.petName);
        petCategory = itemView.findViewById(R.id.petCategory);
        location = itemView.findViewById(R.id.location);
        cardView = itemView.findViewById(R.id.card);
    }
}
