package com.example.vietnamesewordsandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "translation_table";
    private static final String ID = "ID";
    private static final String V_WORD = "vWord";
    private static final String E_WORD = "eWord";
    private static final String CREATED = "created";
    private static final String UPDATED = "updated";

    DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                V_WORD + " TEXT, " +
                E_WORD + " TEXT, " +
                CREATED + " TEXT, " +
                UPDATED + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE " + TABLE_NAME);
        onCreate(db);
    }

    long add(String vWord, String eWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(E_WORD, eWord);
        contentValues.put(V_WORD, vWord);

        Date date = new Date();
        contentValues.put(CREATED, date.getTime());
        contentValues.put(UPDATED, date.getTime());

        return db.insert(TABLE_NAME, null, contentValues);
    }

    void update(long id, String column, String newText) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + column + " = '" + newText + "' WHERE id = " + id + ";";
        db.execSQL(query);
    }

    void delete(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE id = " + id + ";";
        db.execSQL(query);
    }

    Cursor getAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";
        return db.rawQuery(query, null);
    }

    Cursor getRandom() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY RANDOM() LIMIT 1;";
        return db.rawQuery(query, null);
    }

    Cursor getId(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = " + id + ";";
        return db.rawQuery(query, null);
    }

    Cursor search(String language, String searchedText) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query;

        if (language.equals("vietnamese")) {
            query = "SELECT * FROM " + TABLE_NAME + " WHERE " + V_WORD + " LIKE ?";
        } else {
            query = "SELECT * FROM " + TABLE_NAME + " WHERE " + E_WORD + " LIKE ?";
        }

        return db.rawQuery(query, new String[] { "%" + searchedText + "%" });
    }
}