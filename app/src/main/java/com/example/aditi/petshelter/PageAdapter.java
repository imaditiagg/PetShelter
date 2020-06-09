package com.example.aditi.petshelter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class PageAdapter extends FragmentPagerAdapter{
    String place_id;


    public PageAdapter(FragmentManager fm,String id) {

        super(fm);
        place_id=id;

    }
    @Override
    public Fragment getItem(int position) {
        Bundle b =new Bundle();
        b.putString(Constants.PLACE_ID,place_id);
        if(position==0){

          DetailsFragment fragment1 =new DetailsFragment();
          fragment1.setArguments(b);
          return fragment1;
          }
        else if(position==1){

            ReviewsFragment fragment2 =new ReviewsFragment();
            fragment2.setArguments(b);
            return fragment2;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
