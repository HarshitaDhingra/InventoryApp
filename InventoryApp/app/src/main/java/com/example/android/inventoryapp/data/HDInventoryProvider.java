package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class HDInventoryProvider extends ContentProvider {
    int rowsDeleted;
    public static final String LOG_TAG = HDInventoryProvider.class.getSimpleName();
    private HDInventoryDbHelper mDbHelper;
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(HDInventoryContract.CONTENT_AUTHORITY, HDInventoryContract.PATH_INVENTORY, PRODUCTS);
        sUriMatcher.addURI(HDInventoryContract.CONTENT_AUTHORITY, HDInventoryContract.PATH_INVENTORY + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new HDInventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                String[] Projection = {HDInventoryContract.InventoryEntry._ID, HDInventoryContract.InventoryEntry.Product_Name, HDInventoryContract.InventoryEntry.Product_Price,
                        HDInventoryContract.InventoryEntry.Product_Quantity, HDInventoryContract.InventoryEntry.Supplier_Name, HDInventoryContract.InventoryEntry.Supplier_Phone,
                        HDInventoryContract.InventoryEntry.Supplier_Email_Add, HDInventoryContract.InventoryEntry.Product_Photo};
                cursor = database.query(HDInventoryContract.InventoryEntry.TABLE_NAME, Projection, null, null, null, null, null);
                break;

            case PRODUCT_ID:
                selection = HDInventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(HDInventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
//        String name = values.getAsString(HDInventoryContract.InventoryEntry.Product_Name);
//        if (name == null) {
//            throw new IllegalArgumentException("Product requires a Name");
//        }
//        Integer quantity = values.getAsInteger(HDInventoryContract.InventoryEntry.Product_Quantity);
//        if(quantity!= null && quantity<0){
//            throw new IllegalArgumentException("Product requires a Quantity");
//        }
//        String price = values.getAsString(HDInventoryContract.InventoryEntry.Product_Price);
//        if(price == null){
//            throw new IllegalArgumentException("Product requires a Price");
//        }
//        String phone = values.getAsString(HDInventoryContract.InventoryEntry.Supplier_Name);
//        if(phone == null) {
//            throw new IllegalArgumentException("Product requires a suppliers name");
//        }
//            String sphone = values.getAsString(HDInventoryContract.InventoryEntry.Supplier_Phone);
//            if(sphone == null) {
//                throw new IllegalArgumentException("Product requires suppliers phone number");
//            }
//                String email = values.getAsString(HDInventoryContract.InventoryEntry.Supplier_Email_Add);
//                if(email == null){
//                    throw new IllegalArgumentException("Product requires an email address");
//                }
//                String imag = values.getAsString(HDInventoryContract.InventoryEntry.Product_Photo);
//                if(imag == null){
//                    throw new IllegalArgumentException("Product needs an Image");
//                }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(HDInventoryContract.InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = HDInventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(HDInventoryContract.InventoryEntry.Product_Name)) {
            String name = values.getAsString(HDInventoryContract.InventoryEntry.Product_Name);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name.");
            }
        }
        if (values.containsKey(HDInventoryContract.InventoryEntry.Product_Price)) {
            Integer price = values.getAsInteger(HDInventoryContract.InventoryEntry.Product_Price);
            if (price == null) {
                throw new IllegalArgumentException("Product requires valid price.");
            }
        }
        if (values.containsKey(HDInventoryContract.InventoryEntry.Product_Quantity)) {
            Integer quan = values.getAsInteger(HDInventoryContract.InventoryEntry.Product_Quantity);
            if (quan == null && quan < 0) {
                throw new IllegalArgumentException("Product requires valid quantity.");
            }
        }
        if (values.containsKey(HDInventoryContract.InventoryEntry.Supplier_Name)) {
            String name = values.getAsString(HDInventoryContract.InventoryEntry.Supplier_Name);
            if (name == null) {
                throw new IllegalArgumentException("Owner name required.");
            }
        }
        if (values.containsKey(HDInventoryContract.InventoryEntry.Supplier_Phone)) {
            Integer phone = values.getAsInteger(HDInventoryContract.InventoryEntry.Supplier_Phone);
            if (phone == null) {
                throw new IllegalArgumentException("Owner phone number required.");
            }
        }
        if (values.containsKey(HDInventoryContract.InventoryEntry.Supplier_Email_Add)) {
            String email = values.getAsString(HDInventoryContract.InventoryEntry.Supplier_Email_Add);
            if (email == null) {
                throw new IllegalArgumentException("Owner email id required.");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(HDInventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(HDInventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = HDInventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(HDInventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return HDInventoryContract.InventoryEntry.TABLE_TYPE;
            case PRODUCT_ID:
                return HDInventoryContract.InventoryEntry.ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}