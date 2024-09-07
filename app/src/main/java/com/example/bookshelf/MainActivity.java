package com.example.bookshelf;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    DrawerLayout drawerLayout;
    ImageButton buttonDrawerToggle;
    NavigationView navigationView;
    TextView toolbarTitle;
    TextView textDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.main);
        buttonDrawerToggle = findViewById(R.id.buttonDrawerToggle);
        navigationView = findViewById(R.id.navigationView);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        View headerView = navigationView.getHeaderView(0);
        textDuration = headerView.findViewById(R.id.textDuration);
        EditText editUserName = headerView.findViewById(R.id.editUserName);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", "");
        editUserName.setText(userName);

        editUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String userName = s.toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_name", userName);
                editor.apply();
            }
        });

        buttonDrawerToggle.setOnClickListener(v -> drawerLayout.open());

        navigationView.setNavigationItemSelectedListener(item -> {

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
        });
        replaceFragment(new ShelfFragment());
        setToolbarTitle(getString(R.string.btn_shelf));
        displayDuration();
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

    public void displayDuration() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM books WHERE yet = 0";
        Cursor cursor = db.rawQuery(query, null);
        int finishedBookCount = 0;
        if (cursor.moveToFirst()) {
            finishedBookCount = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        String durationText = "読了書籍数：" + finishedBookCount + "冊";
        textDuration.setText(durationText);
    }
}