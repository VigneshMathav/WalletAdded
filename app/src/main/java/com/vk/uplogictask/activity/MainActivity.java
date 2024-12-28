package com.vk.uplogictask.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.vk.uplogictask.fragment.CardFragment;
import com.vk.uplogictask.fragment.HomeFragment;
import com.vk.uplogictask.fragment.ProfileFragment;
import com.vk.uplogictask.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottom_cards);


    }

    CardFragment cardFragment = new CardFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    HomeFragment homeFragment = new HomeFragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.bottom_home) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, homeFragment)
                    .commit();
            return true;
        } else if (id == R.id.bottom_cards) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, cardFragment)
                    .commit();
            return true;
        } else if (id == R.id.bottom_profile) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, profileFragment)
                    .commit();
            return true;
        }
        return false;

    }
}