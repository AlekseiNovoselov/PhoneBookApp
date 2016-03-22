package ru.mail.park.phonebook.screens.create_note.fragments.create_category;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;

import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.db.DBHelper;
import ru.mail.park.phonebook.screens.create_note.CreateNoteActivity;

public class CreateCategoryFragment extends Fragment {

    private final static String TOOLBAR_TEXT = "TOOLBAR_TEXT";
    private Button button;
    int toolbarText;

    AutoCompleteTextView autoCompleteTextView;
    int selectedColor = 0;

    public static CreateCategoryFragment newInstance(int toolbar_title) {
        CreateCategoryFragment createCategoryFragment = new CreateCategoryFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(TOOLBAR_TEXT, toolbar_title);
        createCategoryFragment.setArguments(arguments);
        return createCategoryFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        toolbarText = getArguments().getInt(TOOLBAR_TEXT);
        View v = inflater.inflate(R.layout.create_categoty_fragment, null);
        autoCompleteTextView = (AutoCompleteTextView) v.findViewById(R.id.categoty_name);

        button = (Button) v.findViewById(R.id.choose_color);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ColorPickerDialog colorPickerDialog = ColorPickerDialog.createColorPickerDialog(getActivity());
                colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
                    @Override
                    public void onColorPicked(int color, String hexVal) {
                        selectedColor = color;
                        autoCompleteTextView.setTextColor(color);
                    }
                });
                colorPickerDialog.setOnClosedListener(new ColorPickerDialog.OnClosedListener() {
                    @Override
                    public void onClosed() {
                        button.setEnabled(true);
                    }
                });
                button.setEnabled(false);
                colorPickerDialog.show();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CreateNoteActivity)getActivity()).getToolbar().setTitle(toolbarText);
        button.setEnabled(true);
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
        int id = item.getItemId();
        if ( id == R.id.action_done ) {
            if ( !autoCompleteTextView.getText().toString().equals("")) {
                Category category = new Category(autoCompleteTextView.getText().toString(),
                        autoCompleteTextView.getCurrentTextColor());
                DBHelper dbHelper = new DBHelper();
                if (dbHelper.insertCategory(getContext(), category)) {
                    handleSuccessfulInsert(category);
                }
            } else {
                String alertMessage = getActivity().getResources().getString(R.string.empty_category_add_alert);
                Toast.makeText(getContext(), alertMessage, Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void handleSuccessfulInsert(Category category) {
        String infoMessageFirstPart = getActivity().getResources().getString(R.string.success_category_add_text_begin);
        String infoMessageSecondPart = getActivity().getResources().getString(R.string.success_category_add_text_end);
        autoCompleteTextView.setText("");
        Toast.makeText(getContext(), infoMessageFirstPart + " " + category.getName() + " " +infoMessageSecondPart, Toast.LENGTH_SHORT).show();
        ((CreateNoteActivity)getActivity()).goBack();
    }
}
