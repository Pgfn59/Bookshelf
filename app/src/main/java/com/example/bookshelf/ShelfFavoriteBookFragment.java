package com.example.bookshelf;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

public class ShelfFavoriteBookFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.bookRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<Book> bookList = getBookListFromDatabase();
        ShelfFavoriteBookAdapter adapter = new ShelfFavoriteBookAdapter(bookList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(book -> {
            displayBookOnFavoriteShelf(book);
            dismiss();
        });
    }

    private List<Book> getBookListFromDatabase() {
        List<Book> bookList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query("books", new String[]{"id", "image", "title", "author"},null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                Book book = new Book(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        null,
                        0,
                        0f,
                        null
                );
                bookList.add(book);
            }
     }
        return bookList;
    }

    private void displayBookOnFavoriteShelf(Book book) {
        Bundle result = new Bundle();
        result.putInt("selectedBookId", book.getId());
        getParentFragmentManager().setFragmentResult("requestKey", result);
    }
}