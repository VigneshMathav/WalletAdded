package com.vk.uplogictask.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.uplogictask.activity.LoginActivity;
import com.vk.uplogictask.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;


    public ProfileFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                SharedPreferences preferences = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("isUserLogin");
                editor.commit();
                startActivity(intent);
                requireActivity().finish();
            }
        });
        return binding.getRoot();
    }

}

