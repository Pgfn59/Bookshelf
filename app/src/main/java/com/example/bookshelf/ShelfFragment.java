package com.example.bookshelf;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.Arrays;

public class ShelfFragment extends Fragment {
    private LinearLayout bookshelfRow1;
    private LinearLayout bookshelfRow2;
    private LinearLayout bookshelfRow3;
    private DatabaseHelper dbHelper;
    private int currentRow = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //画面遷移　書籍登録
        View view = inflater.inflate(R.layout.fragment_shelf, container, false);
        view.findViewById(R.id.btn_add_book).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_shelfFragment_to_addBookFragment);;
        });
        //画面遷移　記録一覧
        view.findViewById(R.id.btn_record).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_shelfFragment_to_recordFragment);
        });
        return view;
    }

    //本棚
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookshelfRow1 = view.findViewById(R.id.ShelfLayout1);
        bookshelfRow2 = view.findViewById(R.id.ShelfLayout2);
        bookshelfRow3 = view.findViewById(R.id.ShelfLayout3);
        dbHelper = new DatabaseHelper(requireContext());
        loadBooksFromDatabase();
    }

    private void loadBooksFromDatabase() {
        clearBookshelves();
        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
            String query = "SELECT * FROM books";
            try (Cursor cursor = db.rawQuery(query,null)){
                while (cursor.moveToNext()){
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                    if (imagePath != null) {
                        ImageView bookView = createBookView(imagePath);
                        addToCurrentRow(bookView);
                    }
                }
            }
        }
    }

    private ImageView createBookView(String imagePath) {
        ImageView bookView = new ImageView(requireContext());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int numBooksInRow = 3;
        int bookWidth = screenWidth / numBooksInRow;
        int bookHeight = bookWidth * 4 / 3;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(bookWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        bookView.setLayoutParams(params);

        Glide.with(requireContext()).load(imagePath).override(bookWidth, bookHeight).centerCrop().into(bookView);

        return bookView;
    }

    private void addToCurrentRow(ImageView bookView) {
        int maxBooks = 5;
        LinearLayout[] bookshelfRows = {bookshelfRow1, bookshelfRow2, bookshelfRow3};

        int rowIndex = (currentRow - 1) % bookshelfRows.length;
        LinearLayout row = bookshelfRows[rowIndex];
        if (row.getChildCount() < maxBooks) {
            row.addView(bookView);
        } else {
            currentRow++;
            addToCurrentRow(bookView);
        }
    }

    private void clearBookshelves() {
        LinearLayout[] bookshelfRows = {bookshelfRow1, bookshelfRow2, bookshelfRow3};
        for (LinearLayout row : bookshelfRows) {
            row.removeAllViews();
        }
        currentRow = 1;
    }
}