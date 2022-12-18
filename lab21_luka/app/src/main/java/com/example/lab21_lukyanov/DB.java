package com.example.lab21_lukyanov;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    String sql;
    SQLiteDatabase db;

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        sql = "CREATE TABLE APIEndPoint (endpoint TEXT);";
        db.execSQL(sql);
        sql = "CREATE TABLE LastPosition (wlen_min FLOAT, wlen_max FLOAT, atomic_num INT);";
        db.execSQL(sql);
        sql = "CREATE TABLE DisplaySettings (divisions INT, intensity INT);";
        db.execSQL(sql);
        sql = "INSERT INTO DisplaySettings VALUES (0, 10);";
        db.execSQL(sql);
        sql = "CREATE TABLE LastTag (tag TEXT)";
        db.execSQL(sql);
    }

    public void saveTag(String tag)
    {
        db = getWritableDatabase();
        sql = "DELETE FROM LastTag;";
        db.execSQL(sql);
        sql = "INSERT INTO LastTag VALUES ('" + tag + "');";
        db.execSQL(sql);
    }

    public String getTag()
    {
        db = getReadableDatabase();
        sql = "SELECT * FROM LastTag;";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst()) return cur.getString(0);
        return null;
    }

    public void saveLastPosition(float wlen_min, float wlen_max, int atomic_num)
    {
        db = getWritableDatabase();
        sql = "DELETE FROM LastPosition;";
        db.execSQL(sql);
        sql = "INSERT INTO LastPosition VALUES (" + wlen_min + ", " + wlen_max + ", " + atomic_num + ");";
        db.execSQL(sql);
    }

    public Float[] getLastPosition()
    {
        db = getReadableDatabase();
        sql = "SELECT * FROM LastPosition;";
        Cursor cur = db.rawQuery(sql, null);
        Float[] lastPos = new Float[3];
        if (cur.moveToFirst())
        {
            lastPos[0] = cur.getFloat(0);
            lastPos[1] = cur.getFloat(1);
            lastPos[2] = (float)cur.getInt(2);
            return lastPos;
        }
        return null;
    }

    public void saveDivisions(int divisions)
    {
        db = getWritableDatabase();
        sql = "UPDATE DisplaySettings SET divisions = " + divisions + ";";
        db.execSQL(sql);
    }

    public void saveIntensity(int intensity)
    {
        db = getWritableDatabase();
        sql = "UPDATE DisplaySettings SET intensity = " + intensity + ";";
        db.execSQL(sql);
    }

    public Integer[] getDisplaySettings()
    {
        db = getReadableDatabase();
        sql = "SELECT * FROM DisplaySettings;";
        Cursor cur = db.rawQuery(sql, null);
        Integer[] DisplaySettings = new Integer[2];
        if (cur.moveToFirst())
        {
            DisplaySettings[0] = cur.getInt(0);
            DisplaySettings[1] = cur.getInt(1);
            return DisplaySettings;
        }
        return null;
    }

    public void saveEndPoint(String endpoint)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM APIEndPoint;";
        db.execSQL(sql);
        sql = "INSERT INTO APIEndPoint VALUES ('" + endpoint + "');";
        db.execSQL(sql);
    }

    public String getEndPoint()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM APIEndPoint;";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst())
        {
            String endpoint = cur.getString(0);
            return endpoint;
        }
        else return null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
