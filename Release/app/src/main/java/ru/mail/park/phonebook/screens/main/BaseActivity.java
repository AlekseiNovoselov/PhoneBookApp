package ru.mail.park.phonebook.screens.main;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.screens.main.MyFragmentManager;
import ru.mail.park.phonebook.screens.main.fragments.settings_fragment.SettingsFragment;


public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected final static String DRAWER_ITEM_POSITION = "DRAWER_ITEM_POSITION";
    protected final static String DRAWER_ITEM_NAME = "DRAWER_ITEM_NAME";

    protected MyFragmentManager myFragmentManager;

    protected Toolbar toolbar;
    protected NavigationView navigationView;
    protected DrawerLayout drawer;

    protected void initialize() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar != null) {
            if (getSupportActionBar() != null) {
                // TODO - этот костыль почему-то решает проблему отображения title у Toolbar сразу
                getSupportActionBar().setTitle(" ");
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            // TODO - используется вместо индикатора появления шторки, но в то же время реагирует на другие состояния
            @Override
            public void onDrawerStateChanged(int newState) {
                handleOnDrawerSlide();
                super.onDrawerStateChanged(newState);
            }
        };
        toggle.syncState();
        drawer.setDrawerListener(toggle);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected abstract void handleOnDrawerSlide();

    protected void preferenceTheme() {
        switch (SettingsFragment.Theme.valueOf(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(("pref_dark_theme"), SettingsFragment.Theme.LIGHT.toString()))) {
            case DARK:
                setTheme(R.style.AppTheme_Dark);
                break;
            default:
                //setTheme(R.style.AppTheme);
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}
