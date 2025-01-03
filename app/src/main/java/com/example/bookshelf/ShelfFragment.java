package com.example.bookshelf;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Calendar;

public class ShelfFragment extends Fragment {
    private LinearLayout bookshelfRow1;
    private LinearLayout bookshelfRow2;
    private LinearLayout bookshelfRow3;
    private DatabaseHelper dbHelper;
    private int currentRow = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shelf, container, false);
    }

    //本棚
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookshelfRow1 = view.findViewById(R.id.shelfLayout1);
        bookshelfRow2 = view.findViewById(R.id.shelfLayout2);
        bookshelfRow3 = view.findViewById(R.id.shelfLayout3);
        bookshelfRow1.setGravity(Gravity.BOTTOM);
        bookshelfRow2.setGravity(Gravity.BOTTOM);
        bookshelfRow3.setGravity(Gravity.BOTTOM);
        dbHelper = new DatabaseHelper(requireContext());
        bookshelfRow1.post(this::loadBooksFromDatabase);
        bookshelfRow1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bookshelfRow1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                loadBooksFromDatabase();
            }
        });
    }

    private void loadBooksFromDatabase() {
        clearBookshelves();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
            String query = "SELECT * FROM books WHERE date LIKE '" + year + "/" + String.format("%02d", month) + "/%' ORDER BY date";
            try (Cursor cursor = db.rawQuery(query,null)){
                while (cursor.moveToNext()){
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    ImageView bookView = createBookView(imagePath, title);
                    addToCurrentRow(bookView);
                }
            }
        }
    }

    private ImageView createBookView(String imagePath, String title) {
        ImageView bookView = new ImageView(requireContext());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int bookWidth = screenWidth / 3;
        int bookHeight = bookWidth * 4 / 3;

        if (imagePath != null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(bookWidth, bookHeight);
            bookView.setLayoutParams(params);
            Glide.with(requireContext()).load(imagePath).override(bookWidth, bookHeight).centerCrop().into(bookView);
            bookView.setScaleType(ImageView.ScaleType.FIT_END);
        } else {
            View spineView = getLayoutInflater().inflate(R.layout.sub_shelf_book, null);
            TextView titleTextView = spineView.findViewById(R.id.title_vertical);
            titleTextView.setText(title);

            int linearLayoutHeight = bookshelfRow1.getHeight();
            int imageViewHeight = linearLayoutHeight - 50;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, imageViewHeight);
            spineView.setLayoutParams(params);

            spineView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(imageViewHeight, View.MeasureSpec.EXACTLY));
            spineView.layout(0, 0, spineView.getMeasuredWidth(), spineView.getMeasuredHeight());
            Bitmap bitmap = Bitmap.createBitmap(spineView.getMeasuredWidth(), spineView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            spineView.draw(canvas);
            bookView.setImageBitmap(bitmap);
            bookView.setScaleType(ImageView.ScaleType.FIT_END);
        }

        bookView.setOnClickListener(v -> {
            ListBookDetailFragment dialogFragment = new ListBookDetailFragment();
            Bundle args = new Bundle();
            int bookId = getBookIdFromImagePathOrTitle(imagePath, title);
            args.putInt("BOOK_ID", bookId);
            dialogFragment.setArguments(args);
            dialogFragment.show(getParentFragmentManager(), "book_details");
        });

        return bookView;
    }

    private int getBookIdFromImagePathOrTitle(String imagePath, String title) {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query("books", new String[]{"id"}, "title = ?", new String[]{title}, null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return -1;
    }

    private void addToCurrentRow(ImageView bookView) {
        LinearLayout[] bookshelfRows = {bookshelfRow1, bookshelfRow2, bookshelfRow3};
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        bookView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int bookWidth = bookView.getMeasuredWidth();

        if (currentRow <= bookshelfRows.length) {
            LinearLayout row = bookshelfRows[currentRow - 1];
            int totalWidth = 0;
            for (int i = 0; i < row.getChildCount(); i++) {
                totalWidth += row.getChildAt(i).getMeasuredWidth();
            }

            if (totalWidth + bookWidth <= screenWidth) {
                row.addView(bookView);
            } else {
                currentRow++;
                addToCurrentRow(bookView);
            }
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