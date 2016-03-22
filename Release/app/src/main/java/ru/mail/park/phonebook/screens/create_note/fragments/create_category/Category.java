package ru.mail.park.phonebook.screens.create_note.fragments.create_category;

public class Category {
    private int color;
    private String name;

    public Category(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
}
