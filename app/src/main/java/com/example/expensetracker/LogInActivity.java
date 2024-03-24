package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.*;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class LogInActivity extends AppCompatActivity {
    Connection cnt;
    UserModel userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        //connectDB();

        ExpenseTrackerDB expenseTrackerDB = new ExpenseTrackerDB(LogInActivity.this);
    }

    public void signUpActivity(View v){
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }

    public void LoginCheck(View v){
        TextView username = findViewById(R.id.usernameText);
        TextView password = findViewById(R.id.userpasswordText);
        String usernameValue = username.getText().toString();
        String passwordValue = password.getText().toString();
        UserModel userInfo;

        // Checking null for Username and Password
        if (usernameValue.equals("") || passwordValue.equals("")){
            if (usernameValue.equals(""))
                Toast.makeText(this, "Username can't be empty!", Toast.LENGTH_LONG).show();
            else if (passwordValue.equals(""))
                Toast.makeText(this, "Password can't be empty!", Toast.LENGTH_LONG).show();
        }

        else {
            ExpenseTrackerDB expenseTrackerDB = new ExpenseTrackerDB(LogInActivity.this);
            userInfo = new UserModel(usernameValue, passwordValue);
            boolean checkUserAccount = expenseTrackerDB.checkUserExistence(userInfo);

            if (checkUserAccount){
                boolean checkPass = expenseTrackerDB.checkUserPassword(userInfo);

                if (checkPass) {
                    username.setText("");
                    password.setText("");
                    Intent i = new Intent(this, MainActivity.class);
                    i.putExtra("usernameData", usernameValue);
                    Toast.makeText(this, "Hi, Welcome back!", Toast.LENGTH_LONG).show();
                    startActivity(i);
                }
                else { Toast.makeText(this, "Password is incorrect! Try Again!", Toast.LENGTH_LONG).show(); }
            }

            else { Toast.makeText(this, "Username not exist! Try again!", Toast.LENGTH_LONG).show(); }
        }
    }

    public void moveToForgetPass(View view){
        Intent i = new Intent(this, ForgetPasswordActivity.class);
        startActivity(i);
    }
}