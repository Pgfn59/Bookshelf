package com.example.bookshelf;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    static final private String DB_NAME = "books_database.db";
    static final private int VERSION = 16;

    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE books ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "image TEXT,"
                + "title TEXT,"
                + "author TEXT,"
                + "date TEXT,"
                + "yet INTEGER,"
                + "rating REAL,"
                + "thought TEXT)");

        db.execSQL("CREATE TABLE items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "image INTEGER,"
                + "name TEXT,"
                + "get INTEGER)");

        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.shoes +",'コンバース',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.controller +",'コントローラー',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.controller +",'コントローラー2',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.controller +",'コントローラー3',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.controller +",'コントローラー4',0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS books");
        db.execSQL("DROP TABLE IF EXISTS items");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
