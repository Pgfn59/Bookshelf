package com.example.bookshelf;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageButton buttonDrawerToggle;
    NavigationView navigationView;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.main);
        buttonDrawerToggle = findViewById(R.id.buttonDrawerToggle);
        navigationView = findViewById(R.id.navigationView);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        buttonDrawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        View headerView = navigationView.getHeaderView(0);
        ImageView useImage = headerView.findViewById(R.id.userImage);
        TextView textUserName = headerView.findViewById(R.id.textUserName);

        useImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, textUserName.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId =item.getItemId();
                int titleResId = 0;
                Fragment fragment = null;

                if (itemId == R.id.shelf) {
                    fragment = new ShelfFragment();
                    titleResId = R.string.btn_shelf;
                }else if (itemId == R.id.add_book) {
                    fragment = new AddBookFragment();
                    titleResId = R.string.btn_add_book;
                }else if (itemId == R.id.list_book) {
                    fragment = new ListBookFragment();
                    titleResId = R.string.btn_list_book;
                }else if (itemId == R.id.list_item) {
                    fragment = new ListItemFragment();
                    titleResId = R.string.btn_list_item;
                }else if (itemId == R.id.calendar) {
                    fragment = new CalendarFragment();
                    titleResId = R.string.btn_calendar;
                }

                if (fragment != null) {
                    replaceFragment(fragment);
                    setToolbarTitle(getString(titleResId));
                }

                drawerLayout.close();

                //色替え
                for (int i = 0; i < navigationView.getMenu().size(); i++) {
                    MenuItem menuItem = navigationView.getMenu().getItem(i);
                    if (menuItem.getItemId() == itemId) {
                        menuItem.setChecked(true);
                        SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
                        spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#673AB7")), 0, spanString.length(), 0);
                        menuItem.setTitle(spanString);
                    } else {
                        menuItem.setChecked(false);
                        menuItem.setTitle(menuItem.getTitle().toString());
                    }
                }

                return true;
            }
        });
        replaceFragment(new ShelfFragment());
        setToolbarTitle(getString(R.string.btn_shelf));
    }

    private void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, null);
    }

    private void replaceFragment(Fragment fragment, Bundle args) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (args != null) {
            int bookId = args.getInt("BOOK_ID", -1);
            if (bookId != -1) {
                CalendarBookDetailFragment detailFragment = new CalendarBookDetailFragment();
                Bundle detailArgs = new Bundle();
                detailArgs.putInt("BOOK_ID", bookId);
                detailFragment.setArguments(detailArgs);
                fragment = detailFragment;
            }
        }
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }
}