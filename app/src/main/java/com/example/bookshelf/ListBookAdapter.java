package com.example.bookshelf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListBookAdapter extends RecyclerView.Adapter<ListBookAdapter.BookViewHolder> {
    private List<Book> bookList;
    private OnItemClickListener listener;

    public ListBookAdapter(List<Book> bookList, OnItemClickListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitleTextView;
        TextView bookAuthorTextView;

        BookViewHolder(View itemView) {
            super(itemView);
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            bookAuthorTextView = itemView.findViewById(R.id.bookAuthorTextView);
        }
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_list_book, parent, false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bookTitleTextView.setText(book.title);
        holder.bookAuthorTextView.setText(book.author);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(book);
            }
        });;
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void updateBookList(List<Book> newBookList) {
        this.bookList.clear();
        this.bookList.addAll(newBookList);
        notifyDataSetChanged();
    }
}
