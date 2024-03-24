package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
    }

    public void changePassword(View view){
        TextView newPass = findViewById(R.id.newPass);
        TextView newPass2 = findViewById(R.id.newPass2);
        String newPassValue = newPass.getText().toString();
        String cfPassValue = newPass2.getText().toString();

        if (newPassValue.equals("") || cfPassValue.equals("")){
            if (newPassValue.equals("")){ Toast.makeText(this, "Please enter new password!", Toast.LENGTH_LONG).show(); }
            else if (cfPassValue.equals("")) { Toast.makeText(this, "Please confirm password!", Toast.LENGTH_LONG).show(); }
        }
        else {
            if (newPassValue.equals(cfPassValue)){
                Intent i = getIntent();
                String emailValue = i.getStringExtra("emailValue");

                ExpenseTrackerDB expenseTrackerDB = new ExpenseTrackerDB(ResetPasswordActivity.this);
                boolean changePass = expenseTrackerDB.changePassword(emailValue, newPassValue);

                if (changePass){
                    Toast.makeText(this, "Password updated successfully!\nLet's log in!", Toast.LENGTH_LONG).show();
                    Intent login = new Intent(this, LogInActivity.class);
                    startActivity(login);
                } else {
                    Toast.makeText(this, "Error in password changing. Please try again!", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "Password not match. Try again!", Toast.LENGTH_LONG).show();
            }
        }
    }


}