package com.example.aditi.petshelter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.net.URL;
import java.util.ArrayList;

public class PetAdapter extends RecyclerView.Adapter<PetHolder> {

    LayoutInflater inflater;
    ArrayList<Pet> items;
    Context context;

    public PetAdapter(ArrayList<Pet> items, Context context) {

        this.items = items;
        this.context = context;

    }

    @NonNull
    @Override
    public PetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View output= inflater.inflate(R.layout.pet_item,null,false);

        return new PetHolder(output);
    }

    @Override
    public void onBindViewHolder(@NonNull PetHolder holder, final int position) {

        final Pet pet = items.get(position);
        holder.petname.setText(pet.name);
        holder.petCategory.setText(pet.category);
        holder.location.setText(pet.state);
        String url= pet.imageURL;

        if(!url.equals(""))
           Glide.with(context).load(url).into(holder.petpic);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,PetDetailActivity.class);
                intent.putExtra(Constants.PETID,pet.id);
                intent.putExtra(Constants.USERID,pet.userID);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
