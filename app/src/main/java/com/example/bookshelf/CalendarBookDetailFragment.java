package com.example.bookshelf;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class CalendarBookDetailFragment extends Fragment {

    public CalendarBookDetailFragment() {
        super(R.layout.fragment_list_book_detail);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int bookId = getArguments() != null ? getArguments().getInt("BOOK_ID", -1) : -1;
        if (bookId == -1) {
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"id", "image", "title", "author", "date", "yet", "rating", "thought"};
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(bookId)};

        try (Cursor cursor = db.query("books", columns, selection, selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                String image = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String author = cursor.getString(cursor.getColumnIndexOrThrow("author"));
                int yet = cursor.getInt(cursor.getColumnIndexOrThrow("yet"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                float rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));
                String thought = cursor.getString(cursor.getColumnIndexOrThrow("thought"));

                ImageButton imageButton = view.findViewById(R.id.imageButton);
                EditText titleEditText = view.findViewById(R.id.editText);
                EditText authorEditText = view.findViewById(R.id.editText2);
                CheckBox yetCheckBox = view.findViewById(R.id.checkBox);
                EditText dateEditText = view.findViewById(R.id.editTextDate);
                RatingBar ratingBar = view.findViewById(R.id.ratingBar);
                EditText thoughtEditText = view.findViewById(R.id.editText5);

                imageButton.setImageResource(android.R.drawable.ic_menu_gallery);
                titleEditText.setText(title);
                authorEditText.setText(author);
                yetCheckBox.setChecked(yet == 1);
                dateEditText.setText(date);
                ratingBar.setRating(rating);
                thoughtEditText.setText(thought);
            }
        } finally {
            db.close();
        }
    }
}
