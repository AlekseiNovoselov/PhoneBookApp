package ru.mail.park.phonebook.screens.main.fragments.about_fragment.utils;

import android.app.Application;

/**
 * Provide application-level dependencies. Mainly singleton object that can be injected from
 * anywhere in the app.
 */

public class ApplicationModule {
    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    Application provideApplication() {
        return mApplication;
    }

}
