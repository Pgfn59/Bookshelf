package com.example.bookshelf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShelfFavoriteItemAdapter extends RecyclerView.Adapter<ShelfFavoriteItemAdapter.ItemViewHolder> {
    private final List<Item> itemList;
    private OnItemClickListener clickListener;

    public ShelfFavoriteItemAdapter(List<Item> itemList) { this.itemList = itemList; }

    public interface OnItemClickListener { void onItemClick(Item item); }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public static class  ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemNameView;
        ItemViewHolder(View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImage);
            itemNameView = itemView.findViewById(R.id.itemName);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_favorite_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ShelfFavoriteItemAdapter.ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemImageView.setImageResource(item.getImage());
        holder.itemNameView.setText(item.getName());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
