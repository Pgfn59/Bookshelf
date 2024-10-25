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

public class ListItemFragment extends Fragment {
    private RecyclerView itemRecyclerView;
    private  ListItemAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemRecyclerView = view.findViewById(R.id.itemRecyclerView);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter = new ListItemAdapter(loadItemsFromDatabase());
        itemRecyclerView.setAdapter(adapter);
    }

    private List<Item> loadItemsFromDatabase() {
        List<Item> itemList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] columns = {"id", "image", "name", "get"};
            cursor = db.query("items", columns, null, null, null, null, null);

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

        itemList.sort((item1, item2) -> {
            if (item1.getGet() == 1 && item2.getGet() == 0) {
                return -1;
            } else if (item1.getGet() == 0 && item2.getGet() == 1) {
                return 1;
            } else {
                return 0;
            }
        });
        return itemList;
    }

    public void loadItemList() {
        if (adapter != null) {
            adapter.setItemList(loadItemsFromDatabase());
            adapter.notifyDataSetChanged();
        }
    }
}