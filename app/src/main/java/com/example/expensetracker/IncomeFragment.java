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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class IncomeFragment extends Fragment {

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IncomeFragment newInstance(String param1, String param2) {
        IncomeFragment fragment = new IncomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public IncomeFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        // Tìm và tham chiếu tới Spinner trong layout của Fragment
        Spinner spinner = view.findViewById(R.id.categorySpinner);

        // Khởi tạo mảng dữ liệu cho Spinner
        String[] items = {"Salary", "Pocket Money", "Bonus", "Side Job", "Investment", "Extra", "Other"};

        // Tạo một ArrayAdapter từ mảng dữ liệu
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Đặt Adapter cho Spinner
        spinner.setAdapter(adapter);

        initDatePicker();
        dateButton = view.findViewById(R.id.datePicker2);
        dateButton.setText(getTodayDate());

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        // Username Display
        Bundle bundle = getArguments();
        if (bundle != null) {
            username = bundle.getString("usernameData");
            if (username != null) {
                TextView t = view.findViewById(R.id.textView28);
                t.setText(username);
            } else {
                Log.d("YourTag", "Username is null");
            }
        } else {
            Log.d("YourTag", "Bundle is null");
        }

        // Sign Out
        TextView signOut = view.findViewById(R.id.textView8);
        signOut.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LogInActivity.class);
                startActivity(i);
            }
        });

        // Submit Button
        Button submitButton = view.findViewById(R.id.submitIncome);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseTrackerDB expenseTrackerDB = new ExpenseTrackerDB(requireContext());

                String name = spinner.getSelectedItem().toString();
                String type = "Income";
                TextView incomeText = view.findViewById(R.id.incomePrice);
                TextView incomeNote = view.findViewById(R.id.incomeNote);
                String note = incomeNote.getText().toString();
                String date = dateButton.getText().toString();
                String usernameID = username;

                if (incomeText.getText().toString().matches(".*[a-zA-Z].*")){
                    Toast.makeText(getContext(), "Income must be numeric values!", Toast.LENGTH_LONG).show();
                }
                else if (incomeText.getText().toString().equals("") ){
                    Toast.makeText(getContext(), "Income can't be empty!", Toast.LENGTH_LONG).show();
                }
                else {
                    double price = Double.parseDouble(incomeText.getText().toString());
                    CategoryModel categoryInfo = new CategoryModel(name, type, price, note, date, usernameID);
                    boolean addIncome = expenseTrackerDB.addNewTransaction(categoryInfo);

                    if (addIncome) {
                        dateButton.setText(getTodayDate());
                        incomeNote.setText("");
                        incomeText.setText("0");
                        Toast.makeText(getContext(), "Adding new income successfully!", Toast.LENGTH_LONG).show();
                    }
                    else { Toast.makeText(getContext(), "Couldn't create new income!", Toast.LENGTH_LONG).show(); }
                }
            }
        });

        return view;
    }

    private String getTodayDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
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

    private String makeDateString(int day, int month, int year){
        return getMonthFormat(month) + " " + day + " " + year;
    }

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

    public void openDatePicker(){
        datePickerDialog.show();
    }
}