package com.example.bookshelf;

import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ItemViewHolder> {
    private List<Item> itemList;

    public ListItemAdapter(List<Item> itemList) { this.itemList = itemList; }

    public static class  ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemNameView;
        TextView itemGetView;

        ItemViewHolder(View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImage);
            itemNameView = itemView.findViewById(R.id.itemName);
            itemGetView = itemView.findViewById(R.id.itemGet);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from((parent.getContext())).inflate(R.layout.sub_list_item, parent, false);
        return  new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemImageView.setImageResource(R.drawable.question_mark);

        if (item.getGet() == 1) {
            if (isValidResource(holder, item.getImage())) {
                holder.itemImageView.setImageResource(item.getImage());
            } else {
                Log.e("ListItemAdapter", "Invalid resource ID for item: " + item.getName());
            }
        }

        holder.itemNameView.setText(item.getName());
        holder.itemGetView.setText(item.getGet() == 0 ? "未入手" : "入手済");
    }

    private boolean isValidResource(ItemViewHolder holder, int resourceId) {
        try {
            getResources(holder).getResourceName(resourceId);
            return true;
        } catch (Resources.NotFoundException e) {
            return false;
        }
    }

    private Resources getResources(ItemViewHolder holder) {
        return holder.itemView.getContext().getResources();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
