package com.example.expensetracker;

public class CategoryModel {
    private int categoryID;
    private String category_name; // Food, Housing, ...
    private String type; // Expense or Income
    private double price;
    private String note;
    private String date; // dd-mm--yy
    private String username;

    public CategoryModel() { }
    public CategoryModel(String category_name, String type, double price, String note, String date, String username) {
        this.category_name = category_name;
        this.type = type;
        this.price = price;
        this.note = note;
        this.date = date;
        this.username = username;
    }

    public CategoryModel(String category_name, String type, double price, String note, String date, String username, int categoryID) {
        this.category_name = category_name;
        this.type = type;
        this.price = price;
        this.note = note;
        this.date = date;
        this.username = username;
        this.categoryID = categoryID;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public int getCategoryID() { return categoryID; }

    public void setCategoryID(int categoryID) { this.categoryID = categoryID; }

    public String toString(){
        return "\n" + category_name + "\nType: " + type + "\nPrice: " + price + "\nDate: " + date + "\nNote: " + note + "\n";
    }
}
