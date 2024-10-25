package com.example.bookshelf;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    static final private String DB_NAME = "books_database.db";
    static final private int VERSION = 22;

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

        db.execSQL("CREATE TABLE shelf ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "type INTEGER,"
                + "book_id INTEGER,"
                + "item_id INTEGER,"
                + "shelf_position INTEGER)");

        db.execSQL("CREATE TABLE login("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "last_login_date INTEGER,"
                + "bonus_item_id INTEGER)");

        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.filebox +",'FILEボックス',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.ipod +",'音楽プレイヤー',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.ufo +",'UFO',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.shoes2 +",'スケート靴',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.castle +",'お城',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.skeleton +",'ガイコツ',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.coffee_cup +",'コーヒーカップ',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.shoes +",'靴',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.bicycle +",'自転車',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.football +",'サッカーボール',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.uniform +",'ユニホーム',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.clock +",'時計',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.speaker +",'スピーカー',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.tape +",'セロハンテープ',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.dart_arrow +",'ダーツの矢',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.dart +",'ダーツ',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.dharma +",'ダルマ',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.dumbbell +",'ダンベル',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.chess1 +",'チェスの駒1',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.chess2 +",'チェスの駒2',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.coffee +",'テイクアウトコーヒー',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.chicken +",'にわとり',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.note +",'ノート',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.basketball +",'バスケットボール',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.badminton +",'バドミントン',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.billiards +",'ビリヤード',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.controller +",'コントローラー',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.piggy_bank +",'ブタの貯金箱',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.printer +",'プリンター',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.present_box +",'プレゼントボックス',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.headphone +",'ヘッドホン',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.pennant +",'ペナント',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.penguin +",'ペンギン',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.pin +",'ボウリングのピン',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.mic +",'マイク',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.moai +",'モアイ',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.yacht +",'ヨット',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.helmet +",'兜',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.clapperboard +",'カチンコ',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.house +",'家',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.ship +",'貨物船',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.balloon +",'気球',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.safe +",'金庫',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.stop +",'交通標識',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.hourglass1 +",'砂時計1',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.hourglass2 +",'砂時計2',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.mountain +",'山',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.car +",'車',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.haniwa +",'埴輪',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.torii +",'鳥居',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.tobibako +",'跳び箱',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.weather +",'天気',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.light_bulb +",'電球',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.tower +",'東京タワー',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.island +",'南の島',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.cow +",'牛',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.baseball +",'野球ボール',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.trophy +",'優勝カップ',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.post +",'郵便ポスト',0)");
        db.execSQL("INSERT INTO items (image, name, get)"
                + "VALUES(" + R.drawable.bag +",'鞄',0)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS books");
        db.execSQL("DROP TABLE IF EXISTS items");
        db.execSQL("DROP TABLE IF EXISTS shelf");
        db.execSQL("DROP TABLE IF EXISTS login");
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
