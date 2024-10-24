package com.example.bookshelf;

import android.content.ContentValues;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements UpdateList {
    private SharedPreferences sharedPreferences;
    private Fragment currentFragment;
    private int previousTotalFinishedBookCount = 0;
    private int previousMonthFinishedBookCount = 0;
    private int previousTotal = 0;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    DrawerLayout drawerLayout;
    ImageButton buttonDrawerToggle;
    NavigationView navigationView;
    TextView toolbarTitle;
    TextView textDuration;
    TextView textDurationMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.main);
        buttonDrawerToggle = findViewById(R.id.buttonDrawerToggle);
        navigationView = findViewById(R.id.navigationView);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        View headerView = navigationView.getHeaderView(0);
        textDuration = headerView.findViewById(R.id.textDuration);
        textDurationMonth = headerView.findViewById(R.id.textDurationMonth);
        previousTotalFinishedBookCount = getFinishedBookCountTotal();
        previousMonthFinishedBookCount = getFinishedBookCountMonth();
        EditText editUserName = headerView.findViewById(R.id.editUserName);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", "");
        editUserName.setText(userName);
        giveLoginBonus();

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
            }else if (itemId == R.id.shelf_favorite) {
                fragment = new ShelfFavoriteFragment();
                titleResId = R.string.btn_favorite;
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
    }

    public void onDestroy() {
        super.onDestroy();
        db.close();
        dbHelper.close();
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
        currentFragment = fragment;
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
        int finishedBookCountTotal = 0;
        int finishedBookCountMonth = 0;
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursorTotal = db.rawQuery("SELECT COUNT(*) FROM books WHERE yet = 0", null)) {
            if (cursorTotal.moveToFirst()) {
                finishedBookCountTotal = cursorTotal.getInt(0);
            }
            cursorTotal.close();

            String currentMonth = new SimpleDateFormat("yyyy/MM", Locale.getDefault()).format(new Date());
            String queryMonth = "SELECT COUNT(*) FROM books WHERE yet = 0 AND SUBSTR(date, 1, 7) = ?";
            try (Cursor cursorMonth = db.rawQuery(queryMonth, new String[]{currentMonth})) {
                if (cursorMonth.moveToFirst()) {
                    finishedBookCountMonth = cursorMonth.getInt(0);
                }
            }

            int monthDiff = finishedBookCountMonth - previousMonthFinishedBookCount;
            if (finishedBookCountTotal > previousTotalFinishedBookCount || monthDiff > 0) {
                updateItemStatus(finishedBookCountTotal, monthDiff);
            }

            previousTotalFinishedBookCount = finishedBookCountTotal;
            previousMonthFinishedBookCount = finishedBookCountMonth;

            String durationText = "今までに読んだ本：" + finishedBookCountTotal + "冊";
            String durationTextMonth = "今月読んだ本：" + finishedBookCountMonth + "冊";
            textDuration.setText(durationText);
            textDurationMonth.setText(durationTextMonth);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void listUpdated() {
        if (currentFragment instanceof ListBookFragment) {
            ((ListBookFragment) currentFragment).loadBookList();
        } else if (currentFragment instanceof CalendarFragment) {
            ((CalendarFragment) currentFragment).loadBookList();
        } else if (currentFragment instanceof ListItemFragment) {
            ((ListItemFragment) currentFragment).loadItemList();
        }
        displayDuration();
    }

    private void updateItemStatus(int total, int monthDiff) {
        List<Integer> acquiredItemIds = new ArrayList<>();
        boolean isTotalUpdated = total >= 5 && total % 5 == 0 && previousTotal < total;
        boolean isMonthUpdated = monthDiff >= 1;

        if (isTotalUpdated || isMonthUpdated) {
            try (Cursor cursor = db.query("items", new String[]{"id"}, "get = 0", null, null, null, "RANDOM()")) {
                List<Integer> unacquiredItems = new ArrayList<>();
                while (cursor.moveToNext()) {
                    unacquiredItems.add(cursor.getInt(0));
                }

                int updatedCount = (isTotalUpdated ? 1 : 0) + (isMonthUpdated ? 1 : 0);
                for (int i = 0; i < updatedCount && !unacquiredItems.isEmpty(); i++) {
                    int randomIndex = new Random().nextInt(unacquiredItems.size());
                    int itemId = unacquiredItems.remove(randomIndex);
                    acquiredItemIds.add(itemId);
                    ContentValues values = new ContentValues();
                    values.put("get", 1);
                    db.update("items", values, "id = ?", new String[]{String.valueOf(itemId)});
                }
            }
        }
        if (currentFragment instanceof ListItemFragment) {
            ((ListItemFragment) currentFragment).loadItemList();
        }
        previousTotal = total;
    }

    private int getFinishedBookCountTotal() {
        String queryTotal = "SELECT COUNT(*) FROM books WHERE yet = 0";
        Cursor cursorTotal = db.rawQuery(queryTotal, null);
        int finishedBookCountTotal = 0;
        if (cursorTotal.moveToFirst()) {
            finishedBookCountTotal = cursorTotal.getInt(0);
        }
        cursorTotal.close();
        return finishedBookCountTotal;
    }

    private int getFinishedBookCountMonth() {
        String currentMonth = new SimpleDateFormat("yyyy/MM", Locale.getDefault()).format(new Date());
        String queryMonth = "SELECT COUNT(*) FROM books WHERE yet = 0 AND SUBSTR(date, 1, 7) = ?";
        Cursor cursorMonth = db.rawQuery(queryMonth, new String[]{currentMonth});
        int finishedBookCountMonth = 0;
        if (cursorMonth.moveToFirst()) {
            finishedBookCountMonth = cursorMonth.getInt(0);
        }
        cursorMonth.close();
        return finishedBookCountMonth;
    }

    //ログインボーナス
    private void giveLoginBonus() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long lastLoginDate = getLastLoginDate(db);
        long todayDate = System.currentTimeMillis() / (24 * 60 * 60 * 1000);

        if (lastLoginDate != todayDate) {
            int bonusItemId = getRandomItemId();
            updateLoginBonus(db, todayDate, bonusItemId);
            addItemToUser(bonusItemId);
            String itemName = getItemName(bonusItemId);
            String message = getString(R.string.login_bonus_message, itemName);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    private long getLastLoginDate(SQLiteDatabase db) {
        long lastLoginDate = 0;

        try (Cursor cursor = db.query("login", new String[]{"last_login_date"}, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                lastLoginDate = cursor.getLong(cursor.getColumnIndexOrThrow("last_login_date"));
            }
        }

        return lastLoginDate;
    }

    private int getRandomItemId() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int itemId = 0;

        try (Cursor cursor = db.query("items", new String[]{"id"}, "get = 0", null, null, null, "RANDOM()", "1")) {
            if (cursor.moveToFirst()) {
                itemId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            }
        }

        db.close();
        return itemId;
    }

    private String getItemName(int itemId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String itemName = "";

        try (Cursor cursor = db.query("items", new String[]{"name"}, "id = ?", new String[]{String.valueOf(itemId)}, null, null, null)) {
            if (cursor.moveToFirst()) {
                itemName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            }
        }

        db.close();
        return itemName;
    }

    private void updateLoginBonus(SQLiteDatabase db, long lastLoginDate, int bonusItemId) {
        ContentValues values = new ContentValues();
        values.put("last_login_date", lastLoginDate);
        values.put("bonus_item_id", bonusItemId);
        int rowsAffected = db.update("login", values, "id = ?", new String[]{String.valueOf(1)});
        if (rowsAffected == 0) {
            db.insert("login", null, values);
        }
    }

    private void addItemToUser(int itemId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("get", 1);
        db.update("items", values, "id = ?", new String[]{String.valueOf(itemId)});
        db.close();
    }
}