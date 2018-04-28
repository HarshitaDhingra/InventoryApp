package com.example.android.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.HDInventoryContract.InventoryEntry;

import java.io.IOException;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private int flag = 0;
    private static final int PET_LOADER = 0;
    private static final int PERMISSION = 1;
    private Uri mCurrentInventoryUri;
    private EditText mPName;
    private EditText mPPrice;
    private TextView mPQuantity;
    private EditText mSName;
    private EditText mSPhone;
    private EditText mSEmail;
    private ImageView mPhoto;
    private Button add;
    private Button sub;
    private int quan;
    private static final int GET_IMAGE = 0;
    Uri imageToBeAddedUrl;
    Button imageButton;

    public boolean mProductHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();
        if (mCurrentInventoryUri == null) {
            setTitle("Add a Product");
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.items_in_cart));
            getLoaderManager().initLoader(PET_LOADER, null, this);
        }
        mPName = (EditText) findViewById(R.id.ProductName);
        mPPrice = (EditText) findViewById(R.id.ProductPrice);
        mPQuantity = (TextView) findViewById(R.id.ProductQuantity);
        mSName = (EditText) findViewById(R.id.SupplierName);
        mSPhone = (EditText) findViewById(R.id.SupplierPhone);
        mSEmail = (EditText) findViewById(R.id.SupplierEmail);
        mPhoto = (ImageView) findViewById(R.id.photo);
        imageButton = (Button) findViewById(R.id.fromGallery);

        add = (Button) findViewById(R.id.add);
        sub = (Button) findViewById(R.id.sub);
        mPName.setOnTouchListener(mTouchListener);
        mPPrice.setOnTouchListener(mTouchListener);
        mPQuantity.setOnTouchListener(mTouchListener);
        mSName.setOnTouchListener(mTouchListener);
        mSPhone.setOnTouchListener(mTouchListener);
        mSEmail.setOnTouchListener(mTouchListener);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quan = Integer.parseInt(mPQuantity.getText().toString());
                quan++;
                mPQuantity.setText(Integer.toString(quan));
            }
        });
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quan = Integer.parseInt(mPQuantity.getText().toString());
                if (quan > 0) {
                    quan--;
                    mPQuantity.setText(Integer.toString(quan));
                }
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToOpenImageSelector();
                mProductHasChanged = true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editer_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentInventoryUri == null) {
            MenuItem menuItem1 = menu.findItem(R.id.delete);
            menuItem1.setVisible(false);
            MenuItem menuItem2 = menu.findItem(R.id.Order);
            menuItem2.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                insertProduct();
                if (flag == 1) {
                    return false;
                } else {
                    finish();
                }
                return true;
            case R.id.Order:
                confirmOrder();
                return true;
            case R.id.delete:
                confirmDeletion();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void confirmOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Order via Mail.");
        builder.setPositiveButton("E-mail", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + mSEmail.getText().toString().trim()));
                String bodyMessage = "I want to order " +
                        mPQuantity.getText().toString().trim() +
                        mPName.getText().toString().trim() + " now! ";
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void confirmDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sure you want to delete the item?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentInventoryUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.Error_deleting),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.Deleted),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void insertProduct() {
        String pNameString = mPName.getText().toString().trim();
        String pPriceString = mPPrice.getText().toString().trim();
        String pQuanString = mPQuantity.getText().toString().trim();
        String SNameString = mSName.getText().toString().trim();
        String SPhoneString = mSPhone.getText().toString().trim();
        String SEmailString = mSEmail.getText().toString().trim();
        if (TextUtils.isEmpty(pNameString) || TextUtils.isEmpty(pPriceString) ||
                TextUtils.isEmpty(pQuanString) || TextUtils.isEmpty(SNameString) ||
                TextUtils.isEmpty(SPhoneString) || TextUtils.isEmpty(SEmailString) ||
                (imageToBeAddedUrl==null)) {
            Toast.makeText(this, "Please insert all required information.",
                    Toast.LENGTH_SHORT).show();
            flag = 1;
            return;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.Product_Name, pNameString);
        int quantity = 1;
        if (!TextUtils.isEmpty(pQuanString)) {
            quantity = Integer.parseInt(pQuanString);
        }
        values.put(InventoryEntry.Product_Quantity, quantity);
        values.put(InventoryEntry.Product_Price, pPriceString);
        values.put(InventoryEntry.Supplier_Name, SNameString);
        values.put(InventoryEntry.Supplier_Phone, SPhoneString);
        values.put(InventoryEntry.Supplier_Email_Add, SEmailString);
        values.put(InventoryEntry.Product_Photo, String.valueOf(imageToBeAddedUrl));

        if (mCurrentInventoryUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.save),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);

            if (rowsAffected == 0 || InventoryEntry.Product_Photo == null) {
                Toast.makeText(this, getString(R.string.errorupdate),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.savedupdate),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
    //Dialog Box created.
    //Start by building a builder first.
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.Product_Name,
                InventoryEntry.Product_Price,
                InventoryEntry.Product_Quantity,
                InventoryEntry.Supplier_Name,
                InventoryEntry.Supplier_Phone,
                InventoryEntry.Supplier_Email_Add,
                InventoryEntry.Product_Photo
        };
        return new CursorLoader(this, mCurrentInventoryUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int pnameColumnIndex = cursor.getColumnIndex(InventoryEntry.Product_Name);
            int ppriceColumnIndex = cursor.getColumnIndex(InventoryEntry.Product_Price);
            int pquanColumnIndex = cursor.getColumnIndex(InventoryEntry.Product_Quantity);
            int snameColumnIndex = cursor.getColumnIndex(InventoryEntry.Supplier_Name);
            int sphoneColumnIndex = cursor.getColumnIndex(InventoryEntry.Supplier_Phone);
            int semailColumnIndex = cursor.getColumnIndex(InventoryEntry.Supplier_Email_Add);
            int pimageColumnIndex = cursor.getColumnIndex(InventoryEntry.Product_Photo);

            String imageIndex = cursor.getString(pimageColumnIndex);
            String productNameIndex = cursor.getString(pnameColumnIndex);
            int productPriceIndex = cursor.getInt(ppriceColumnIndex);
            int productQuantityIndex = cursor.getInt(pquanColumnIndex);
            String SupllierNameIndex = cursor.getString(snameColumnIndex);
            int SupllierPhoneIndex = cursor.getInt(sphoneColumnIndex);
            String SupllierEmailIndex = cursor.getString(semailColumnIndex);
            mPName.setText(productNameIndex);
            mPPrice.setText(Integer.toString(productPriceIndex));
            mPQuantity.setText(Integer.toString(productQuantityIndex));
            mSName.setText(SupllierNameIndex);
            mSPhone.setText(Integer.toString(SupllierPhoneIndex));
            mSEmail.setText(SupllierEmailIndex);
            imageToBeAddedUrl = Uri.parse(imageIndex);
            mPhoto.setImageURI(imageToBeAddedUrl);
            imageButton.setEnabled(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPName.setText("");
        mPPrice.setText("");
        mPQuantity.setText("");
        mSName.setText("");
        mSPhone.setText("");
        mSEmail.setText("");
        mPhoto.setImageURI(imageToBeAddedUrl);
    }

    public void tryToOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION);
            return;
        }
        openImage();
    }

    private void openImage() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImage();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == GET_IMAGE && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                imageToBeAddedUrl = resultData.getData();
                String[] Path = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(imageToBeAddedUrl, Path, null, null, null);
                cursor.moveToFirst();
                int coulmn = cursor.getColumnIndex(Path[0]);
                String pho = cursor.getString(coulmn);
                cursor.close();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageToBeAddedUrl));
                    mPhoto.setImageBitmap(bitmap);
                } catch (IOException ie) {
                    Toast.makeText(this, "Errorrrrrrr", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}