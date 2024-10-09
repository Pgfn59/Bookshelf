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

import java.util.Collections;
import java.util.List;

public class ShelfFavoriteAdapter extends RecyclerView.Adapter<ShelfFavoriteAdapter.BookViewHolder> {
    private final List<Book> bookList;
    private final Context context;
    private final DisplayMetrics displayMetrics;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ShelfFavoriteAdapter(List<Book> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
        this.displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        BookViewHolder(ImageView itemView) {
            super(itemView);
            imageView = itemView;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Book book = bookList.get(position);
        return book.getImage() != null && !book.getImage().isEmpty() ? 0: 1;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        RecyclerView.LayoutParams params;

        if (viewType == 0) {
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
        } else {
            params = new RecyclerView.LayoutParams(parent.getContext().getResources().getDimensionPixelSize(R.dimen.book_width), ViewGroup.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(params);

            imageView.post(() -> {
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
            });
        }

        return new BookViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        final Book book = bookList.get(position);

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
    public int getItemCount() { return bookList.size(); }

    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(bookList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(bookList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }
}
