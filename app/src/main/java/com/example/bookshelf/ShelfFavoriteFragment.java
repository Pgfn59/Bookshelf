package com.example.bookshelf;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShelfFavoriteFragment extends Fragment {
    private boolean isEditing = false;
    private Button bookButton;
    private Button itemButton;
    private Button editButton;
    private List<Book> favoriteBookList1 = new ArrayList<>();
    private List<Book> favoriteBookList2 = new ArrayList<>();
    private List<Book> favoriteBookList3 = new ArrayList<>();
    private ShelfFavoriteAdapter shelfFavoriteAdapter1;
    private ShelfFavoriteAdapter shelfFavoriteAdapter2;
    private ShelfFavoriteAdapter shelfFavoriteAdapter3;
    private RecyclerView favoriteBookRecyclerView1;
    private RecyclerView favoriteBookRecyclerView2;
    private RecyclerView favoriteBookRecyclerView3;
    private List<Book> bookList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shelf_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookButton = view.findViewById(R.id.favorite_btn_book);
        itemButton = view.findViewById(R.id.favorite_btn_item);
        editButton = view.findViewById(R.id.favorite_btn_edit);
        favoriteBookRecyclerView1 = view.findViewById(R.id.favorite_shelfLayout1);
        favoriteBookRecyclerView2 = view.findViewById(R.id.favorite_shelfLayout2);
        favoriteBookRecyclerView3 = view.findViewById(R.id.favorite_shelfLayout3);
        shelfFavoriteAdapter1 = new ShelfFavoriteAdapter(favoriteBookList1, requireContext());
        shelfFavoriteAdapter2 = new ShelfFavoriteAdapter(favoriteBookList2, requireContext());
        shelfFavoriteAdapter3 = new ShelfFavoriteAdapter(favoriteBookList3, requireContext());
        favoriteBookRecyclerView1.setAdapter(shelfFavoriteAdapter1);
        favoriteBookRecyclerView2.setAdapter(shelfFavoriteAdapter2);
        favoriteBookRecyclerView3.setAdapter(shelfFavoriteAdapter3);
        favoriteBookRecyclerView1.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        favoriteBookRecyclerView2.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        favoriteBookRecyclerView3.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        ItemTouchHelper itemTouchHelper1 = new ItemTouchHelper(new ItemTouchHelperCallback(shelfFavoriteAdapter1));
        itemTouchHelper1.attachToRecyclerView(favoriteBookRecyclerView1);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(new ItemTouchHelperCallback(shelfFavoriteAdapter2));
        itemTouchHelper2.attachToRecyclerView(favoriteBookRecyclerView2);
        ItemTouchHelper itemTouchHelper3 = new ItemTouchHelper(new ItemTouchHelperCallback(shelfFavoriteAdapter3));
        itemTouchHelper3.attachToRecyclerView(favoriteBookRecyclerView3);

        editButton.setOnClickListener(v -> {
            isEditing = !isEditing;
            if (isEditing) {
                editButton.setText("保存");
                bookButton.setVisibility(View.VISIBLE);
                itemButton.setVisibility(View.VISIBLE);
            } else {
                editButton.setText(R.string.btn_edit);
                bookButton.setVisibility(View.GONE);
                itemButton.setVisibility(View.GONE);
            }
        });

        bookButton.setOnClickListener(v -> {
            ShelfFavoriteBookFragment detailFragment = new ShelfFavoriteBookFragment();
            detailFragment.show(getChildFragmentManager(), "ShelfFavoriteBookFragment");
        });

        itemButton.setOnClickListener(v -> {
            ShelfFavoriteItemFragment detailFragment = new ShelfFavoriteItemFragment();
            detailFragment.show(getChildFragmentManager(), "ShelfFavoriteItemFragment");
        });

        getChildFragmentManager().setFragmentResultListener("requestKey", getViewLifecycleOwner(), (requestKey, result) -> {
            int selectedBookId = result.getInt("selectedBookId");
            if (selectedBookId != -1 && isEditing) {
                Book selectedBook = getBookFromDatabase(selectedBookId);
                if (selectedBook != null) {
                    addBookToShelf(selectedBook);
                }
            }
        });
    }

    private Book getBookFromDatabase(int bookId) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("books", new String[]{"id", "image", "title", "author"}, "id = ?", new String[]{String.valueOf(bookId)},null, null, null);
        Book book = null;
        if (cursor != null && cursor.moveToFirst()) {
            book = new Book(cursor.getInt(cursor.getColumnIndexOrThrow("id")), cursor.getString(cursor.getColumnIndexOrThrow("image")), cursor.getString(cursor.getColumnIndexOrThrow("title")), cursor.getString(cursor.getColumnIndexOrThrow("author")));
            cursor.close();
        }
        db.close();
        return book;
    }

    private void addBookToShelf(Book book) {
        final int bookWidth = getResources().getDimensionPixelSize(R.dimen.book_width);
        int freeSpace1 = getRecyclerViewFreeSpace(favoriteBookRecyclerView1);
        int freeSpace2 = getRecyclerViewFreeSpace(favoriteBookRecyclerView2);
        int freeSpace3 = getRecyclerViewFreeSpace(favoriteBookRecyclerView3);

        if (freeSpace1 >= bookWidth) {
            favoriteBookList1.add(book);
            shelfFavoriteAdapter1.notifyItemInserted(favoriteBookList1.size() - 1);
        } else if (freeSpace2 >= bookWidth) {
            favoriteBookList2.add(book);
            shelfFavoriteAdapter2.notifyItemInserted(favoriteBookList2.size() - 1);
        } else if (freeSpace3 >= bookWidth) {
            favoriteBookList3.add(book);
            shelfFavoriteAdapter3.notifyItemInserted(favoriteBookList3.size() - 1);
        } else {
            Toast.makeText(requireContext(), "これ以上並べることはできません", Toast.LENGTH_SHORT).show();
        }
    }

    private int getRecyclerViewFreeSpace(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition == RecyclerView.NO_POSITION) {
                return recyclerView.getWidth();
            } else {
                View lastVisibleView = layoutManager.findViewByPosition(lastVisibleItemPosition);
                if (lastVisibleView != null) {
                    return recyclerView.getWidth() - lastVisibleView.getRight();
                }
            }
        }
        return 0;
    }
}