package ru.mail.park.phonebook.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ru.mail.park.phonebook.screens.main.BaseActivity;
import ru.mail.park.phonebook.screens.create_note.fragments.contact_form.Contact;
import ru.mail.park.phonebook.screens.create_note.fragments.create_category.Category;

public class MySharedPreferences {

    private static final String CONTACT_NAME  = "CONTACT_NAME";
    private static final String PHONE_NUMBER  = "PHONE_NUMBER";
    private static final String FILTER_CATEGORY_ID  = "FILTER_CATEGORY_ID";
    BaseActivity activity;

    public MySharedPreferences(BaseActivity activity) {
        this.activity = activity;
    }

    private void inputContactData(String name, String phone_number) {
        SharedPreferences prefs = activity.getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CONTACT_NAME, name);
        editor.putString(PHONE_NUMBER, phone_number);
        editor.apply();
    }

    public void resetInputContactData() {
        inputContactData("", "");
    }

    public void saveInputContactData(String name, String phoneNumber) {
        inputContactData(name, phoneNumber);
    }

    public Contact getSavedInputData() {
        SharedPreferences prefs = activity.getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE);
        return new Contact(-1, prefs.getString(CONTACT_NAME, ""), prefs.getString(PHONE_NUMBER,""), new ArrayList<Category>());
    }

    public void saveSelectedCategories(ArrayList<Category> selectedCategories) {
        String s = new Gson().toJson(selectedCategories, ArrayList.class);
        SharedPreferences prefs = activity.getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CATEGORIES", s);
        editor.apply();
    }

    public ArrayList<Category> getChoosenCategories() {
        SharedPreferences sPref = activity.getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE);
        String savedText = sPref.getString("CATEGORIES", "");
        Type type = new TypeToken<ArrayList<Category>>() { }.getType();
        return new Gson().fromJson(savedText, type);
    }

    public String[] getChoosenCategoriesNames() {
        ArrayList<String> toppings = new ArrayList<>();

        ArrayList<Category> categories = getChoosenCategories();

        if (categories != null) {
            for (Category object : categories) {
            toppings.add(object.getName());
            }
        }

        String[] stockArr = new String[toppings.size()];
        stockArr = toppings.toArray(stockArr);
        return stockArr;
    }

    public void resetChosenCatagories() {
        ArrayList<Category> selectedCategories = new ArrayList<>();
        String s = new Gson().toJson(selectedCategories, ArrayList.class);
        SharedPreferences prefs = activity.getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CATEGORIES", s);
        editor.apply();
    }

    public void saveSelectedFilterCategoryId(int category_id) {
        SharedPreferences prefs = activity.getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(FILTER_CATEGORY_ID, category_id);
        editor.apply();
    }

    public int getSelectedFilterCategory() {
        SharedPreferences prefs = activity.getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE );
        return prefs.getInt(FILTER_CATEGORY_ID, 1);
    }
}


