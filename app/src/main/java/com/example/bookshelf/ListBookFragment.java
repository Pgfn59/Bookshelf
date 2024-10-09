package com.example.bookshelf;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ListBookFragment extends Fragment {

    private RecyclerView bookRecyclerView;
    private ListBookAdapter adapter;
    private List<Book> bookList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookRecyclerView = view.findViewById(R.id.bookRecyclerView);
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookList = new ArrayList<>();
        adapter = new ListBookAdapter(bookList, book -> {
            ListBookDetailFragment detailFragment = new ListBookDetailFragment();
            Bundle args = new Bundle();
            args.putInt("BOOK_ID", book.getId());
            detailFragment.setArguments(args);
            detailFragment.show(getChildFragmentManager(), "ListBookDetailFragment");
        });
        bookRecyclerView.setAdapter(adapter);
        loadBookList();
    }

    private List<Book> getBooksFromDatabase() {
        List<Book> bookList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query("books", new String[]{"id", "image", "title", "author", "date", "yet", "rating", "thought"}, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                Book book = new Book(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("author")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("yet")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("rating")),
                        cursor.getString(cursor.getColumnIndexOrThrow("thought"))
                );
                bookList.add(book);
            }
        }
        return bookList;
    }
    public void loadBookList() {
        bookList = getBooksFromDatabase();
        adapter.updateBookList(bookList);
    }
}