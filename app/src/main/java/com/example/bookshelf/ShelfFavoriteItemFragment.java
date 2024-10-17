package com.example.bookshelf;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

public class ShelfFavoriteItemFragment extends DialogFragment {
    private  ShelfFavoriteItemAdapter adapter;
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
        return inflater.inflate(R.layout.fragment_list_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.itemRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ShelfFavoriteItemAdapter(loadItemsFromDatabase());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Bundle result = new Bundle();
            result.putInt("selectedItemId", item.getId());
            getParentFragmentManager().setFragmentResult("requestItemKey", result);
            dismiss();
        });
    }

    private List<Item> loadItemsFromDatabase() {
        List<Item> itemList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] columns = {"id", "image", "name", "get"};
            String selection = "get = ?";
            String[] selectionArgs = {"1"};
            cursor = db.query("items", columns, selection, selectionArgs, null, null, null);

            while (cursor.moveToNext()) {
                Item item = new Item();
                item.setId(cursor.getInt(0));
                item.setImage(cursor.getInt(1));
                item.setName(cursor.getString(2));
                item.setGet(cursor.getInt(3));
                itemList.add(item);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return itemList;
    }
}