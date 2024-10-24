package com.example.bookshelf;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShelfFavoriteAdapter extends RecyclerView.Adapter<ShelfFavoriteAdapter.BookViewHolder> {
    private static final int VIEW_TYPE_BOOK = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private final List<Object> dataList;
    public List<Object> getDataList() {
        return dataList;
    }
    private final List<Object> items = new ArrayList<>();
    private final Context context;
    private final DisplayMetrics displayMetrics;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private OnItemClickListener itemClickListener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    private boolean isEditing;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
        notifyDataSetChanged();
    }

    public ShelfFavoriteAdapter(List<Object> dataList, Context context) {
        this.dataList = dataList;
        this.items.addAll(dataList);
        this.context = context;
        this.displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    public List<Object> getItems() { return items; }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        BookViewHolder(ImageView itemView) {
            super(itemView);
            imageView = itemView;
        }
    }
    public static class ItemViewHolder extends BookViewHolder{
        ItemViewHolder(ImageView itemView){
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object data = dataList.get(position);
        if (data instanceof Book) {
            return VIEW_TYPE_BOOK;
        } else if (data instanceof Item) {
            return VIEW_TYPE_ITEM;
        } else {
            return -1;
        }
    }

    public void addItem(Object item) {
        dataList.add(item);
        items.add(item);
        notifyDataSetChanged();
    }

    public Object removeItemAt(int position) {
        Object removedItem = dataList.remove(position);
        notifyItemRemoved(position);
        return removedItem;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        RecyclerView.LayoutParams params;

        if (viewType == VIEW_TYPE_BOOK) {
            params = new RecyclerView.LayoutParams(parent.getContext().getResources().getDimensionPixelSize(R.dimen.book_width), ViewGroup.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(params);
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerView recyclerView = (RecyclerView) imageView.getParent();
                    if (recyclerView != null) {
                        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                int recyclerViewHeight = recyclerView.getHeight();
                                RecyclerView.LayoutParams coverParams = (RecyclerView.LayoutParams) imageView.getLayoutParams();
                                coverParams.height = recyclerViewHeight;
                                imageView.setLayoutParams(coverParams);

                                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });
                    }
                }
            });
        } else if (viewType == VIEW_TYPE_ITEM){
            params = new RecyclerView.LayoutParams(parent.getContext().getResources().getDimensionPixelSize(R.dimen.book_width), ViewGroup.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(params);

            imageView.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerView recyclerView = (RecyclerView) imageView.getParent();
                    if (recyclerView != null) {
                        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                int recyclerViewHeight = recyclerView.getHeight();
                                RecyclerView.LayoutParams spineParams = (RecyclerView.LayoutParams) imageView.getLayoutParams();
                                spineParams.height = recyclerViewHeight;
                                imageView.setLayoutParams(spineParams);

                                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });
                    }
                }
            });
        }
        if(viewType == VIEW_TYPE_BOOK){
            return new BookViewHolder(imageView);
        }else{
            return new ItemViewHolder(imageView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Object data = dataList.get(position);

        holder.itemView.setOnClickListener(v -> {
            if (isEditing && itemClickListener != null) {
                itemClickListener.onItemClick(position);
            }
        });

        if (data instanceof Book) {
            Book book = (Book) data;
            if (book.getImage() != null && !book.getImage().isEmpty()) {
                int screenWidth = displayMetrics.widthPixels;
                int bookWidth = screenWidth / 3;
                int bookHeight = bookWidth * 4 / 3;

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(bookWidth, bookHeight);
                holder.imageView.setLayoutParams(params);

                Glide.with(context).load(book.getImage()).override(bookWidth, bookHeight).centerCrop().into(holder.imageView);
                holder.imageView.setScaleType(ImageView.ScaleType.FIT_END);

                book.setImageView(holder.imageView);
            } else {
                holder.imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = holder.imageView.getWidth();
                        int recyclerViewHeight = holder.imageView.getContext() instanceof Activity ? ((Activity) holder.imageView.getContext()).findViewById(R.id.favorite_shelfLayout1).getHeight() : 0;
                        int height = recyclerViewHeight - 50;

                        if (width > 0 && height > 0) {
                            Bitmap spineBitmap = createSpineBitmap(book.getTitle(), width, height, context);

                            handler.post(() -> {
                                book.setSpineBitmap(spineBitmap);
                                holder.imageView.setImageBitmap(spineBitmap);
                                holder.imageView.setScaleType(ImageView.ScaleType.FIT_END);
                            });
                        } else {
                            holder.imageView.post(this);
                        }
                    }
                });
            }
        } else if (data instanceof Item) {
            Item item = (Item) data;
            Glide.with(context).load(item.getImage()).into(holder.imageView);
        }
    }

    private Bitmap createSpineBitmap(String title, int width, int height, Context context) {
        View spineView = LayoutInflater.from(context).inflate(R.layout.sub_shelf_book, null);
        TextView titleTextView = spineView.findViewById(R.id.title_vertical);
        titleTextView.setText(title);

        spineView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        spineView.layout(0, 0, spineView.getMeasuredWidth(), spineView.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(spineView.getMeasuredWidth(), spineView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        spineView.draw(canvas);

        return bitmap;
    }

    @Override
    public int getItemCount() { return dataList.size(); }

    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(dataList, i, i + 1);
                Collections.swap(items, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(dataList, i, i - 1);
                Collections.swap(items, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public void setDataList(List<Object> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }
}