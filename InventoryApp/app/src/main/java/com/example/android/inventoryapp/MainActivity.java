package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.HDInventoryContract;
import com.example.android.inventoryapp.data.HDInventoryCursorAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PET_LOADER = 0;
    HDInventoryCursorAdapter mCursorAdapter;
    ListView InventoryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });
        InventoryListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        InventoryListView.setEmptyView(emptyView);

        mCursorAdapter = new HDInventoryCursorAdapter(this, null);
        InventoryListView.setAdapter(mCursorAdapter);

        InventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(HDInventoryContract.InventoryEntry.CONTENT_URI, id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(PET_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.catalog_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stored_product:
                insertDummyProduct();
                return true;
            case R.id.delete_all_products:
                deleteAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        int rowsDeleted = getContentResolver().delete(HDInventoryContract.InventoryEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from Inventory database");
    }

    private void insertDummyProduct() {
        ContentValues values = new ContentValues();
        values.put(HDInventoryContract.InventoryEntry.Product_Name, "Umbrellas");
        values.put(HDInventoryContract.InventoryEntry.Product_Price, "50");
        values.put(HDInventoryContract.InventoryEntry.Product_Quantity, "10");
        values.put(HDInventoryContract.InventoryEntry.Supplier_Name, "Harshita Dhingra");
        values.put(HDInventoryContract.InventoryEntry.Supplier_Phone, "9485448488");
        values.put(HDInventoryContract.InventoryEntry.Supplier_Email_Add, "abcd@gmail.com");
        values.put(HDInventoryContract.InventoryEntry.Product_Photo, "android.resource://com.example.android.inventoryapp/drawable/umb");
        Uri newUri = getContentResolver().insert(HDInventoryContract.InventoryEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                HDInventoryContract.InventoryEntry._ID,
                HDInventoryContract.InventoryEntry.Product_Name,
                HDInventoryContract.InventoryEntry.Product_Quantity,
                HDInventoryContract.InventoryEntry.Product_Price
        };

        return new CursorLoader(this, HDInventoryContract.InventoryEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}