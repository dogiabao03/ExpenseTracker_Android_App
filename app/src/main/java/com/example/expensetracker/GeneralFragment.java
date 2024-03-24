package com.example.expensetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneralFragment extends Fragment {
    String username;
    ListView listView;
    double totalExpense, totalIncome;
    TextView totalExpenseText, totalIncomeText, totalText;

    EditText searchText;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GeneralFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralFragment newInstance(String param1, String param2) {
        GeneralFragment fragment = new GeneralFragment();
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
        View view = inflater.inflate(R.layout.fragment_general, container, false);

        // Username Display
        Bundle bundle = getArguments();
        if (bundle != null) {
            username = bundle.getString("usernameData");
            if (username != null) {
                TextView t = view.findViewById(R.id.textView30);
                t.setText(username);
            } else {
                Log.d("YourTag", "Username is null");
            }
        } else {
            Log.d("YourTag", "Bundle is null");
        }

        // Sign Out
        TextView signOut = view.findViewById(R.id.textView13);
        signOut.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LogInActivity.class);
                startActivity(i);
            }
        });

        // Showing Total
        ExpenseTrackerDB expenseTrackerDB = new ExpenseTrackerDB(requireContext());
        totalExpense = expenseTrackerDB.showingTotal("Expense", username);
        totalIncome = expenseTrackerDB.showingTotal("Income", username);

        totalExpenseText = view.findViewById(R.id.totalExpense);
        totalExpenseText.setText(String.valueOf(totalExpense));

        totalIncomeText = view.findViewById(R.id.totalIncome);
        totalIncomeText.setText(String.valueOf(totalIncome));

        totalText = view.findViewById(R.id.total);
        totalText.setText(String.valueOf(totalIncome - totalExpense));

        // Initialize a List
        listView = view.findViewById(R.id.listView);
        showListTransaction(expenseTrackerDB, listView);

        // Initialize searchText variable using for Delete and Search Category
        searchText = view.findViewById(R.id.editTextText5);

        // Delete Category
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryModel clickedCategory = (CategoryModel) parent.getItemAtPosition(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Delete Transaction?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                expenseTrackerDB.deleteTransaction(clickedCategory);
                                showListTransaction(expenseTrackerDB, listView);
                                if (searchText.getText().toString().equals("")){ showListTransaction(expenseTrackerDB, listView); }
                                else { searchTransaction(expenseTrackerDB, listView, searchText.getText().toString()); }

                                totalExpense = expenseTrackerDB.showingTotal("Expense", username);
                                totalIncome = expenseTrackerDB.showingTotal("Income", username);
                                totalExpenseText.setText(String.valueOf(totalExpense));
                                totalIncomeText.setText(String.valueOf(totalIncome));
                                totalText.setText(String.valueOf(totalIncome - totalExpense));

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // Searching Transaction Type
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!searchText.getText().toString().equals("")) { searchTransaction(expenseTrackerDB, listView, searchText.getText().toString()); }
                else { showListTransaction(expenseTrackerDB, listView); }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        return view;
    }

    public void showListTransaction(ExpenseTrackerDB expenseTrackerDB, ListView listView){
        List<CategoryModel> transactions = expenseTrackerDB.getTransactions(username);
        Collections.reverse(transactions);
        ArrayAdapter transactionList = new ArrayAdapter<CategoryModel>(getContext(), android.R.layout.simple_list_item_1, transactions);
        listView.setAdapter(transactionList);
    }

    public void searchTransaction(ExpenseTrackerDB expenseTrackerDB, ListView listView, String type){
        List<CategoryModel> transactions = expenseTrackerDB.getSearchTransaction(username, type);
        ArrayAdapter transactionList = new ArrayAdapter<CategoryModel>(getContext(), android.R.layout.simple_list_item_1, transactions);
        listView.setAdapter(transactionList);
    }
}