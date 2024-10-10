package com.example.emanager.views.activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.emanager.R;
import com.example.emanager.services.APICallback;
import com.example.emanager.services.APIService;
import com.example.emanager.services.RegisterResponse;

import java.util.Objects;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button btnRegister;
    private TextView tvAlreadyRegistered;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvAlreadyRegistered = findViewById(R.id.tvAlreadyRegistered);

        btnRegister.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            // Form validation
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(RegisterActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(email)) {
                Toast.makeText(RegisterActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
            } else if (!EMAIL_PATTERN.matcher(email).matches()) {
                Toast.makeText(RegisterActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password should be at least 6 digit long", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Please enter confirm password", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                APIService.getInstance().registerUser(email, password, name, new APICallback<RegisterResponse>() {
                    public void onSuccess(RegisterResponse registerResponse) {
                        Log.v("Register Response", String.valueOf(registerResponse.getMessage()));
                        Toast.makeText(RegisterActivity.this, "Registration successful, please login now.", Toast.LENGTH_LONG).show();
                        redirectToLoginActivity();
                    }
                    public void onError(Throwable error) {
                        Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("error", Objects.requireNonNull(error.getMessage()));
                    }
                });
            }
        });

        tvAlreadyRegistered.setOnClickListener(v -> {
            redirectToLoginActivity();
        });
    }

    private void redirectToLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}