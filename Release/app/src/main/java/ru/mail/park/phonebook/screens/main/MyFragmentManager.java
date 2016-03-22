package ru.mail.park.phonebook.screens.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Spinner;

import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.screens.create_note.fragments.contact_form.Contact;
import ru.mail.park.phonebook.screens.main.fragments.about_fragment.AboutFragment;
import ru.mail.park.phonebook.screens.main.fragments.contact_details_fragment.ContactDetailFragment;
import ru.mail.park.phonebook.screens.main.fragments.contact_fragment.ContactsFragment;
import ru.mail.park.phonebook.screens.main.fragments.settings_fragment.SettingsFragment;

public class MyFragmentManager {

    private MainActivity activity;
    private Toolbar toolbar = null;
    private Spinner spinner = null;
    private Screen currentScreen = Screen.EMPTY;
    private Contact chosenContact = null;

    public void setChosenContact(Contact chosenContact) {
        this.chosenContact = chosenContact;
    }

    public Contact getChosenContact() {
        return chosenContact;
    }

    public void setCurrentScreen(Screen currentScreen) {
        this.currentScreen = currentScreen;
    }

    public enum Screen {
        CONTACTS, SETTINGS, ABOUT, DETAIL, EMPTY
    }

    public MyFragmentManager(MainActivity activity, Toolbar toolbar, Spinner spinner_nav) {
        this.activity = activity;
        this.toolbar = toolbar;
        this.spinner = spinner_nav;
    }

    public void addFragment(Screen value) {
        currentScreen = value;
        switch (currentScreen) {
            case CONTACTS:
                addFragment(R.string.empty_string, ContactsFragment.newInstance(), Screen.CONTACTS, false);
                spinner.setVisibility(View.VISIBLE);
                break;
            case SETTINGS:
                addFragment(R.string.toolbar_settings_item, SettingsFragment.newInstance(), Screen.SETTINGS, false);
                spinner.setVisibility(View.GONE);
                break;
            case ABOUT:
                addFragment(R.string.toolbar_about_item, AboutFragment.newInstance(), Screen.ABOUT, false);
                spinner.setVisibility(View.GONE);
                break;
            case DETAIL:
                addFragment(R.string.toolbar_detail_item, ContactDetailFragment.newInstance(getChosenContact()), Screen.DETAIL, false);
                spinner.setVisibility(View.GONE);
                break;
            default:
                addFragment(R.string.empty_string, ContactsFragment.newInstance(), Screen.CONTACTS, false);
        }
    }

    private void addFragment(int title, Fragment fragment, Screen screen, boolean addToBackStack) {
        toolbar.setTitle(title);
        FragmentTransaction fTran = activity.getSupportFragmentManager().beginTransaction();
        fTran.replace(R.id.mainLayout, fragment);
        if (addToBackStack) fTran.addToBackStack(null);
        fTran.commit();
        currentScreen = screen;
    }
    public Screen getCurrentScreen() {
        return currentScreen;
    }
}
