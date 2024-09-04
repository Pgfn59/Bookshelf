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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookRecyclerView = view.findViewById(R.id.bookRecyclerView);
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<Book> bookList = getBooksFromDatabase();
        adapter = new ListBookAdapter(bookList, new ListBookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                ListBookDetailFragment detailFragment = new ListBookDetailFragment();
                Bundle args = new Bundle();
                args.putInt("BOOK_ID", book.id);
                detailFragment.setArguments(args);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        bookRecyclerView.setAdapter(adapter);
    }

    private List<Book> getBooksFromDatabase() {
        List<Book> bookList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] columns = {"id", "image", "title", "author", "date", "yet", "rating", "thought"};
            cursor = db.query("books", columns, null, null, null, null, null);

            while (cursor.moveToNext()) {
                Book book = new Book();
                book.id = cursor.getInt(0);
                book.image = cursor.getString(1);
                book.title = cursor.getString(2);
                book.author = cursor.getString(3);
                book.date = cursor.getString(4);
                book.yet = cursor.getInt(5);
                book.rating = cursor.getFloat(6);
                book.thought = cursor.getString(7);
                bookList.add(book);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return bookList;
    }
}