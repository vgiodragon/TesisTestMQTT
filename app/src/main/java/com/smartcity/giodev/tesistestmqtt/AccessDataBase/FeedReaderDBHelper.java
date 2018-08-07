package com.smartcity.giodev.tesistestmqtt.AccessDataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedReaderDBHelper extends SQLiteOpenHelper {

    private String SQL_CREATE_ENTRIES ;
                    /*="CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";*/

    private String SQL_DELETE_ENTRIES ;
    //="DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Beagons.db";

    public FeedReaderDBHelper(Context context, String TABLE_NAME) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setSQL_CREATE_ENTRIES(TABLE_NAME);
        setSQL_DELETE_ENTRIES(TABLE_NAME);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void setSQL_CREATE_ENTRIES(String TABLE_NAME) {
        SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                FeedReaderContract.FeedEntry.COLUMN_NAME_NTP + " TEXT," +
                FeedReaderContract.FeedEntry.COLUMN_HORA_LLEGADA + " TEXT," +
                FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";
    }

    public void setSQL_DELETE_ENTRIES(String TABLE_NAME) {
        SQL_DELETE_ENTRIES ="DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }
}
