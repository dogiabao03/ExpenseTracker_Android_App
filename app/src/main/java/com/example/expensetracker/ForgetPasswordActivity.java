package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgetPasswordActivity extends AppCompatActivity {
    String resetCode = generateNewPassword();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
    }

    public void sendRestPassword(View view){
        EditText emailText = findViewById(R.id.emailText);
        String stringReceiverEmail = emailText.getText().toString();
        String stringSenderEmail = "expensetrackerappbybaodo@gmail.com";
        String stringSenderPassword = "aihtawokdaxdpacr";
        String stringHost = "smtp.gmail.com";

        if (stringReceiverEmail.equals("")){
            Toast.makeText(this, "Please enter your register email!", Toast.LENGTH_LONG).show();
        }
        else {
            ExpenseTrackerDB expenseTrackerDB = new ExpenseTrackerDB(ForgetPasswordActivity.this);
            UserModel userInfo = new UserModel(emailText.getText().toString());
            boolean checkEmail = expenseTrackerDB.checkEmailExistence(userInfo);

            if (checkEmail){
                try {
                    Properties properties = System.getProperties();
                    properties.put("mail.smtp.host", stringHost);
                    properties.put("mail.smtp.port", "465");
                    properties.put("mail.smtp.ssl.enable", "true");
                    properties.put("mail.smtp.auth", "true");

                    Session session = Session.getInstance(properties, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(stringSenderEmail, stringSenderPassword);
                        }
                    });

                    MimeMessage mineMessage = new MimeMessage(session);
                    mineMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(stringReceiverEmail));
                    mineMessage.setSubject("Reset Your Expense Tracker App Password!");
                    mineMessage.setText(" Hi, \n Need to change your password. \n Here is your reset code: " + resetCode + "\n Have a good day! \n\n Expense Tracker Team.");

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Transport.send(mineMessage);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ForgetPasswordActivity.this, "Reset code sent successfully!\nPlease check your gmail.", Toast.LENGTH_SHORT).show();
                                        emailText.setText("");
                                        Intent i = new Intent(ForgetPasswordActivity.this, ResetCodeActivity.class);
                                        i.putExtra("resetCodeValue", resetCode);
                                        i.putExtra("emailValue", stringReceiverEmail);
                                        startActivity(i);
                                    }
                                });
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();

                } catch (AddressException e){
                    e.printStackTrace();
                } catch (MessagingException e){
                    e.printStackTrace();
                }
            } else { Toast.makeText(this, "Email not register yet!", Toast.LENGTH_LONG).show(); }
        }
    }

    private String generateNewPassword() {
        String characters = "0123456789";
        StringBuilder newPassword = new StringBuilder();
        int length = 6;
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            newPassword.append(characters.charAt(index));
        }
        return newPassword.toString();
    }

    public void backToLogin(View view){
        Intent i = new Intent(this, LogInActivity.class);
        startActivity(i);
    }

}