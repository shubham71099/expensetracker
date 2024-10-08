package com.example.emanager.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emanager.R;
import com.example.emanager.services.APICallback;
import com.example.emanager.services.APIService;
import com.example.emanager.services.LoginResponse;
import com.example.emanager.services.UserProfileResponse;
import com.example.emanager.views.activites.LoginActivity;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private TextView profileName;
    private TextView profileEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize your views
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        Button logoutButton = view.findViewById(R.id.logout);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", null);

        if (authToken != null) {
            APIService.getInstance().getUserProfile(authToken, new APICallback<UserProfileResponse>() {
                public void onSuccess(UserProfileResponse userProfileResponse) {
                    profileName.setText(userProfileResponse.getName());
                    profileEmail.setText(userProfileResponse.getEmail());
                }

                public void onError(Throwable error) {
                    Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("error", Objects.requireNonNull(error.getMessage()));
                }
            });
        } else {
            // Handle the case where the token is null
            Toast.makeText(requireContext(), "Authentication token is missing", Toast.LENGTH_LONG).show();
        }


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIService.getInstance().logoutUser(requireContext());
                Toast.makeText(requireContext(), "Logout successful !", Toast.LENGTH_LONG).show();

            }
        });

    }
}
