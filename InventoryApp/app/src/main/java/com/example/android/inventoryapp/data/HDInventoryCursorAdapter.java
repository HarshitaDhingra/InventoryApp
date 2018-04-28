package com.example.android.inventoryapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.R;

public class HDInventoryCursorAdapter extends CursorAdapter {
    public HDInventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    public void bindView(View view, final Context context, Cursor cursor) {
        TextView productNameTextView = (TextView) view.findViewById(R.id.name);
        final TextView productQuantityTextView = (TextView) view.findViewById(R.id.quantityleft);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.price);
        ImageView image = (ImageView) view.findViewById(R.id.ima);

        int id = cursor.getColumnIndex(HDInventoryContract.InventoryEntry._ID);
        int pNameColumnIndex = cursor.getColumnIndex(HDInventoryContract.InventoryEntry.Product_Name);
        int pQuantityIndex = cursor.getColumnIndex(HDInventoryContract.InventoryEntry.Product_Quantity);
        int priceColumnIndex = cursor.getColumnIndex(HDInventoryContract.InventoryEntry.Product_Price);

        final long ID = cursor.getLong(id);
        String pName = cursor.getString(pNameColumnIndex);
        int pQuan = cursor.getInt(pQuantityIndex);
        int pPrice = cursor.getInt(priceColumnIndex);
        productNameTextView.setText(pName);
        productQuantityTextView.setText(Integer.toString(pQuan));
        productPriceTextView.setText(Integer.toString(pPrice));

        image.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Quant = Integer.parseInt(productQuantityTextView.getText().toString());
                if (Quant > 0) {
                    Quant -= 1;
                    productQuantityTextView.setText(Integer.toString(Quant));
                    ContentValues values = new ContentValues();
                    values.put(HDInventoryContract.InventoryEntry.Product_Quantity, Quant);
                    Uri uri = ContentUris.withAppendedId(HDInventoryContract.InventoryEntry.CONTENT_URI, ID);
                    context.getContentResolver().update(uri, values, null, null);
                }
            }
        }));
    }
}

