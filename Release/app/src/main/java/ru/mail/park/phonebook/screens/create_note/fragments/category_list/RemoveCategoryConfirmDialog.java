package ru.mail.park.phonebook.screens.create_note.fragments.category_list;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.db.DBHelper;
import ru.mail.park.phonebook.screens.create_note.CreateNoteActivity;
import ru.mail.park.phonebook.screens.create_note.fragments.create_category.Category;

public class RemoveCategoryConfirmDialog {
    private AlertDialog.Builder dialog;

    private CreateNoteActivity activity;
    private View view;

    public RemoveCategoryConfirmDialog(CreateNoteActivity activity, View view) {
        this.activity = activity;
        this.view = view;
        buildDialog();
    }

    private void buildDialog() {

        TextView tv = (TextView) view.findViewById(android.R.id.text1);
        final Category category = new Category(tv.getText().toString(), tv.getCurrentTextColor());

        dialog = new AlertDialog.Builder(new ContextThemeWrapper(activity, android.R.style.Theme_DeviceDefault_Light_DarkActionBar));
        String message = activity.getResources().getString(R.string.remove_category_dialog_message);
        dialog.setTitle(R.string.remove_category_dialog_title);      // заголовок
        dialog.setMessage(message + " \"" + category.getName()+ "\" ?");  // сообщение
        dialog.setPositiveButton(R.string.remove_category_dialog_positive_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                removeCategory(category);
            }
        });
        dialog.setNegativeButton(R.string.remove_category_dialog_negative_button_text, new DialogInterface.OnClickListener() {
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

    private void removeCategory(Category category) {
        DBHelper dbHelper = new DBHelper();
        if (dbHelper.deleteCategory(activity.getBaseContext(), category)) {
            String infoMessageFirstPart = activity.getResources().getString(R.string.delete_category_alert_start);
            String infoMessageSecondPart = activity.getResources().getString(R.string.delete_category_alert_end);
            Toast.makeText(activity.getBaseContext(), infoMessageFirstPart + " \"" + category.getName()
                    + "\" " + infoMessageSecondPart + ".", Toast.LENGTH_SHORT).show();
        } else {
            String infoMessage = activity.getResources().getString(R.string.error_remove_category);
            Toast.makeText(activity.getBaseContext(), infoMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public void show() {
        dialog.show();
    }
}
