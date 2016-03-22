package ru.mail.park.phonebook.screens.main.fragments.contact_fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.db.PhoneBookContentProvider;
import ru.mail.park.phonebook.screens.main.BaseActivity;
import ru.mail.park.phonebook.screens.create_note.fragments.contact_form.Contact;
import ru.mail.park.phonebook.screens.main.MainActivity;
import ru.mail.park.phonebook.screens.main.fragments.contact_fragment.adapters.SearchResultsCursorAdapter;
import ru.mail.park.phonebook.utils.MySharedPreferences;

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,SearchResultsCursorAdapter.OnItemClickListener {

    private static final int LOADER_SEARCH_RESULTS = 1;
    private static final int LOADER_SEARCH_BY_CATEGORY_RESULTS = 2;

    private SearchResultsCursorAdapter adapter;
    private int currentCategory_id = 0;

    public static ContactsFragment newInstance() {
        ContactsFragment contactsFragment = new ContactsFragment();
        Bundle arguments = new Bundle();
        contactsFragment.setArguments(arguments);
        return contactsFragment;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, null);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);

        this.adapter = new SearchResultsCursorAdapter(getContext());
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        MySharedPreferences mySharedPreferences = new MySharedPreferences((BaseActivity) getActivity());
        int category_id = mySharedPreferences.getSelectedFilterCategory();
        searchByCategory(category_id);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).addNote();
            }
        });

        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args)
    {
        String orderBy = PhoneBookContentProvider.COLUMN_ID + " DESC";
        switch (id)
        {
            case LOADER_SEARCH_RESULTS:
                String[] projection1 = { PhoneBookContentProvider.COLUMN_ID, PhoneBookContentProvider.COLUMN_NAME,
                        PhoneBookContentProvider.COLUMN_PHONE_NUMBER};
                return new CursorLoader(getContext(), PhoneBookContentProvider.CONTACT_SOURCE_CONTENT_URI, projection1, null, null, orderBy);

            case LOADER_SEARCH_BY_CATEGORY_RESULTS:
                String[] projection2 = { PhoneBookContentProvider.COLUMN_ID, PhoneBookContentProvider.COLUMN_NAME,
                        PhoneBookContentProvider.COLUMN_PHONE_NUMBER};
                String[] categoryID = { String.valueOf(currentCategory_id) };
                return new CursorLoader(getContext(), PhoneBookContentProvider.CONTACT_SOURCE_BY_CATEGORY_CONTENT_URI, projection2, null, categoryID, orderBy);
        }
        return null;
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor c)
    {
        switch (loader.getId())
        {
            case LOADER_SEARCH_RESULTS:
                this.adapter.swapCursor(c);
                break;
            case LOADER_SEARCH_BY_CATEGORY_RESULTS:
                this.adapter.swapCursor(c);
                break;
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader)
    {
        switch (loader.getId())
        {
            case LOADER_SEARCH_RESULTS:
                this.adapter.swapCursor(null);
                break;
            case LOADER_SEARCH_BY_CATEGORY_RESULTS:
                this.adapter.swapCursor(null);
                break;
        }
    }

    public void searchByCategory(int category_id) {
        currentCategory_id = category_id;
        if (category_id > 1) {
            this.getLoaderManager().restartLoader(LOADER_SEARCH_BY_CATEGORY_RESULTS, null, this);
        } else {
            this.getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
        }
    }

    @Override
    public void onItemClicked(Cursor cursor) {
        final String name = cursor.getString(cursor.getColumnIndex(PhoneBookContentProvider.COLUMN_NAME));
        final String phone_number = cursor.getString(cursor.getColumnIndex(PhoneBookContentProvider.COLUMN_PHONE_NUMBER));
        final int id = cursor.getInt(cursor.getColumnIndex(PhoneBookContentProvider.COLUMN_ID));
        Contact contact = new Contact(id, name, phone_number, null);
        ((MainActivity) getActivity()).addContactItemFragment(contact);
    }
}

