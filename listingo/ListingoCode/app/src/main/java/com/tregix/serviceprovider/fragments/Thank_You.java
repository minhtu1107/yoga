package com.tregix.serviceprovider.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;
import com.tregix.serviceprovider.R;
import com.tregix.serviceprovider.activities.NavigationDrawerActivity;


public class Thank_You extends BaseFragment {
    
    String customerID;
    
    AdView mAdView;
    FrameLayout banner_adView;
    Button order_status_btn, continue_shopping_btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.thank_you, container, false);

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.order_placed));
    
        // Get the customersID and defaultAddressID from SharedPreferences
        customerID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");

        
        // Binding Layout Views
        order_status_btn = (Button) rootView.findViewById(R.id.order_status_btn);

        order_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAcitivty( null,NavigationDrawerActivity.class);
            }
        });

        return rootView;
    }
    

}

