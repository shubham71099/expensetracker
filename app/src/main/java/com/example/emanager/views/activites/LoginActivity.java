package com.example.emanager.views.activites;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.emanager.services.APIService;
import com.example.emanager.services.APICallback;
import com.example.emanager.services.LoginResponse;

import java.util.Objects;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private SharedPreferences sharedPreferences;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("auth_preferences", MODE_PRIVATE);

        if(isLoggedIn()) {
            redirectToMainActivity();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvCreateAccount = findViewById(R.id.tvCreateAccount);

        btnLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
            } else if (!EMAIL_PATTERN.matcher(email).matches()) {
                Toast.makeText(LoginActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(LoginActivity.this, "Password should be at least 6 digit long", Toast.LENGTH_SHORT).show();
            } else {
                APIService.getInstance().loginUser(email, password, new APICallback<LoginResponse>() {
                    public void onSuccess(LoginResponse loginResponse) {
                        Log.v("Login Response", String.valueOf(loginResponse.getToken()));
                        saveAuthToken(loginResponse.getToken());
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                        redirectToMainActivity();
                    }
                    public void onError(Throwable error) {
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("error", Objects.requireNonNull(error.getMessage()));
                    }
                });
            }
        });

        tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean isLoggedIn() {
        String token = sharedPreferences.getString("auth_token", null);
        return token != null && !token.isEmpty();
    }

    private void saveAuthToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);
        editor.apply();
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}