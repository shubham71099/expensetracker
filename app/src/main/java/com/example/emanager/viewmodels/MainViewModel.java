package com.example.emanager.viewmodels;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.emanager.models.Transaction;
import com.example.emanager.services.APICallback;
import com.example.emanager.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.emanager.services.APIService;
import io.realm.Realm;

public class MainViewModel extends AndroidViewModel {

    public MutableLiveData<List<Transaction>> transactions = new MutableLiveData<>();
    public MutableLiveData<List<Transaction>> categoriesTransactions = new MutableLiveData<>();

    public MutableLiveData<Double> totalIncome = new MutableLiveData<>();
    public MutableLiveData<Double> totalExpense = new MutableLiveData<>();
    public MutableLiveData<Double> totalAmount = new MutableLiveData<>();

    Realm realm;
    Calendar calendar;

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public void getTransactions(Calendar calendar, String type) {
        this.calendar = (Calendar) calendar.clone(); // Clone to avoid modifying the original calendar

        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", "");

        if (authToken.isEmpty()) {
            return;
        }

        Date startDate;
        Date endDate;

        Calendar workingCalendar = (Calendar) calendar.clone();

        if (Constants.SELECTED_TAB_STATS == Constants.DAILY) {
            // Set to start of day
            workingCalendar.set(Calendar.HOUR_OF_DAY, 0);
            workingCalendar.set(Calendar.MINUTE, 0);
            workingCalendar.set(Calendar.SECOND, 0);
            workingCalendar.set(Calendar.MILLISECOND, 0);
            startDate = workingCalendar.getTime();

            // Set end date to start of next day
            workingCalendar.add(Calendar.DAY_OF_MONTH, 1);
            endDate = workingCalendar.getTime();
        }
        else if (Constants.SELECTED_TAB_STATS == Constants.MONTHLY) {
            // Set to start of month
            workingCalendar.set(Calendar.DAY_OF_MONTH, 1);
            workingCalendar.set(Calendar.HOUR_OF_DAY, 0);
            workingCalendar.set(Calendar.MINUTE, 0);
            workingCalendar.set(Calendar.SECOND, 0);
            workingCalendar.set(Calendar.MILLISECOND, 0);
            startDate = workingCalendar.getTime();

            // Set to start of next month
            workingCalendar.add(Calendar.MONTH, 1);
            endDate = workingCalendar.getTime();
        }
        else {
            return;
        }

        APIService.getInstance().getTransactions(authToken, startDate, endDate, new APICallback<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> result) {
                // Filter results by type
                List<Transaction> filteredTransactions = new ArrayList<>();
                for (Transaction transaction : result) {
                    if (transaction.getType().equals(type)) {
                        filteredTransactions.add(transaction);
                    }
                }

                categoriesTransactions.postValue(filteredTransactions);
            }

            @Override
            public void onError(Throwable t) {
                Log.e("getTransactions", "Error fetching transactions", t);
            }
        });
    }

    public void getTransactions(Calendar calendar) {
        this.calendar = (Calendar) calendar.clone(); // Clone to avoid modifying the original calendar

        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", "");

        if (authToken.isEmpty()) {
            return;
        }

        Date startDate;
        Date endDate;

        // Create a new calendar instance for calculations
        Calendar workingCalendar = (Calendar) calendar.clone();

        if (Constants.SELECTED_TAB == Constants.DAILY) {
            // Set to start of day
            workingCalendar.set(Calendar.HOUR_OF_DAY, 0);
            workingCalendar.set(Calendar.MINUTE, 0);
            workingCalendar.set(Calendar.SECOND, 0);
            workingCalendar.set(Calendar.MILLISECOND, 0);
            startDate = workingCalendar.getTime();

            // Set end date to start of next day
            workingCalendar.add(Calendar.DAY_OF_MONTH, 1);
            endDate = workingCalendar.getTime();
        }
        else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
            // Set to start of month
            workingCalendar.set(Calendar.DAY_OF_MONTH, 1);
            workingCalendar.set(Calendar.HOUR_OF_DAY, 0);
            workingCalendar.set(Calendar.MINUTE, 0);
            workingCalendar.set(Calendar.SECOND, 0);
            workingCalendar.set(Calendar.MILLISECOND, 0);
            startDate = workingCalendar.getTime();

            // Set to start of next month
            workingCalendar.add(Calendar.MONTH, 1);
            endDate = workingCalendar.getTime();
        }
        else {
            Log.e("getTransactions", "Unsupported tab selection");
            return;
        }

        // Calling API to get transactions
        APIService.getInstance().getTransactions(authToken, startDate, endDate, new APICallback<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> result) {
                double income = 0;
                double expense = 0;
                double total = 0;

                for (Transaction transaction : result) {
                    Log.i("-----TESTING-----", String.valueOf(transaction.getId()));
                    if (transaction.getType().equals(Constants.INCOME)) {
                        income += transaction.getAmount();
                    } else if (transaction.getType().equals(Constants.EXPENSE)) {
                        expense += transaction.getAmount();
                    }
                }
                total = income + expense;

                totalIncome.postValue(income);
                totalExpense.postValue(expense);
                totalAmount.postValue(total);
                transactions.postValue(result);

                Log.d("getTransactions", String.format("Fetched %d transactions between %s and %s",
                        result.size(), startDate.toString(), endDate.toString()));
            }

            @Override
            public void onError(Throwable t) {
                Log.e("getTransactions", "Error fetching transactions", t);

            }
        });
    }


    public void deleteTransaction(Transaction transaction) {
        Log.i("INFO------", "Got transaction to delete: " + transaction);


        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", "");

        if (authToken.isEmpty()) {
            return;
        }

        Log.i("INFO------", "Trying to delete txn with id: " + transaction.getId());

        APIService.getInstance().deleteTransaction(authToken, transaction.getId() ,new APICallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getApplication(), "Transaction deleted successfully", Toast.LENGTH_SHORT).show();
                getTransactions(calendar);
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(getApplication(), "Error in deleting transaction", Toast.LENGTH_SHORT).show();

            }
        });
        getTransactions(calendar);
    }

}
