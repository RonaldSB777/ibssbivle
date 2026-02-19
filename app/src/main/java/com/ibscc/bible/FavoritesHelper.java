package com.ibscc.bible;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class FavoritesHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Favorites.db";
    private static final int DB_VERSION = 1;

    public FavoritesHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "book TEXT, " +
                "abbrev TEXT, " +
                "chapter INTEGER, " +
                "verse INTEGER, " +
                "text TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS favorites");
        onCreate(db);
    }

    public void addFavorite(String book, String abbrev, int chapter, int verse, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("book", book);
        values.put("abbrev", abbrev);
        values.put("chapter", chapter);
        values.put("verse", verse);
        values.put("text", text);
        db.insert("favorites", null, values);
        db.close();
    }

    public void removeFavorite(String abbrev, int chapter, int verse) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("favorites", "abbrev=? AND chapter=? AND verse=?", 
                 new String[]{abbrev, String.valueOf(chapter), String.valueOf(verse)});
        db.close();
    }

    public boolean isFavorite(String abbrev, int chapter, int verse) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM favorites WHERE abbrev=? AND chapter=? AND verse=?", 
                new String[]{abbrev, String.valueOf(chapter), String.valueOf(verse)});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public List<String> getAllFavorites() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM favorites ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                String book = cursor.getString(1);
                int chap = cursor.getInt(3);
                int ver = cursor.getInt(4);
                String text = cursor.getString(5);
                
                list.add("⭐ " + book + " " + chap + ":" + ver + "\n" + text);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}