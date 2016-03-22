package ru.mail.park.phonebook.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

import ru.mail.park.phonebook.screens.create_note.fragments.contact_form.Contact;
import ru.mail.park.phonebook.screens.create_note.fragments.create_category.Category;

public class DBHelper {

    public boolean insertCategory(Context context, Category category) {
        ContentValues cv = new ContentValues();
        cv.put(PhoneBookContentProvider.COLUMN_NAME, category.getName());
        cv.put(PhoneBookContentProvider.COLUMN_COLOR, category.getColor());
        Uri newUri = context.getContentResolver().insert(PhoneBookContentProvider.CATEGORY_SOURCE_CONTENT_URI, cv);
        long insertedString = ContentUris.parseId(newUri);
        return insertedString > 0;

    }

    public Cursor getCategories(Context context) {
        Cursor cursor = context.getContentResolver().query(PhoneBookContentProvider.CATEGORY_SOURCE_CONTENT_URI, null, null,
                null, null);
        return cursor;
    }

    public boolean deleteCategory(Context baseContext, Category category) {
        int cnt = baseContext.getContentResolver().delete(
                PhoneBookContentProvider.CATEGORY_SOURCE_CONTENT_URI,
                PhoneBookContentProvider.COLUMN_NAME + " =? AND " + PhoneBookContentProvider.COLUMN_COLOR + " =?",
                new String[]{category.getName(), String.valueOf(category.getColor())});
        return cnt > 0;
    }

    public boolean insertContact(Context context, Contact contact) {
        ContentValues cv = new ContentValues();
        cv.put(PhoneBookContentProvider.COLUMN_NAME, contact.getName());
        cv.put(PhoneBookContentProvider.COLUMN_PHONE_NUMBER, contact.getPhoneNumber());
        Uri newUri = context.getContentResolver().insert(PhoneBookContentProvider.CONTACT_SOURCE_CONTENT_URI, cv);
        long insertedString = ContentUris.parseId(newUri);

        ArrayList<Category> categories = contact.getCategories();

        if (categories != null && categories.size() > 0) {
            for (Category category : categories) {
                category.getName();
                category.getColor();
                Cursor cursor = context.getContentResolver().query(PhoneBookContentProvider.CATEGORY_SOURCE_CONTENT_URI,
                        null,
                        PhoneBookContentProvider.COLUMN_NAME + " =? AND " + PhoneBookContentProvider.COLUMN_COLOR + " =?",
                        new String[]{category.getName(), String.valueOf(category.getColor())},
                        null);
                cursor.moveToFirst();
                int category_id = cursor.getInt(cursor.getColumnIndex(PhoneBookContentProvider.COLUMN_ID));
                cursor.close();
                insertLink(context, insertedString, category_id);
            }
        }
        return insertedString > 0;
    }

    private boolean insertLink(Context context, long contact_id, int category_id) {
        ContentValues cv = new ContentValues();
        cv.put(PhoneBookContentProvider.COLUMN_CONTACT_ID, contact_id);
        cv.put(PhoneBookContentProvider.COLUMN_CATEGORY_ID, category_id);
        Uri newUri = context.getContentResolver().insert(PhoneBookContentProvider.LINK_SOURCE_CONTENT_URI, cv);
        long insertedString = ContentUris.parseId(newUri);
        return insertedString > 0;
    }

    public boolean deleteContact(Context context, int contact_id) {
        Uri uri = ContentUris.withAppendedId(PhoneBookContentProvider.CONTACT_SOURCE_CONTENT_URI, contact_id);
        int cnt = context.getContentResolver().delete(uri, null, null);
        return cnt > 0;
    }
}
