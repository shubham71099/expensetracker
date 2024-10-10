package com.example.emanager.views.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.emanager.utils.Constants;
import com.example.emanager.viewmodels.MainViewModel;
import com.example.emanager.R;
import com.example.emanager.databinding.ActivityMainBinding;
import com.example.emanager.views.fragments.ProfileFragment;
import com.example.emanager.views.fragments.StatsFragment;
import com.example.emanager.views.fragments.TransactionsFragment;
import com.google.android.material.navigation.NavigationBarView;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    Calendar calendar;

    public MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setSupportActionBar(binding.toolBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Expense Tracker");

        Constants.setCategories();

        calendar = Calendar.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new TransactionsFragment());
        transaction.commit();

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if(item.getItemId() == R.id.transactions) { 
                    getSupportFragmentManager().popBackStack();
                } else if(item.getItemId() == R.id.stats){
                    transaction.replace(R.id.content, new StatsFragment());
                    transaction.addToBackStack(null);
                } else if(item.getItemId() == R.id.profile){
                    transaction.replace(R.id.content, new ProfileFragment());
                    transaction.addToBackStack(null);
                }
                transaction.commit();
                return true;
            }
        });


    }

    public void getTransactions() {
        viewModel.getTransactions(calendar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}