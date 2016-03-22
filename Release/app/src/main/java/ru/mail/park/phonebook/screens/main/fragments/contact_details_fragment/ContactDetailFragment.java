package ru.mail.park.phonebook.screens.main.fragments.contact_details_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.screens.create_note.fragments.contact_form.Contact;
import ru.mail.park.phonebook.screens.create_note.fragments.create_category.Category;
import ru.mail.park.phonebook.screens.main.MainActivity;

public class ContactDetailFragment extends Fragment  {

    private Contact contact;

    private static final String CONTACT_ID = "CONTACT_ID";
    private static final String CONTACT_NAME = "CONTACT_NAME";
    private static final String CONTACT_PHONE = "CONTACT_PHONE";

    public static ContactDetailFragment newInstance(Contact contact) {
        ContactDetailFragment aboutFragment = new ContactDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(CONTACT_ID, contact.getId());
        arguments.putString(CONTACT_NAME, contact.getName());
        arguments.putString(CONTACT_PHONE, contact.getPhoneNumber());
        aboutFragment.setArguments(arguments);
        return aboutFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int contactId = getArguments().getInt(CONTACT_ID);
        String contactName = getArguments().getString(CONTACT_NAME);
        String phone_number = getArguments().getString(CONTACT_PHONE);
        contact = new Contact(contactId, contactName, phone_number, new ArrayList<Category>());
        View v = inflater.inflate(R.layout.fragment_detail, null);
        TextView tvName = (TextView) v.findViewById(R.id.person_name);
        TextView tvPhoneNumber = (TextView) v.findViewById(R.id.person_phone_number);
        tvPhoneNumber.setText(phone_number);
        tvName.setText(contactName);
        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.share_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, buildString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.action_delete:
                RemoveContactConfirmDialog removeConfirmDialog = new RemoveContactConfirmDialog((MainActivity)getActivity(), contact);
                removeConfirmDialog.show();
        }
        return true;
    }

    private String buildString() {
        String contactName = getActivity().getResources().getString(R.string.share_contact_name);
        String phoneNumber = getActivity().getResources().getString(R.string.share_phone_number);
        return contactName + " " + contact.getName() + "\n" + phoneNumber + contact.getPhoneNumber() + ".";
    }
}
