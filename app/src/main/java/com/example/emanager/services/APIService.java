package com.example.emanager.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import com.example.emanager.models.Transaction;
import com.example.emanager.views.activites.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;


public class APIService {
    private static final String BASE_URL = "http://10.0.2.2:5000";

//    private static final String BASE_URL = "https://expensetracker-wra9gxiy.b4a.run";
    private static APIService instance;
    private final APIInterface apiInterface;

    private APIService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(APIInterface.class);

    }

    public static synchronized APIService getInstance() {
        if (instance == null) {
            instance = new APIService();
        }
        return instance;
    }

    private String parseErrorResponse(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                return jObjError.getString("message");
            } else {
                return "Unknown error occurred";
            }
        } catch (JSONException | IOException e) {
            Log.e("APIService", "Error parsing error response", e);
            return "Error parsing server response";
        }
    }

    public void registerUser(String email, String password, String name, final APICallback<RegisterResponse> callback) {
        RegisterRequest registerRequest = new RegisterRequest(email, password, name);
        Call<RegisterResponse> call = apiInterface.registerUser(registerRequest);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = parseErrorResponse(response);
                    callback.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void loginUser(String email, String password, final APICallback<LoginResponse> callback) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiInterface.loginUser(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Log.v("Info", String.valueOf(response.body()));
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = parseErrorResponse(response);
                    callback.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void addTransaction(String authToken, Transaction transaction, final APICallback<Transaction> callback) {
        Call<Transaction> call = apiInterface.addTransaction(authToken, transaction);
        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(@NonNull Call<Transaction> call, @NonNull Response<Transaction> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = parseErrorResponse(response);
                    callback.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Transaction> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void getTransactions(String authToken, Date startDate, Date endDate, final APICallback<List<Transaction>> callback) {
        Call<List<Transaction>> call = apiInterface.getTransactions(authToken, startDate, endDate);
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(@NonNull Call<List<Transaction>> call, @NonNull Response<List<Transaction>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = parseErrorResponse(response);
                    callback.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Transaction>> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void deleteTransaction(String authToken, String transactionId, final APICallback<Void> callback) {
        Call<Void> call = apiInterface.deleteTransaction(authToken, transactionId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    String errorMessage = parseErrorResponse(response);
                    callback.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }


    public void getUserProfile(String authToken, final APICallback<UserProfileResponse> callback) {
        Call<UserProfileResponse> call = apiInterface.getUserProfile(authToken);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = parseErrorResponse(response);
                    callback.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }
    public void updateUserProfile(String authToken, UpdateUserProfileRequest updateUserProfileRequest, final APICallback<UserProfileResponse> callback) {
        Call<UserProfileResponse> call = apiInterface.updateUserProfile(authToken, updateUserProfileRequest);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = parseErrorResponse(response);
                    callback.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }


    public void logoutUser(Context context) {
        // Clear auth token from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("auth_token");
        editor.apply();

        // Redirect to login activity
        Intent loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(loginIntent);
    }
}


class LoginRequest {
    private String email, password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

class RegisterRequest {
    private String email, password, name;

    public RegisterRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}


class GetTransactionsRequest{
    Date startDate, endDate;

    public GetTransactionsRequest(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}


interface APIInterface {
    @POST("/api/auth/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("/api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("/api/transactions")
    Call<Transaction> addTransaction(@Header("x-auth-token") String authToken, @Body Transaction transaction);

    @GET("/api/transactions")
    Call<List<Transaction>> getTransactions(
            @Header("x-auth-token") String authToken,
            @retrofit2.http.Query("startDate") Date startDate,
            @retrofit2.http.Query("endDate") Date endDate
    );

    @DELETE("/api/transactions/{id}")
    Call<Void> deleteTransaction(@Header("x-auth-token") String authToken, @Path("id") String transactionId);

    @GET("/api/user/profile")
    Call<UserProfileResponse> getUserProfile(@Header("x-auth-token") String authToken);

    @PUT("/api/user/profile")
    Call<UserProfileResponse> updateUserProfile(
            @Header("x-auth-token") String authToken,
            @Body UpdateUserProfileRequest updateUserProfileRequest
    );

}

