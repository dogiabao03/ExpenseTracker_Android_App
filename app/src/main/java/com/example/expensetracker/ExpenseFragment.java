package com.example.expensetracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ExpenseFragment extends Fragment {

    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    String username;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    public static ExpenseFragment newInstance(String param1, String param2) {
        ExpenseFragment fragment = new ExpenseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Category Spinner
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        Spinner spinner = view.findViewById(R.id.expenseSpinner);

        String[] items = {"Food", "Houseware", "Clothes", "Cosmetic", "Exchange", "Medical", "Education", "Bills", "Transportation", "Fee", "Housing Expenses", "Other"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Datetime Picker
        initDatePicker();
        dateButton = view.findViewById(R.id.datePicker);
        dateButton.setText(getTodayDate());

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        // Submit Button
        Button submitButton = view.findViewById(R.id.submitExpense);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseTrackerDB expenseTrackerDB = new ExpenseTrackerDB(requireContext());

                String name = spinner.getSelectedItem().toString();
                String type = "Expense";
                TextView expenseText = view.findViewById(R.id.expensePrice);
                TextView expenseNote = view.findViewById(R.id.noteExpense);
                String note = expenseNote.getText().toString();
                String date = dateButton.getText().toString();
                String usernameID = username;

                if (expenseText.getText().toString().matches(".*[a-zA-Z].*")){
                    Toast.makeText(getContext(), "Expense must be numeric values!", Toast.LENGTH_LONG).show();
                }
                else if (expenseText.getText().toString().equals("") ){
                    Toast.makeText(getContext(), "Expense can't be empty!", Toast.LENGTH_LONG).show();
                }
                else {
                    double price = Double.parseDouble(expenseText.getText().toString());
                    CategoryModel categoryInfo = new CategoryModel(name, type, price, note, date, usernameID);
                    boolean addExpense = expenseTrackerDB.addNewTransaction(categoryInfo);

                    if (addExpense) {
                        dateButton.setText(getTodayDate());
                        expenseNote.setText("");
                        expenseText.setText("0");
                        Toast.makeText(getContext(), "Adding new expense successfully!", Toast.LENGTH_LONG).show();
                    }
                    else { Toast.makeText(getContext(), "Couldn't create new expense!", Toast.LENGTH_LONG).show(); }
                }

            }
        });

        // Sign Out Button
        TextView signOut = view.findViewById(R.id.textView11);
        signOut.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LogInActivity.class);
                startActivity(i);
            }
        });

        // Username Data Display
        Bundle bundle = getArguments();
        if (bundle != null) {
            username = bundle.getString("usernameData");
            if (username != null) {
                TextView t = view.findViewById(R.id.textView26);
                t.setText(username);
            } else {
                Log.d("YourTag", "Username is null");
            }
        } else {
            Log.d("YourTag", "Bundle is null");
        }

        return view;
    }

    public void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(getContext(), style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year){ return getMonthFormat(month) + " " + day + " " + year; }

    private String getMonthFormat(int month){
        if (month == 1)
            return "JAN";
        else if (month == 2)
            return "FEB";
        else if (month == 3)
            return "MAR";
        else if (month == 4)
            return "APR";
        else if (month == 5)
            return "MAY";
        else if (month == 6)
            return "JUN";
        else if (month == 7)
            return "JUL";
        else if (month == 8)
            return "AUG";
        else if (month == 9)
            return "SEP";
        else if (month == 10)
            return "OCT";
        else if (month == 11)
            return "NOV";
        else if (month == 12)
            return "DEC";
        else
            return "Invalid month";
    }

    private String getTodayDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    public void openDatePicker(){
        datePickerDialog.show();
    }

}