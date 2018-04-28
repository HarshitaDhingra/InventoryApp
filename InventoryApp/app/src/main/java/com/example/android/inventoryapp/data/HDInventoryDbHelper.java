package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HDInventoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = HDInventoryDbHelper.class.getName();
    private static final String DATABASE_NAME = "shelter70.db";
    private static final int DATABASE_VERSION = 1;

    public HDInventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + HDInventoryContract.InventoryEntry.TABLE_NAME + " (" +
                HDInventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HDInventoryContract.InventoryEntry.Product_Name + " TEXT NOT NULL, " +
                HDInventoryContract.InventoryEntry.Product_Price + " TEXT NOT NULL, " +
                HDInventoryContract.InventoryEntry.Product_Quantity + " INTEGER NOT NULL DEFAULT 0, " +
                HDInventoryContract.InventoryEntry.Supplier_Name + " TEXT NOT NULL, " +
                HDInventoryContract.InventoryEntry.Supplier_Phone + " INTEGER NOT NULL, " +
                HDInventoryContract.InventoryEntry.Supplier_Email_Add + " TEXT NOT NULL, " +
                HDInventoryContract.InventoryEntry.Product_Photo + " TEXT);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + HDInventoryContract.InventoryEntry.TABLE_NAME);
    }
}
