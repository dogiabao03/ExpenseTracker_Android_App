package com.example.expensetracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.graphics.Color;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment {

    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    String username, date;
    TextView totalExpenseText, totalIncomeText;
    ListView CategorylistView;
    PieChart pieChart;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        // Datetime Picker
        initDatePicker();
        dateButton = view.findViewById(R.id.button5);
        dateButton.setText(getTodayDate());

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        // Sign out
        TextView signOut = view.findViewById(R.id.textView65);
        signOut.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LogInActivity.class);
                startActivity(i);
            }
        });

        // Display username
        Bundle bundle = getArguments();

        if (bundle != null) {
            username = bundle.getString("usernameData");
            if (username != null) {
                TextView t = view.findViewById(R.id.textView64);
                t.setText(username);
            }
        }

        // Showing statistics
        CategorylistView = view.findViewById(R.id.listView1);

        Spinner spinner = view.findViewById(R.id.spinner);
        String[] items = {"Expense", "Income"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        pieChart = view.findViewById(R.id.pieChart);
        TextView categoryText = view.findViewById(R.id.textView57);
        String transactionType = spinner.getSelectedItem().toString();
        TextView pieChartText = view.findViewById(R.id.textView58);
        ConstraintLayout pieChartLayout = view.findViewById(R.id.pieChartContainer);

        showingStatistics(view); // Showing monthly total of expense/ income
        showingMonthlyTransactions(transactionType, CategorylistView, categoryText, pieChartText, pieChartLayout, pieChart); // Showing monthly history (ListView)
        calculateCategoryPercentage(transactionType, pieChart); // Pie chart

        dateButton.addTextChangedListener(new TextWatcher() { // Changing the date
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                showingStatistics(view);
                spinner.setSelection(0);
                showingMonthlyTransactions("Expense", CategorylistView, categoryText, pieChartText, pieChartLayout, pieChart);
                calculateCategoryPercentage("Expense", pieChart);
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // Showing monthly category of expense/ income
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newItem = spinner.getSelectedItem().toString();
                showingMonthlyTransactions(newItem, CategorylistView, categoryText, pieChartText, pieChartLayout, pieChart);
                calculateCategoryPercentage(newItem, pieChart);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        return view;
    }

    public void calculateCategoryPercentage(String transactionType, PieChart pieChart){
        ExpenseTrackerDB expenseTrackerDB = new ExpenseTrackerDB(requireContext());
        date = dateButton.getText().toString();
        String month = date.substring(0, 3);
        String year = date.substring(date.length() - 4);

        List<CategoryModel> categoryModelList = expenseTrackerDB.getTransactions(username);

        HashMap<String, Double> categoryAmountMap = new HashMap<>();
        double totalAmount = 0;

        for (CategoryModel categoryModel : categoryModelList) {
            String categoryMonth = categoryModel.getDate().substring(0, 3);
            String categoryYear = categoryModel.getDate().substring(categoryModel.getDate().length() - 4);

            if (categoryModel.getType().equals(transactionType) && categoryMonth.equals(month) && categoryYear.equals(year)) {
                String categoryName = categoryModel.getCategory_name();
                double amount = categoryModel.getPrice();
                double totalAmountForCategory = categoryAmountMap.getOrDefault(categoryName, 0.0);
                categoryAmountMap.put(categoryName, totalAmountForCategory + amount);
                totalAmount += amount;
            }
        }

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryAmountMap.entrySet()) {
            String categoryName = entry.getKey();
            double amount = entry.getValue();
            double percentage = (amount / totalAmount) * 100;
            pieEntries.add(new PieEntry((float) percentage, categoryName));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(transactionType);
        pieChart.animateY(1000);

        pieChart.invalidate();
    }

    public void showingMonthlyTransactions(String transactionType, ListView CategorylistView, TextView categoryText, TextView pieChartText, ConstraintLayout pieChartContainer, PieChart pieChart){
        ExpenseTrackerDB expenseTrackerDB = new ExpenseTrackerDB(requireContext());
        date = dateButton.getText().toString();
        String month = date.substring(0, 3);
        String year = date.substring(date.length() - 4);

        if (transactionType.equals("Expense") || transactionType.equals("Income")){
            categoryText.setText("Monthly " + transactionType);
            List<CategoryModel> transactions = expenseTrackerDB.getMonthlyTransaction(transactionType, username, month, year);
            if (transactions.isEmpty()){
                categoryText.setVisibility(View.GONE);
                CategorylistView.setVisibility(View.GONE);
                pieChartText.setVisibility(View.GONE);
                pieChart.setVisibility(View.GONE);
                pieChartContainer.setVisibility(View.GONE);
            } else {
                categoryText.setVisibility(View.VISIBLE);
                CategorylistView.setVisibility(View.VISIBLE);
                pieChartText.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.VISIBLE);
                pieChartContainer.setVisibility(View.VISIBLE);

                Collections.reverse(transactions);
                ArrayAdapter transactionList = new ArrayAdapter<CategoryModel>(getContext(), android.R.layout.simple_list_item_1, transactions);
                CategorylistView.setAdapter(transactionList);
            }
        }
    }

    private void showingStatistics(View view){
        ExpenseTrackerDB expenseTrackerDB = new ExpenseTrackerDB(requireContext());
        date = dateButton.getText().toString();
        String month = date.substring(0, 3);
        String year = date.substring(date.length() - 4);

        double totalExpense = expenseTrackerDB.showingMonthlyTotal(username, "Expense", month, year);
        totalExpenseText = view.findViewById(R.id.editTextText2);
        totalExpenseText.setText(String.valueOf(totalExpense));

        double totalIncome = expenseTrackerDB.showingMonthlyTotal(username, "Income", month, year);
        totalIncomeText = view.findViewById(R.id.editTextText3);
        totalIncomeText.setText(String.valueOf(totalIncome));

        TextView totalText = view.findViewById(R.id.editTextText4);
        totalText.setText(String.valueOf(totalIncome - totalExpense));
    }

    public void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(getContext(), style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
    }

    private String makeDateString(int month, int year){ return getMonthFormat(month) + " " + year; }

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
        return makeDateString(month, year);
    }

    public void openDatePicker(){
        datePickerDialog.show();
    }

}