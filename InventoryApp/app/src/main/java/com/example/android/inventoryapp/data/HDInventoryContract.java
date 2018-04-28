package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class HDInventoryContract {
    private HDInventoryContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";

    public static final class InventoryEntry implements BaseColumns {

        public static final String TABLE_NAME = "Inventory";
        public static final String _ID = BaseColumns._ID;
        public static final String Product_Name = "ProductName";
        public static final String Product_Price = "ProductPrice";
        public static final String Product_Quantity = "ProductQuantity";
        public static final String Supplier_Name = "SupplierName";
        public static final String Supplier_Phone = "SupplierPhone";
        public static final String Supplier_Email_Add = "SupplierEmail";
        public static final String Product_Photo = "ProductPhoto";

        public static final String TABLE_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public static final String ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY + "/#" + _ID;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);


    }
}
