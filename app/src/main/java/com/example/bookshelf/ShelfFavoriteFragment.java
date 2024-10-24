package com.example.bookshelf;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
    private DatabaseHelper dbHelper;
    private boolean isEditing = false;
    private Button bookButton;
    private Button itemButton;
    private Button editButton;
    private ShelfFavoriteAdapter shelfFavoriteAdapter1;
    private ShelfFavoriteAdapter shelfFavoriteAdapter2;
    private ShelfFavoriteAdapter shelfFavoriteAdapter3;
    private RecyclerView favoriteBookRecyclerView1;
    private RecyclerView favoriteBookRecyclerView2;
    private RecyclerView favoriteBookRecyclerView3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shelf_favorite, container, false);
        dbHelper = new DatabaseHelper(requireContext());

        return view;
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
        shelfFavoriteAdapter1 = new ShelfFavoriteAdapter(new ArrayList<>(), requireContext());
        shelfFavoriteAdapter2 = new ShelfFavoriteAdapter(new ArrayList<>(), requireContext());
        shelfFavoriteAdapter3 = new ShelfFavoriteAdapter(new ArrayList<>(), requireContext());
        favoriteBookRecyclerView1.setAdapter(shelfFavoriteAdapter1);
        favoriteBookRecyclerView2.setAdapter(shelfFavoriteAdapter2);
        favoriteBookRecyclerView3.setAdapter(shelfFavoriteAdapter3);
        favoriteBookRecyclerView1.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        favoriteBookRecyclerView2.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        favoriteBookRecyclerView3.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        List<Object> items = loadShelf();
        distributeItemsToAdapters(items);
        ItemTouchHelper itemTouchHelper1 = new ItemTouchHelper(new ItemTouchHelperCallback(shelfFavoriteAdapter1, items));
        itemTouchHelper1.attachToRecyclerView(favoriteBookRecyclerView1);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(new ItemTouchHelperCallback(shelfFavoriteAdapter2, items));
        itemTouchHelper2.attachToRecyclerView(favoriteBookRecyclerView2);
        ItemTouchHelper itemTouchHelper3 = new ItemTouchHelper(new ItemTouchHelperCallback(shelfFavoriteAdapter3, items));
        itemTouchHelper3.attachToRecyclerView(favoriteBookRecyclerView3);
        shelfFavoriteAdapter1.notifyDataSetChanged();
        shelfFavoriteAdapter2.notifyDataSetChanged();
        shelfFavoriteAdapter3.notifyDataSetChanged();

        shelfFavoriteAdapter1.setOnItemClickListener(position -> {
            if (isEditing) {
                showDeleteConfirmationDialog(position, shelfFavoriteAdapter1);
            }
        });

        shelfFavoriteAdapter2.setOnItemClickListener(position -> {
            if (isEditing) {
                showDeleteConfirmationDialog(position, shelfFavoriteAdapter2);
            }
        });

        shelfFavoriteAdapter3.setOnItemClickListener(position -> {
            if (isEditing) {
                showDeleteConfirmationDialog(position, shelfFavoriteAdapter3);
            }
        });

        editButton.setOnClickListener(v -> {
            isEditing = !isEditing;
            shelfFavoriteAdapter1.setEditing(isEditing);
            shelfFavoriteAdapter2.setEditing(isEditing);
            shelfFavoriteAdapter3.setEditing(isEditing);
            if (isEditing) {
                editButton.setText("保存");
                bookButton.setVisibility(View.VISIBLE);
                itemButton.setVisibility(View.VISIBLE);
            } else {
                editButton.setText(R.string.btn_edit);
                bookButton.setVisibility(View.GONE);
                itemButton.setVisibility(View.GONE);
                items.clear();
                items.addAll(shelfFavoriteAdapter1.getItems());
                items.addAll(shelfFavoriteAdapter2.getItems());
                items.addAll(shelfFavoriteAdapter3.getItems());
                List<Object> dataList = new ArrayList<>();
                dataList.addAll(shelfFavoriteAdapter1.getDataList());
                dataList.addAll(shelfFavoriteAdapter2.getDataList());
                dataList.addAll(shelfFavoriteAdapter3.getDataList());
                saveShelf(dataList);
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

        getChildFragmentManager().setFragmentResultListener("requestItemKey", getViewLifecycleOwner(), (requestKey, result) -> {
            int selectedItemId = result.getInt("selectedItemId");
            if (selectedItemId != -1 && isEditing) {
                Item selectedItem = getItemFromDatabase(selectedItemId);
                if (selectedItem != null) {
                    addItemToShelf(selectedItem);
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

    private Item getItemFromDatabase(int itemId) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("items", new String[]{"id", "image", "name"}, "id = ?", new String[]{String.valueOf(itemId)}, null, null, null);

        Item item = null;
        if (cursor != null && cursor.moveToFirst()) {
            item = new Item();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            item.setImage(cursor.getInt(cursor.getColumnIndexOrThrow("image")));
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            cursor.close();
        }

        db.close();
        return item;
    }

    private void addBookToShelf(Book book) {
        final int bookWidth = getResources().getDimensionPixelSize(R.dimen.book_width);
        int freeSpace1 = getRecyclerViewFreeSpace(favoriteBookRecyclerView1);
        int freeSpace2 = getRecyclerViewFreeSpace(favoriteBookRecyclerView2);
        int freeSpace3 = getRecyclerViewFreeSpace(favoriteBookRecyclerView3);

        if (freeSpace1 >= bookWidth) {
            shelfFavoriteAdapter1.addItem(book);
        } else if (freeSpace2 >= bookWidth) {
            shelfFavoriteAdapter2.addItem(book);
        } else if (freeSpace3 >= bookWidth) {
            shelfFavoriteAdapter3.addItem(book);
        } else {
            Toast.makeText(requireContext(), "これ以上並べることはできません", Toast.LENGTH_SHORT).show();
        }
    }

    private void addItemToShelf(Item item) {
        final int itemWidth = getResources().getDimensionPixelSize(R.dimen.item_width);
        int freeSpace1 = getRecyclerViewFreeSpace(favoriteBookRecyclerView1);
        int freeSpace2 = getRecyclerViewFreeSpace(favoriteBookRecyclerView2);
        int freeSpace3 = getRecyclerViewFreeSpace(favoriteBookRecyclerView3);

        if (freeSpace1 >= itemWidth) {
            shelfFavoriteAdapter1.addItem(item);
        } else if (freeSpace2 >= itemWidth) {
            shelfFavoriteAdapter2.addItem(item);
        } else if (freeSpace3 >= itemWidth) {
            shelfFavoriteAdapter3.addItem(item);
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

    public void saveShelf(List<Object> items) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("shelf", null, null);
        List<Object> dataList = new ArrayList<>();
        dataList.addAll(shelfFavoriteAdapter1.getDataList());
        dataList.addAll(shelfFavoriteAdapter2.getDataList());
        dataList.addAll(shelfFavoriteAdapter3.getDataList());

        for (int i = 0; i < dataList.size(); i++) {
            Object item = items.get(i);
            ContentValues values = new ContentValues();

            if (item instanceof Book) {
                values.put("book_id", ((Book) item).getId());
                values.put("type", 0);
            } else if (item instanceof Item) {
                values.put("item_id", ((Item) item).getId());
                values.put("type", 1);
            }

            int shelfPosition = getShelfPositionFromItems(item, dataList);
            values.put("shelf_position", shelfPosition);

            db.insert("shelf", null, values);
        }

        db.close();
    }

    private int getShelfPositionFromItems(Object item, List<Object> items) {
        List<Object> dataList = new ArrayList<>();
        dataList.addAll(shelfFavoriteAdapter1.getDataList());
        dataList.addAll(shelfFavoriteAdapter2.getDataList());
        dataList.addAll(shelfFavoriteAdapter3.getDataList());
        int index = dataList.indexOf(item);
        if (index < shelfFavoriteAdapter1.getItemCount()) {
            return 0;
        } else if (index < shelfFavoriteAdapter1.getItemCount() + shelfFavoriteAdapter2.getItemCount()) {
            return 1;
        } else if (index < shelfFavoriteAdapter1.getItemCount() + shelfFavoriteAdapter2.getItemCount() + shelfFavoriteAdapter3.getItemCount()) {
            return 2;
        } else {
            return 0;
        }
    }

    public List<Object> loadShelf() {
        List<Object> items = new ArrayList<>();
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM shelf ORDER BY shelf_position", null)) {

            while (cursor.moveToNext()) {
                int bookId = cursor.getInt(cursor.getColumnIndexOrThrow("book_id"));
                int itemId = cursor.getInt(cursor.getColumnIndexOrThrow("item_id"));

                if (bookId != 0) {
                    Book book = getBookFromDatabase(bookId);
                    if (book != null) {
                        items.add(book);
                    }
                } else {
                    Item item = getItemFromDatabase(itemId);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
        }
        return items;
    }

    private void distributeItemsToAdapters(List<Object> items) {
        shelfFavoriteAdapter1.setDataList(new ArrayList<>());
        shelfFavoriteAdapter2.setDataList(new ArrayList<>());
        shelfFavoriteAdapter3.setDataList(new ArrayList<>());

        for (Object item : items) {
            if (!(item instanceof Book || item instanceof Item)) {
                continue;
            }

            int shelfPosition = getShelfPosition(item);

            switch (shelfPosition) {
                case 0:
                    shelfFavoriteAdapter1.addItem(item);
                    break;
                case 1:
                    shelfFavoriteAdapter2.addItem(item);
                    break;
                case 2:
                    shelfFavoriteAdapter3.addItem(item);
                    break;
            }
        }
    }

    private int getShelfPosition(Object item) {
        int shelfPosition = 0;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"shelf_position"};
        String selection = "book_id = ? OR item_id = ?";
        String[] selectionArgs;

        if (item instanceof Book) {
            selectionArgs = new String[]{String.valueOf(((Book) item).getId()), "0"};
        } else if (item instanceof Item) {
            selectionArgs = new String[]{"0", String.valueOf(((Item) item).getId())};
        } else {
            return shelfPosition;
        }

        Cursor cursor = db.query("shelf", columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            shelfPosition = cursor.getInt(cursor.getColumnIndexOrThrow("shelf_position"));
            cursor.close();
        }

        db.close();

        return shelfPosition;
    }

    private void showDeleteConfirmationDialog(int position, ShelfFavoriteAdapter adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("このアイテムを削除しますか？");
        builder.setPositiveButton("削除", (dialog, which) -> adapter.removeItemAt(position));
        builder.setNegativeButton("キャンセル", null);
        builder.show();
    }
}