package ru.mail.park.phonebook.screens.create_note;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.screens.main.BaseActivity;
import ru.mail.park.phonebook.screens.main.MyFragmentManager;

public class CreateNoteActivity extends BaseActivity {

    public final static String CURRENT_SCREEN = "ru.mail.park.phoneBook.CURRENT_SCREEN";
    CreateNoteFragmentManager createNoteFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_node);
        initialize();

        createNoteFragmentManager = new CreateNoteFragmentManager(CreateNoteActivity.this);
        createNoteFragmentManager.addFragment(CreateNoteFragmentManager.Screen.ADD_CONTACT_FROM);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        MyFragmentManager.Screen currentScreen;
        switch (item.getItemId()) {
            case R.id.contacts:
                currentScreen = MyFragmentManager.Screen.CONTACTS;
                break;
            case R.id.settings:
                currentScreen = MyFragmentManager.Screen.SETTINGS;
                break;
            case R.id.about:
                currentScreen = MyFragmentManager.Screen.ABOUT;
                break;
            default:
                currentScreen = MyFragmentManager.Screen.CONTACTS;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        Intent intent = new Intent();
        intent.putExtra(DRAWER_ITEM_NAME, currentScreen.toString());
        intent.putExtra(DRAWER_ITEM_POSITION, item.getItemId());
        setResult(RESULT_OK, intent);

        finish();

        return true;
    }

    @Override
    protected void handleOnDrawerSlide() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void addCategoryListFragment() {
        handleOnDrawerSlide();
        createNoteFragmentManager.addFragment(CreateNoteFragmentManager.Screen.CATEGORIES_LIST);
    }

    public void createNewCategory() {
        createNoteFragmentManager.addFragment(CreateNoteFragmentManager.Screen.CREATE_CATEGORY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createNoteFragmentManager.addFragment(createNoteFragmentManager.getTmpScreen());
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(CURRENT_SCREEN, createNoteFragmentManager.getCurrentScreen().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        createNoteFragmentManager.setTmpScreen(CreateNoteFragmentManager.Screen.valueOf(savedInstanceState.getString(CURRENT_SCREEN)));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            switch (createNoteFragmentManager.getCurrentScreen()) {
                case ADD_CONTACT_FROM:
                    super.onBackPressed();
                    break;
                default:
                    goBack();
            }
        }
    }

    public void goBack() {
        switch (createNoteFragmentManager.getCurrentScreen()) {
            case ADD_CONTACT_FROM:
                super.onBackPressed();
                break;
            case CATEGORIES_LIST:
                createNoteFragmentManager.addFragment(CreateNoteFragmentManager.Screen.ADD_CONTACT_FROM);
                break;
            case CREATE_CATEGORY:
                createNoteFragmentManager.addFragment(CreateNoteFragmentManager.Screen.CATEGORIES_LIST);
                break;
        }
    }
}
