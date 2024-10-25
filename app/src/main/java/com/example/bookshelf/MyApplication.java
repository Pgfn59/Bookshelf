package com.example.bookshelf;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class MyApplication extends Application {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        checkLoginBonus();
    }

    private void checkLoginBonus() {
        SharedPreferences sharedPreferences = getSharedPreferences("login_bonus", Context.MODE_PRIVATE);
        long lastLoginBonusDate = sharedPreferences.getLong("last_login_bonus_date", 0);
        long currentDate = System.currentTimeMillis() / (24 * 60 * 60 * 1000);
        if (lastLoginBonusDate != currentDate) {
            giveLoginBonus(sharedPreferences);
        }
    }

    private void giveLoginBonus(SharedPreferences sharedPreferences) {
        int itemId = getRandomUnacquiredItemId();

        if (itemId != 0) {
            updateItemStatus(itemId, true);
            String itemName = getItemName(itemId);
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(getApplicationContext(), "ログインボーナス\n" + itemName + "を獲得しました！", Toast.LENGTH_SHORT).show();
            });
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("last_login_bonus_date", System.currentTimeMillis() / (24 * 60 * 60 * 1000));
        editor.apply();
    }

    private int getRandomUnacquiredItemId() {
        int itemId = 0;
        try (Cursor cursor = db.query("items", new String[]{"id"}, "get = 0", null, null, null, "RANDOM()", "1")) {
            if (cursor.moveToFirst()) {
                itemId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemId;
    }

    private void updateItemStatus(int itemId, boolean acquired) {
        ContentValues values = new ContentValues();
        values.put("get", acquired ? 1 : 0);
        db.update("items", values, "id = ?", new String[]{String.valueOf(itemId)});
    }

    private String getItemName(int itemId) {
        String itemName = "";
        try (Cursor cursor = db.query("items", new String[]{"name"}, "id = ?", new String[]{String.valueOf(itemId)}, null, null, null)) {
            if (cursor.moveToFirst()) {
                itemName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemName;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
