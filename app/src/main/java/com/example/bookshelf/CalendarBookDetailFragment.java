package com.example.bookshelf;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

public class CalendarBookDetailFragment extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            dismiss();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.bookDetailToolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_book_detail, container, false);
        int bookId = getArguments() != null ? getArguments().getInt("BOOK_ID", -1) : -1;
        if (bookId == -1) {
            dismiss();
            return view;
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
        return view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
