package com.example.vietnamesewordsandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "translation_table";
    private static final String COL0 = "ID";
    private static final String COL1 = "vWord";
    private static final String COL2 = "eWord";
    private static final String COL3 = "created";
    private static final String COL4 = "updated";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " TEXT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT, " +
                COL4 + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addTranslation(String vWord, String eWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, vWord);
        contentValues.put(COL2, eWord);

        Date date = new Date();
        contentValues.put(COL3, date.getTime());
        contentValues.put(COL4, date.getTime());

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result == -1 ? false : true;
    }

    public Cursor getAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
