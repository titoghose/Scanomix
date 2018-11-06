package com.example.upamanyu.stanfordhealthpp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper{
    public static final String db_name = "VITALS2";

    public DBHelper(Context context) {
        super(context, db_name , null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table vitals_data " +
                        "(id integer primary key, name text,bpm text,rr text,date_stamp text)"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS vitals_data");
        onCreate(db);
    }
    public boolean insert_data(String name,String bpm, String rr, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("bpm",bpm);
        contentValues.put("rr",rr);
        contentValues.put("date_stamp",date);
        db.insert("vitals_data", null, contentValues);
        return true;
    }
    public Cursor getData(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from vitals_data where name=\""+name+"\"", null );
        return res;
    }

}