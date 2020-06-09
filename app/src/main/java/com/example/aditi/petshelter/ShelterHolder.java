package com.example.aditi.petshelter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ShelterHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView vicinity;
    public View itemView;
    CardView shelterCard;

    ShelterHolder(View itemView){
        super(itemView);
        this.itemView=itemView;
        name=itemView.findViewById(R.id.shelterName);
        vicinity=itemView.findViewById(R.id.shelterVicinity);
        shelterCard=itemView.findViewById(R.id.shelterCardView);

    }
}
