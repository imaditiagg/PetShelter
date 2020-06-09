package com.example.aditi.petshelter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ShelterAdapter extends RecyclerView.Adapter<ShelterHolder> {
    LayoutInflater inflater;
    ArrayList<ShelterResult> shelterResults;
    Context context;

    ShelterAdapter(Context context,ArrayList<ShelterResult> shelterResults){
        this.context=context;
        this.shelterResults=shelterResults;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ShelterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View output= inflater.inflate(R.layout.shelter_row_layout,null,false);

        return new ShelterHolder(output);

    }

    @Override
    public void onBindViewHolder(@NonNull ShelterHolder holder, int position) {
        final ShelterResult shelterResult = shelterResults.get(position);
        holder.name.setText(shelterResult.name);
        holder.vicinity.setText(shelterResult.vicinity);
        holder.shelterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context,ShelterDetailsActivity.class);
                intent.putExtra(Constants.PLACE_ID,shelterResult.place_id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shelterResults.size();
    }
}
