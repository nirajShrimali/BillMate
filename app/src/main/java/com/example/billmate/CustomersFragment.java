package com.example.billmate;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.billmate.databinding.FragmentCustomersBinding;
import com.google.firebase.auth.FirebaseAuth;

public class CustomersFragment extends Fragment {
    FragmentCustomersBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomersBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}
