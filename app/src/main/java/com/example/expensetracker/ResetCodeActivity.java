package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ResetCodeActivity extends AppCompatActivity {
    String resetCode, emailValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_code);
    }

    public void submitResetCode(View view){
        EditText codeText = findViewById(R.id.codeText);
        String codeValue = codeText.getText().toString();

        if (codeValue.equals("")){
            Toast.makeText(this, "Please enter your reset code!", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = getIntent();
            resetCode = intent.getStringExtra("resetCodeValue");
            emailValue = intent.getStringExtra("emailValue");

            if (resetCode.equals(codeValue)){
                Intent i = new Intent(this, ResetPasswordActivity.class);
                i.putExtra("emailValue", emailValue);
                startActivity(i);
            } else { Toast.makeText(this, "Wrong reset code, please try again!", Toast.LENGTH_LONG).show(); }
        }
    }
}