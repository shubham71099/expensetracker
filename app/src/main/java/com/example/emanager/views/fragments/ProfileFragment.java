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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.emanager.R;
import com.example.emanager.services.APICallback;
import com.example.emanager.services.APIService;
import com.example.emanager.services.UpdateUserProfileRequest;
import com.example.emanager.services.UserProfileResponse;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private EditText profileName;
    private EditText profileEmail;
    private Button editProfileButton;
    private Button updateProfileButton;
    private Button cancelUpdateButton;
    private LinearLayout editButtonsLayout;

    private boolean isEditing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        Button logoutButton = view.findViewById(R.id.logout);
        editProfileButton = view.findViewById(R.id.edit_profile);
        updateProfileButton = view.findViewById(R.id.update_profile);
        cancelUpdateButton = view.findViewById(R.id.cancel_update);
        editButtonsLayout = view.findViewById(R.id.edit_buttons_layout);

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

            // Handle Edit Profile
            editProfileButton.setOnClickListener(v -> toggleEdit(true));

            // Handle Update Profile
            updateProfileButton.setOnClickListener(v -> {
                UpdateUserProfileRequest request = new UpdateUserProfileRequest(
                        profileName.getText().toString(), profileEmail.getText().toString());

                APIService.getInstance().updateUserProfile(authToken, request, new APICallback<UserProfileResponse>() {
                    @Override
                    public void onSuccess(UserProfileResponse response) {
                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        toggleEdit(false);  // Switch back to view mode
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });

            cancelUpdateButton.setOnClickListener(v -> {

                APIService.getInstance().getUserProfile(authToken, new APICallback<UserProfileResponse>() {
                    public void onSuccess(UserProfileResponse userProfileResponse) {
                        profileName.setText(userProfileResponse.getName());
                        profileEmail.setText(userProfileResponse.getEmail());
                        toggleEdit(false);  // Exit edit mode
                    }

                    public void onError(Throwable error) {
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });

            logoutButton.setOnClickListener(v -> {
                APIService.getInstance().logoutUser(requireContext(), authToken);
            });

        }
    }

    private void toggleEdit(boolean enableEditing) {
        isEditing = enableEditing;
        profileName.setEnabled(enableEditing);
        profileEmail.setEnabled(enableEditing);
        profileName.setFocusableInTouchMode(enableEditing);
        profileEmail.setFocusableInTouchMode(enableEditing);

        if (enableEditing) {
            editProfileButton.setVisibility(View.GONE);
            editButtonsLayout.setVisibility(View.VISIBLE);
            profileName.setBackgroundResource(R.drawable.edittext_underline);
            profileEmail.setBackgroundResource(R.drawable.edittext_underline);
        } else {
            editProfileButton.setVisibility(View.VISIBLE);
            editButtonsLayout.setVisibility(View.GONE);
            profileName.setBackgroundResource(0);
            profileEmail.setBackgroundResource(0);
        }
    }
}
