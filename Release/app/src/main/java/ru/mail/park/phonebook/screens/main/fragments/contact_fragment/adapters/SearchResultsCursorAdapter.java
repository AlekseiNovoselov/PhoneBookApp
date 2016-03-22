package ru.mail.park.phonebook.screens.main.fragments.contact_fragment.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.db.PhoneBookContentProvider;

public class SearchResultsCursorAdapter extends RecyclerViewCursorAdapter<SearchResultsCursorAdapter.SearchResultViewHolder>

{
    private final LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;

    public SearchResultsCursorAdapter(final Context context)
    {
        super();

        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType)
    {
        final View view = this.layoutInflater.inflate(R.layout.item, parent, false);

        return new SearchResultViewHolder(view, new SearchResultViewHolder.IMyViewHolderClicks() {
            @Override
            public void onMyItemClick(View caller, String s, String toString) {
                final RecyclerView recyclerView = (RecyclerView) caller.getParent();
                final int position = recyclerView.getChildLayoutPosition(caller);
                if (position != RecyclerView.NO_POSITION)
                {
                    getCursor().moveToPosition(position);
                    final Cursor cursor = getCursor();
                    onItemClickListener.onItemClicked(cursor);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(final SearchResultViewHolder holder, final Cursor cursor)
    {
        holder.bindData(cursor);
    }

     /*
     * View.OnClickListener
     */

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public IMyViewHolderClicks mListener;
        TextView textViewName;
        TextView textViewPhoneNumber;

        public SearchResultViewHolder(final View itemView, IMyViewHolderClicks listener)
        {
            super(itemView);
            mListener = listener;
            textViewName = (TextView) itemView.findViewById(R.id.person_name);
            textViewPhoneNumber = (TextView) itemView.findViewById(R.id.person_phone_number);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindData(final Cursor cursor)
        {
            final String name = cursor.getString(cursor.getColumnIndex(PhoneBookContentProvider.COLUMN_NAME));
            final String phone_number = cursor.getString(cursor.getColumnIndex(PhoneBookContentProvider.COLUMN_PHONE_NUMBER));
            textViewName.setText(name);
            textViewPhoneNumber.setText(phone_number);
        }

        @Override
        public void onClick(View v) {
            mListener.onMyItemClick(v, textViewName.getText().toString(), textViewPhoneNumber.getText().toString());
        }

        public interface IMyViewHolderClicks {
            void onMyItemClick(View caller, String s, String toString);
        }
    }

    public interface OnItemClickListener
    {
        void onItemClicked(Cursor cursor);
    }
}
