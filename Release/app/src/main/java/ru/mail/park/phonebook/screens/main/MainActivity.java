package ru.mail.park.phonebook.screens.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.db.PhoneBookContentProvider;
import ru.mail.park.phonebook.screens.create_note.CreateNoteActivity;
import ru.mail.park.phonebook.screens.create_note.fragments.contact_form.Contact;
import ru.mail.park.phonebook.screens.main.fragments.contact_fragment.ContactsFragment;
import ru.mail.park.phonebook.utils.MySharedPreferences;

public class MainActivity extends BaseActivity {

    public final static String CURRENT_SCREEN = "ru.mail.park.phoneBook.CURRENT_SCREEN";
    public final static String CHOSEN_CONTACT_ID = "CHOSEN_CONTACT_ID";
    public final static String CHOSEN_CONTACT_NAME = "CHOSEN_CONTACT_NAME";
    public final static String CHOSEN_CONTACT_PHONE = "CHOSEN_CONTACT_PHONE";

    final int REQUEST_CODE_DRAWER_ITEM = 1;

    private Spinner spinner_nav;
    LoaderManager.LoaderCallbacks<Cursor> contactsLoader;
    private static final int CATEGORY_LIST_LOADER = 0x01;
    SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        spinner_nav = (Spinner) findViewById(R.id.spinner_nav);
        addItemsToSpinner();
        MySharedPreferences mySharedPreferences = new MySharedPreferences(this);
        mySharedPreferences.saveSelectedFilterCategoryId(1);
        myFragmentManager = new MyFragmentManager(MainActivity.this, toolbar, spinner_nav);
        myFragmentManager.addFragment(MyFragmentManager.Screen.CONTACTS);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.contacts:
                if (myFragmentManager.getCurrentScreen() != MyFragmentManager.Screen.CONTACTS)
                    myFragmentManager.addFragment(MyFragmentManager.Screen.CONTACTS);
                break;
            case R.id.settings:
                if (myFragmentManager.getCurrentScreen() != MyFragmentManager.Screen.SETTINGS)
                    myFragmentManager.addFragment(MyFragmentManager.Screen.SETTINGS);
                break;
            case R.id.about:
                if (myFragmentManager.getCurrentScreen() != MyFragmentManager.Screen.ABOUT)
                    myFragmentManager.addFragment(MyFragmentManager.Screen.ABOUT);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(CURRENT_SCREEN, myFragmentManager.getCurrentScreen().toString());
        if (myFragmentManager.getChosenContact()!= null) {
            state.putInt(CHOSEN_CONTACT_ID, myFragmentManager.getChosenContact().getId());
            state.putString(CHOSEN_CONTACT_NAME, myFragmentManager.getChosenContact().getName());
            state.putString(CHOSEN_CONTACT_PHONE, myFragmentManager.getChosenContact().getPhoneNumber());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        int id = state.getInt(CHOSEN_CONTACT_ID, 0);
        String name = state.getString(CHOSEN_CONTACT_NAME, "DEFAULT NAME");
        String phone = state.getString(CHOSEN_CONTACT_PHONE, "+7***88886711");
        myFragmentManager.setChosenContact(new Contact(id, name, phone, null));
        myFragmentManager.setCurrentScreen(MyFragmentManager.Screen.valueOf(state.getString(CURRENT_SCREEN)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        myFragmentManager.addFragment(myFragmentManager.getCurrentScreen());
    }

    public void addNote() {
        MySharedPreferences mySharedPreferences = new MySharedPreferences(this);
        mySharedPreferences.resetInputContactData();
        mySharedPreferences.resetChosenCatagories();

        Intent intent = new Intent(this, CreateNoteActivity.class);
        startActivityForResult(intent, REQUEST_CODE_DRAWER_ITEM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        MenuItem item = navigationView.getMenu().findItem(data.getIntExtra(DRAWER_ITEM_POSITION, R.id.contacts));
        item.setCheckable(true);
        item.setChecked(true);
        myFragmentManager.setCurrentScreen(MyFragmentManager.Screen.valueOf(data.getStringExtra(DRAWER_ITEM_NAME)));
    }

    @Override
    protected void handleOnDrawerSlide() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void addItemsToSpinner() {

        contactsLoader =
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        String[] projection = { PhoneBookContentProvider.COLUMN_ID, PhoneBookContentProvider.COLUMN_NAME,
                                PhoneBookContentProvider.COLUMN_COLOR};
                        return new CursorLoader(getBaseContext(), PhoneBookContentProvider.CATEGORY_SOURCE_CONTENT_URI,
                                projection, null, null, null);
                    }
                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                        mAdapter.swapCursor(cursor);
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {
                        mAdapter.swapCursor(null);
                    }
                };

        String[] uiBindFrom = { PhoneBookContentProvider.COLUMN_NAME };
        int[] uiBindTo = { android.R.id.text1 };

        getSupportLoaderManager().initLoader(CATEGORY_LIST_LOADER, null, contactsLoader);

        mAdapter = new SimpleCursorAdapter(
                getBaseContext(), android.R.layout.simple_list_item_1,
                null, uiBindFrom, uiBindTo,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setText(cursor.getString(cursor.getColumnIndex(PhoneBookContentProvider.COLUMN_NAME)));
                tv.setTextColor(cursor.getInt(cursor.getColumnIndex(PhoneBookContentProvider.COLUMN_COLOR)));
            }
        };

        spinner_nav.setAdapter(mAdapter);

        spinner_nav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                Cursor cursor = (Cursor) adapter.getItemAtPosition(position);
                int category_id = cursor.getInt(cursor.getColumnIndex(PhoneBookContentProvider.COLUMN_ID));
                saveCategoryId(category_id);

                if (myFragmentManager.getCurrentScreen() == MyFragmentManager.Screen.CONTACTS) {
                    ContactsFragment contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentById(R.id.mainLayout);
                    contactsFragment.searchByCategory(category_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void saveCategoryId(int category_id) {
        MySharedPreferences mySharedPreferences = new MySharedPreferences(this);
        mySharedPreferences.saveSelectedFilterCategoryId(category_id);
    }

    public void addContactItemFragment(Contact contact) {
        myFragmentManager.setChosenContact(contact);
        myFragmentManager.addFragment(MyFragmentManager.Screen.DETAIL);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            switch (myFragmentManager.getCurrentScreen()) {
                case DETAIL:
                    myFragmentManager.addFragment(MyFragmentManager.Screen.CONTACTS);
                    break;
                default:
                    super.onBackPressed();
            }
        }
    }
}
