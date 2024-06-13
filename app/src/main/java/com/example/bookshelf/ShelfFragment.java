package com.example.bookshelf;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ShelfFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shelf, container, false);
        view.findViewById(R.id.btn_add_book).setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_shelfFragment_to_addBookFragment);;
        });
        return view;
    }
}