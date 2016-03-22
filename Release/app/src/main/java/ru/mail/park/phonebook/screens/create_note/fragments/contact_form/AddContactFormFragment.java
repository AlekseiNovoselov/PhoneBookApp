package ru.mail.park.phonebook.screens.create_note.fragments.contact_form;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.db.DBHelper;
import ru.mail.park.phonebook.screens.main.BaseActivity;
import ru.mail.park.phonebook.screens.create_note.CreateNoteActivity;
import ru.mail.park.phonebook.utils.MySharedPreferences;

public class AddContactFormFragment extends Fragment {

    private final static String TOOLBAR_TEXT = "TOOLBAR_TEXT";
    int toolbarText;

    final String LOG_TAG = "myLogs";

    ListView lvMain;
    View footer1;

    EditText phoneNumber;
    AutoCompleteTextView name;

    ColorArrayAdapter adapter;

    public static AddContactFormFragment newInstance(int toolbar_text) {
        AddContactFormFragment addContactFormFragment = new AddContactFormFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(TOOLBAR_TEXT, toolbar_text);
        addContactFormFragment.setArguments(arguments);
        return addContactFormFragment;
    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        toolbarText = getArguments().getInt(TOOLBAR_TEXT);
        View v = inflater.inflate(R.layout.fragment_add_contact_form, null);
        phoneNumber = (EditText) v.findViewById(R.id.phone_number);
        name = (AutoCompleteTextView) v.findViewById(R.id.contact_name);

        MySharedPreferences mySharedPreferences = new MySharedPreferences((BaseActivity)getActivity());
        Contact contact = mySharedPreferences.getSavedInputData();
        name.setText(contact.getName());
        phoneNumber.setText(contact.getPhoneNumber());

        lvMain = (ListView) v.findViewById(R.id.lvMain);
        adapter = new ColorArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                mySharedPreferences.getChoosenCategoriesNames(), mySharedPreferences.getChoosenCategories());

        footer1 = createFooter();
        footer1.setClickable(true);

        fillList();

        Button forward = (Button) footer1.findViewById(R.id.add_category);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CreateNoteActivity) getActivity()).addCategoryListFragment();
            }
        });

        return v;
    }

    View createFooter() {
        return getActivity().getLayoutInflater().inflate(R.layout.footer, null);
    }

    void fillList() {
        try {
            lvMain.setAdapter(adapter);
            lvMain.addFooterView(footer1);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
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
        switch (item.getItemId()) {
            case R.id.action_done:
                item.setEnabled(false);
                String contact_name = name.getText().toString();
                String phone_number = phoneNumber.getText().toString();
                if (contact_name.equals("") || phone_number.equals("")) {
                    String infoMessageFirstPart = getActivity().getResources().getString(R.string.empty_contact_add_alert);
                    Toast.makeText(getContext(), infoMessageFirstPart, Toast.LENGTH_SHORT).show();
                    item.setEnabled(true);
                    break;
                }
                MySharedPreferences mySharedPreferences = new MySharedPreferences((BaseActivity)getActivity());
                Contact contact = new Contact(-1, contact_name, phone_number, mySharedPreferences.getChoosenCategories());
                DBHelper dbhelper = new DBHelper();
                if (dbhelper.insertContact(getContext(), contact)) {
                    String infoMessageFirstPart = getActivity().getResources().getString(R.string.success_contact_create_text_begin);
                    String infoMessageSecondPart = getActivity().getResources().getString(R.string.success_contact_create_text_end);
                    Toast.makeText(getContext(), infoMessageFirstPart + " " + contact_name + " " + infoMessageSecondPart, Toast.LENGTH_SHORT).show();
                    name.setText("");
                    phoneNumber.setText("");
                    getActivity().onBackPressed();
                }
                else {
                    String unExpectedErrorString = getActivity().getResources().getString(R.string.error_write_to_db);
                    Toast.makeText(getContext(), unExpectedErrorString, Toast.LENGTH_SHORT).show();
                }
                item.setEnabled(true);
                break;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        MySharedPreferences mySharedPreferences = new MySharedPreferences((BaseActivity)getActivity());
        String contact_name = name.getText().toString();
        String phone_number = phoneNumber.getText().toString();
        mySharedPreferences.saveInputContactData(contact_name, phone_number);
        super.onDestroy();
    }
}
