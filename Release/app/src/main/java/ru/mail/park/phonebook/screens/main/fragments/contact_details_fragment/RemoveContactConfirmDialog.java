package ru.mail.park.phonebook.screens.main.fragments.contact_details_fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.view.ContextThemeWrapper;
import android.widget.Toast;

import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.db.DBHelper;
import ru.mail.park.phonebook.screens.create_note.fragments.contact_form.Contact;
import ru.mail.park.phonebook.screens.main.MainActivity;

public class RemoveContactConfirmDialog {
    private AlertDialog.Builder dialog;

    private MainActivity activity;
    private Contact contact;

    public RemoveContactConfirmDialog(MainActivity activity, Contact contact) {
        this.activity = activity;
        this.contact = contact;
        buildDialog();
    }

    private void buildDialog() {


        dialog = new AlertDialog.Builder(new ContextThemeWrapper(activity, android.R.style.Theme_DeviceDefault_Light_DarkActionBar));
        String message = activity.getResources().getString(R.string.remove_contact_dialog_message);
        dialog.setTitle(R.string.remove_contact_dialog_title);      // заголовок
        dialog.setMessage(message + " \"" + contact.getName()+ "\" ?");  // сообщение
        dialog.setPositiveButton(R.string.remove_contact_dialog_positive_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                deleteContact();
            }
        });
        dialog.setNegativeButton(R.string.remove_contact_dialog_negative_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                // ignore
            }
        });
        dialog.setCancelable(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                // ignore
            }
        });
    }

    private void deleteContact() {
        DBHelper dbHelper = new DBHelper();
        if (dbHelper.deleteContact(activity.getBaseContext(), contact.getId())) {
            String infoMessageFirstPart = activity.getResources().getString(R.string.delete_contact_success_text_start);
            String infoMessageSecondPart = activity.getResources().getString(R.string.delete_contact_success_text_end);
            Toast.makeText(activity.getBaseContext(), infoMessageFirstPart + " \"" + contact.getName()
                    + "\" " + infoMessageSecondPart + ".", Toast.LENGTH_SHORT).show();
        } else {
            String infoMessage = activity.getResources().getString(R.string.error_delete_contact);
            Toast.makeText(activity.getBaseContext(), infoMessage, Toast.LENGTH_SHORT).show();
        }
        activity.onBackPressed();
    }

    public void show() {
        dialog.show();
    }
}
