package com.example.billmate;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.billmate.databinding.FragmentAccountBinding;
import com.example.billmate.databinding.FragmentCustomersBinding;
import com.google.firebase.auth.FirebaseAuth;


public class AccountFragment extends Fragment {
    FragmentAccountBinding binding;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(getLayoutInflater(),container,false);
        auth = FirebaseAuth.getInstance();

        // Sign out button logic
        binding.signOutBtn.setOnClickListener(view -> {
            auth.signOut();
            Intent intent = new Intent(getContext(), Registration.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
        });

        return binding.getRoot();
    }
}