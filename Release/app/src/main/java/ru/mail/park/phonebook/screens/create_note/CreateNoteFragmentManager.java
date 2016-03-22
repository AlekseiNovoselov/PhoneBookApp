package ru.mail.park.phonebook.screens.create_note;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.screens.create_note.fragments.category_list.CategoryListFragment;
import ru.mail.park.phonebook.screens.create_note.fragments.contact_form.AddContactFormFragment;
import ru.mail.park.phonebook.screens.create_note.fragments.create_category.CreateCategoryFragment;

public class CreateNoteFragmentManager {

    private CreateNoteActivity activity;
    private Screen currentScreen = Screen.ADD_CONTACT_FROM;
    private Screen tmpScreen = Screen.EMPTY;

    public void setTmpScreen(Screen tmpScreen) {
        this.tmpScreen = tmpScreen;
    }

    public enum Screen {
        ADD_CONTACT_FROM, CATEGORIES_LIST, CREATE_CATEGORY, create_categories, EMPTY
    }

    public CreateNoteFragmentManager(CreateNoteActivity activity) {
        this.activity = activity;
    }

    public void addFragment(Screen categoriesList) {
        currentScreen = categoriesList;
        switch (currentScreen) {
            case ADD_CONTACT_FROM:
                addFragment(AddContactFormFragment.newInstance(R.string.create_node_toolbar_text), Screen.ADD_CONTACT_FROM, false);
                break;
            case CATEGORIES_LIST:
                addFragment(CategoryListFragment.newInstance(R.string.categories_list), Screen.CATEGORIES_LIST, false);
                break;
            case CREATE_CATEGORY:
                addFragment(CreateCategoryFragment.newInstance(R.string.create_categories), Screen.CREATE_CATEGORY, false);
                break;
            default:
                addFragment(AddContactFormFragment.newInstance(R.string.create_node_toolbar_text), Screen.ADD_CONTACT_FROM, false);
        }
    }

    private void addFragment(Fragment fragment, Screen screen, boolean addToBackStack) {
        FragmentTransaction fTran = activity.getSupportFragmentManager().beginTransaction();
        fTran.replace(R.id.mainLayout, fragment);
        if (addToBackStack) fTran.addToBackStack(null);
        fTran.commit();
        currentScreen = screen;
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public Screen getTmpScreen() {
        return tmpScreen;
    }
}
