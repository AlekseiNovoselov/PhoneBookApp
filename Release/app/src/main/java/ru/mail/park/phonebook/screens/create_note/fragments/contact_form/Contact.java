package ru.mail.park.phonebook.screens.create_note.fragments.contact_form;

import java.util.ArrayList;

import ru.mail.park.phonebook.screens.create_note.fragments.create_category.Category;

public class Contact {
    private int id;
    private String name;
    private String phoneNumber;
    private ArrayList<Category> categories;

    public Contact(int id, String name, String phoneNumber, ArrayList<Category> categories) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public int getId() {
        return id;
    }
}
