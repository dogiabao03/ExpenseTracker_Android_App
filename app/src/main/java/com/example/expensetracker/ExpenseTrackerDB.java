package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExpenseTrackerDB extends SQLiteOpenHelper {

    public ExpenseTrackerDB(@Nullable Context context) {
        super(context, "ExpenseTracker.db", null, 9);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE IF NOT EXISTS User (" + " userID INTEGER PRIMARY KEY AUTOINCREMENT," + " username TEXT," + " password TEXT" + ");";
        db.execSQL(createUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion<9){
            db.execSQL("DROP TABLE IF EXISTS User;");
            db.execSQL("DROP TABLE IF EXISTS Category;");

            String createUserTable = "CREATE TABLE IF NOT EXISTS User (" + "userID INTEGER PRIMARY KEY AUTOINCREMENT," + "username TEXT," + "password TEXT," + "email TEXT" + ");";
            db.execSQL(createUserTable);

            String createCategoryTable = "CREATE TABLE IF NOT EXISTS Category (" + "categoryID INTEGER PRIMARY KEY AUTOINCREMENT," + "name TEXT," + "type TEXT," + "price REAL," + "note TEXT," + "date TEXT," + "usernameID TEXT," + "FOREIGN KEY(usernameID) REFERENCES User(username)" + ");";
            db.execSQL(createCategoryTable);
        }
    }

    public boolean addNewUser(UserModel user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", user.getUsername());
        cv.put("password", user.getPassword());
        cv.put("email", user.getEmail());

        long insertCheck = db.insert("User", null, cv);
        if (insertCheck == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean checkUserExistence(UserModel user){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"username"};
        String[] selection = {user.getUsername()};
        Cursor c = db.query("User", columns, "username = ?", selection, null, null, null);

        if (c.getCount() > 0) { return true; } // Username already exist
        return false;
    }

    public boolean checkEmailExistence(UserModel user){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"email"};
        String[] selection = {user.getEmail()};
        Cursor c = db.query("User", columns, "email = ?", selection, null, null, null);

        if (c.getCount() > 0) { return true; } // Email already exist
        return false;
    }

    public boolean changePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int rowsAffected = db.update("User", values, "email = ?", new String[]{email});
        db.close();
        return rowsAffected > 0; // true: update successful, false: error
    }

    public boolean checkUserPassword(UserModel user){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"username", "password"};
        String selection = "username = ?";
        String[] selectionArgs = {user.getUsername()};
        boolean checkPass = false;

        Cursor c = db.query("User", columns, selection, selectionArgs, null, null, null);

        if(c.getCount() > 0){
            c.moveToFirst();
            int passwordColumnIndex = c.getColumnIndex("password");
            if(passwordColumnIndex != -1 ) {
                String storedPassword = c.getString(passwordColumnIndex);
                if (user.getPassword().equals(storedPassword)) {
                    checkPass = true;
                }
            }
        }
        return checkPass;
    }

    public boolean addNewTransaction(CategoryModel categoryModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", categoryModel.getCategory_name());
        cv.put("type", categoryModel.getType());
        cv.put("price", categoryModel.getPrice());
        cv.put("note", categoryModel.getNote());
        cv.put("date", categoryModel.getDate());
        cv.put("usernameID", categoryModel.getUsername());

        long insertCheck = db.insert("Category", null, cv);

        if (insertCheck == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public double showingTotal(String type, String usernameID){ // Showing total of expense/ total
        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;

        String selection = "type = ? AND usernameID = ?";
        String[] selectionArgs = {type, usernameID};

        Cursor cursor = db.query("Category", new String[]{"price"}, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex("price");
            if (columnIndex != -1) {
                while (cursor.moveToNext()) {
                    total += cursor.getDouble(columnIndex);
                }
            }
        }
        return total;
    }

    public List<CategoryModel> getTransactions(String usernameID){
        List<CategoryModel> transactions = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Category WHERE usernameID = ?", new String[]{usernameID});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String type = cursor.getString(2);
                double price = cursor.getDouble(3);
                String note = cursor.getString(4);
                String date = cursor.getString(5);
                String username = cursor.getString(6);
                CategoryModel transaction = new CategoryModel(name, type, price, note, date, username, id);
                transactions.add(transaction);
            } while (cursor.moveToNext());
        } else { }

        cursor.close();
        db.close();

        return transactions;
    }

    public boolean deleteTransaction(CategoryModel categoryModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM Category WHERE categoryID = " + categoryModel.getCategoryID() + "";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) { return true; }
        else { return false; }
    }

    public List<CategoryModel> getSearchTransaction(String usernameID, String searchType){
        List<CategoryModel> transactions = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {usernameID, "%" + searchType + "%"};
        Cursor cursor = db.rawQuery("SELECT * FROM Category WHERE usernameID = ? AND type LIKE ?", selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String type = cursor.getString(2);
                double price = cursor.getDouble(3);
                String note = cursor.getString(4);
                String date = cursor.getString(5);
                String username = cursor.getString(6);
                CategoryModel transaction = new CategoryModel(name, type, price, note, date, username, id);
                transactions.add(transaction);
            } while (cursor.moveToNext());
        } else { }

        cursor.close();
        db.close();

        return transactions;
    }

    public double showingMonthlyTotal(String usernameID, String type, String month, String year){
        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;

        String selection = "type = ? AND usernameID = ? AND SUBSTR(date, 1, 3) = ? AND SUBSTR(date, -4) = ?";
        String[] selectionArgs = {type, usernameID, month, year};

        Cursor cursor = db.query("Category", new String[]{"price"}, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex("price");
            if (columnIndex != -1) {
                while (cursor.moveToNext()) {
                    total += cursor.getDouble(columnIndex);
                }
            }
            cursor.close();
        }

        db.close();
        return total;
    }

    public List<CategoryModel> getMonthlyTransaction(String expenseType, String usernameID, String month, String year){
        List<CategoryModel> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = "type = ? AND usernameID = ? AND SUBSTR(date, 1, 3) = ? AND SUBSTR(date, -4) = ?";
        String[] selectionArgs = {expenseType, usernameID, month, year};

        Cursor cursor = db.query("Category", null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String type = cursor.getString(2);
                double price = cursor.getDouble(3);
                String note = cursor.getString(4);
                String date = cursor.getString(5);
                String username = cursor.getString(6);
                CategoryModel transaction = new CategoryModel(name, type, price, note, date, username, id);
                transactions.add(transaction);
            } while (cursor.moveToNext());
        } else { }

        cursor.close();
        db.close();

        return transactions;
    }

}
