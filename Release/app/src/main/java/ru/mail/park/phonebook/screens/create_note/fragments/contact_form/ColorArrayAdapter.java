package ru.mail.park.phonebook.screens.create_note.fragments.contact_form;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.mail.park.phonebook.screens.create_note.fragments.create_category.Category;

public class ColorArrayAdapter extends ArrayAdapter {
    private LayoutInflater mInflater;

    private String[] mStrings;
    private ArrayList<Category> chosenCategories;

    private int mViewResourceId;

    public ColorArrayAdapter(FragmentActivity ctx, int viewResourceId, String[] strings, ArrayList<Category> choosenCategories) {
        super(ctx, viewResourceId, strings);
        mInflater = (LayoutInflater)ctx.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        mStrings = strings;
        this.chosenCategories = choosenCategories;
        mViewResourceId = viewResourceId;
    }

    @Override
    public int getCount() {
        return mStrings.length;
    }

    @Override
    public String getItem(int position) {
        return mStrings[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);

        TextView tv = (TextView)convertView.findViewById(android.R.id.text1);
        tv.setText(mStrings[position]);
        tv.setTextColor((chosenCategories.get(position)).getColor());

        return convertView;
    }
}