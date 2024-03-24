package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.expensetracker.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Main Expense Tracker Code File
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        username =  getIntent().getStringExtra("usernameData");
        replaceFragment(new ExpenseFragment(), username);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.expense:
                    replaceFragment(new ExpenseFragment(), username);
                    break;

                case R.id.income:
                    replaceFragment(new IncomeFragment(), username);
                    break;

                case R.id.general:
                    replaceFragment(new GeneralFragment(), username);
                    break;

                case R.id.report:
                    replaceFragment(new ReportFragment(), username);
                    break;
            }
            return true;
        });

    }

    private void replaceFragment(Fragment fragment, String username){
        Bundle b = new Bundle();
        b.putString("usernameData", username);
        fragment.setArguments(b);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}