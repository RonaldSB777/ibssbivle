package com.ibscc.bible;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.HashMap;

public class NotesHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Notes.db";
    private static final int DB_VERSION = 1;

    public NotesHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE notes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "book TEXT, " +
                "chapter INTEGER, " +
                "verse INTEGER, " +
                "note TEXT, " +
                "date TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }

    public void saveNote(String book, int chapter, int verse, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Deletar nota anterior se existir
        db.delete("notes", "book=? AND chapter=? AND verse=?", 
                 new String[]{book, String.valueOf(chapter), String.valueOf(verse)});
        
        // Inserir nova nota apenas se não estiver vazia
        if (note != null && !note.trim().isEmpty()) {
            ContentValues values = new ContentValues();
            values.put("book", book);
            values.put("chapter", chapter);
            values.put("verse", verse);
            values.put("note", note);
            values.put("date", System.currentTimeMillis() + "");
            db.insert("notes", null, values);
        }
        db.close();
    }

    public String getNote(String book, int chapter, int verse) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT note FROM notes WHERE book=? AND chapter=? AND verse=?", 
                new String[]{book, String.valueOf(chapter), String.valueOf(verse)});
        
        String note = "";
        if (cursor.moveToFirst()) {
            note = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return note;
    }

    public HashMap<Integer, String> getChapterNotes(String book, int chapter) {
        HashMap<Integer, String> notes = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT verse, note FROM notes WHERE book=? AND chapter=?", 
                new String[]{book, String.valueOf(chapter)});
        
        if (cursor.moveToFirst()) {
            do {
                int verse = cursor.getInt(0);
                String note = cursor.getString(1);
                notes.put(verse, note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }
}