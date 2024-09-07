package com.example.bookshelf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class CalendarBookAdapter extends ArrayAdapter<Book> {

    public CalendarBookAdapter(Context context, List<Book> books) {
        super(context,0, books);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Book book = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sub_list_book, parent, false);
        }
        TextView titleTextView = convertView.findViewById(R.id.bookTitleTextView);
        TextView authorTextView = convertView.findViewById(R.id.bookAuthorTextView);

        titleTextView.setText(book.title);
        authorTextView.setText(book.author);

        return convertView;
    }

    public void updateBookList(List<Book> newBookList) {
        clear();
        addAll(newBookList);
        notifyDataSetChanged();
    }
}
