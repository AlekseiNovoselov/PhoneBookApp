package ru.mail.park.phonebook.screens.create_note.fragments.category_list;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.db.PhoneBookContentProvider;
import ru.mail.park.phonebook.screens.main.BaseActivity;
import ru.mail.park.phonebook.screens.create_note.CreateNoteActivity;
import ru.mail.park.phonebook.screens.create_note.fragments.create_category.Category;
import ru.mail.park.phonebook.utils.MySharedPreferences;

public class CategoryListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemLongClickListener{

    private final static String TOOLBAR_TEXT = "TOOLBAR_TEXT";
    int toolbarText;
    private SimpleCursorAdapter mAdapter;
    private static final int CATEGORY_LIST_LOADER = 0x01;


    public static CategoryListFragment newInstance(int toolbar_title) {
        CategoryListFragment aboutFragment = new CategoryListFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(TOOLBAR_TEXT , toolbar_title);
        aboutFragment.setArguments(arguments);
        return aboutFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        toolbarText = getArguments().getInt(TOOLBAR_TEXT);
        View view =  inflater.inflate(R.layout.fragment_category_list, null);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CreateNoteActivity) getActivity()).createNewCategory();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] uiBindFrom = { PhoneBookContentProvider.COLUMN_NAME };
        int[] uiBindTo = { android.R.id.text1 };

        getLoaderManager().initLoader(CATEGORY_LIST_LOADER, null, this);

        mAdapter = new SimpleCursorAdapter(
                getActivity().getBaseContext(), android.R.layout.select_dialog_multichoice,
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
        setListAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CreateNoteActivity)getActivity()).getToolbar().setTitle(toolbarText);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.create_node_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ArrayList<Category> selectedCategories = new ArrayList<>();
        switch (item.getItemId()) {
            case R.id.action_done:
                int count = getListView().getCount();
                SparseBooleanArray sparseBooleanArray = getListView().getCheckedItemPositions();
                for (int i = 0; i < count; i++){
                    if (sparseBooleanArray.get(i)) {
                        Cursor cursor = (Cursor) getListView().getItemAtPosition(i);
                        Category category = new Category(cursor.getString(cursor.getColumnIndex(PhoneBookContentProvider.COLUMN_NAME)),
                                cursor.getInt(cursor.getColumnIndex(PhoneBookContentProvider.COLUMN_COLOR)));
                        selectedCategories.add(category);
                    }
                }
                MySharedPreferences mySharedPreferences = new MySharedPreferences((BaseActivity)getActivity());
                mySharedPreferences.saveSelectedCategories(selectedCategories);
                ((CreateNoteActivity)getActivity()).goBack();
                break;
        }


        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { PhoneBookContentProvider.COLUMN_ID, PhoneBookContentProvider.COLUMN_NAME,
                PhoneBookContentProvider.COLUMN_COLOR};
        String selection = PhoneBookContentProvider.COLUMN_ID + " != ? ";
        String[] selectionArgs = { "1" };
        return new CursorLoader(getActivity(), PhoneBookContentProvider.CATEGORY_SOURCE_CONTENT_URI,
                projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        mAdapter.swapCursor(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        RemoveCategoryConfirmDialog removeConfirmDialog = new RemoveCategoryConfirmDialog((CreateNoteActivity)getActivity(), view);
        removeConfirmDialog.show();
        return false;
    }
}
