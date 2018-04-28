package com.example.android.shop;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHandler extends SQLiteOpenHelper{

    String CREATE_STUDENT_TABLE;
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME ="studentinfo.db";
    private static final String TABLE_STUDENT_DATA="student";
    private static final String ID="_id";
    private static final String STUDENT_ROLL="roll";
    private static final String STUDENT_NAME="name";
    private static final String STUDENT_PHONE="phone";
    private static final String STUDENT_EMAIL="email";

    public DbHandler(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CREATE_STUDENT_TABLE = "CREATE  TABLE " + TABLE_STUDENT_DATA + "("
        + ID + "INTEGER PRIMARY KEY, "+STUDENT_ROLL+" INTEGER, "+STUDENT_NAME+" TEXT  "+STUDENT_PHONE+" TEXT "+STUDENT_EMAIL+" TEXT "+");";
        db.execSQL(CREATE_STUDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + CREATE_STUDENT_TABLE);
        onCreate(db);
    }
}
