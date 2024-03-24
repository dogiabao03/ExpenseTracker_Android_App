package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {
    UserModel userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    // Go to Login Activity
    public void logInActivity(View v){
        Intent i = new Intent(this, LogInActivity.class);
        startActivity(i);
    }

    public void signUpCheck(View v){
        TextView username = findViewById(R.id.usernameText);
        TextView password = findViewById(R.id.userpasswordText);
        TextView password2 = findViewById(R.id.userpassword2Text);
        TextView email = findViewById(R.id.emailTextSignUp);

        String usernameValue = username.getText().toString();
        String passwordValue = password.getText().toString();
        String password2Value = password2.getText().toString();
        String emailValue = email.getText().toString();

        if (usernameValue.equals("") || passwordValue.equals("") || password2Value.equals("") || emailValue.equals("")){
            if (usernameValue.equals(""))
                Toast.makeText(this, "Username can't be empty!", Toast.LENGTH_LONG).show();
            else if (emailValue.equals("")){
                Toast.makeText(this, "Please enter your email!", Toast.LENGTH_LONG).show();
            }
            else if (passwordValue.equals(""))
                Toast.makeText(this, "Password can't be empty!", Toast.LENGTH_LONG).show();
            else if (password2Value.equals(""))
                Toast.makeText(this, "Please confirm your password!", Toast.LENGTH_LONG).show();
        }

        else {
            if (!passwordValue.equals(password2Value)) {
                Toast.makeText(this, "Password not match! \n Please try again.", Toast.LENGTH_LONG).show();
            }

            else {
                ExpenseTrackerDB expenseTrackerDB = new ExpenseTrackerDB(SignUpActivity.this);
                userInfo = new UserModel(usernameValue, passwordValue, emailValue);
                boolean checkUser = expenseTrackerDB.checkUserExistence(userInfo);

                if (checkUser) { Toast.makeText(this, "Username already exist!", Toast.LENGTH_LONG).show(); }
                else {
                    boolean checkEmail = expenseTrackerDB.checkEmailExistence(userInfo);
                    if (checkEmail) { Toast.makeText(this, "Email has been registered!", Toast.LENGTH_LONG).show(); }
                    else {
                        boolean addUser = expenseTrackerDB.addNewUser(userInfo);
                        if (addUser){ Toast.makeText(this, "Account created successfully!", Toast.LENGTH_LONG).show(); }
                        else { Toast.makeText(this, "Can't create account!", Toast.LENGTH_LONG).show(); }
                        username.setText("");
                        password.setText("");
                        password2.setText("");
                        email.setText("");
                    }
                }
            }
        }
    }
}