package com.example.bookshelf;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;

public class ListBookDetailFragment extends Fragment {
    private ImageButton imageButton;
    private EditText titleEditText;
    private EditText authorEditText;
    private CheckBox yetCheckBox;
    private EditText dateEditText;
    private RatingBar ratingBar;
    private EditText thoughtEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_book_detail, container, false);
        imageButton = view.findViewById(R.id.imageButton);
        titleEditText = view.findViewById(R.id.editText);
        authorEditText = view.findViewById(R.id.editText2);
        yetCheckBox = view.findViewById(R.id.checkBox);
        dateEditText = view.findViewById(R.id.editTextDate);
        ratingBar = view.findViewById(R.id.ratingBar);
        thoughtEditText = view.findViewById(R.id.editText5);

        int bookId = getArguments().getInt("BOOK_ID", -1);
        if (bookId != -1) {
            Book book = getBookFromDatabase(bookId);
            if (book != null) {
                displayBookDetails(book);
            }
        }
        return view;
    }

    private void displayBookDetails(Book book) {
        if (book.image != null) {
            Glide.with(requireContext()).load(book.image).placeholder(android.R.drawable.ic_menu_gallery).error(android.R.drawable.ic_menu_gallery).into(imageButton);
        }
        titleEditText.setText(book.title);
        authorEditText.setText(book.author);
        yetCheckBox.setChecked(book.yet == 1);
        dateEditText.setText(book.date);
        float ratingValue = book.rating != null ? book.rating : 0f;
        ratingBar.setRating(ratingValue);
        thoughtEditText.setText(book.thought);
    }

    private Book getBookFromDatabase(int bookId) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Book book = null;

        try {
            cursor = db.query("books", null, "id = ?", new String[]{String.valueOf(bookId)}, null, null, null);
            if (cursor.moveToFirst()) {
                book = new Book();
                book.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                book.image = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                book.title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                book.author = cursor.getString(cursor.getColumnIndexOrThrow("author"));
                book.date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                book.yet = cursor.getInt(cursor.getColumnIndexOrThrow("yet"));
                book.rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));
                book.thought = cursor.getString(cursor.getColumnIndexOrThrow("thought"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return book;
    }
}