package com.example.inventorymanagementsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddSupplierActivity extends AppCompatActivity {


    private EditText supplierName, supplierEmail, supplierContact, supplierAddress, supplierComments;
    private Button saveButton;
    private ImageView personImage;
    Uri FilePathUri;
    final int REQUEST_CODE_GALLERY = 999;
    DatabaseReference databaseReference;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_supplier);

        supplierName = findViewById(R.id.SupplierName);
        supplierEmail = findViewById(R.id.SupplierEmail);
        supplierContact= findViewById(R.id.SupplierPhone);
        supplierAddress = findViewById(R.id.SupplierAddress);
        supplierComments = findViewById(R.id.SupplierComments);
        saveButton = findViewById(R.id.btnSave);
        personImage = findViewById(R.id.SupplierIm);

        id = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference("Supplier_Details").child(id);

        personImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(AddSupplierActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY
                );
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadSupplierData();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);

            } else {
                Toast.makeText(AddSupplierActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                personImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void UploadSupplierData() {
        String name = supplierName.getText().toString().trim();
        String email = supplierEmail.getText().toString().trim();
        String contact = supplierContact.getText().toString().trim();
        String address = supplierAddress.getText().toString().trim();
        String comments = supplierComments.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Please enter item name", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) personImage.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Supplier s = new Supplier(base64Image,name, email, contact, address, comments);
        String supplierId = databaseReference.push().getKey();
        // Adding image upload id s child element into databaseReference.
        databaseReference.child(supplierId).setValue(s);

        Intent updateItem = new Intent (AddSupplierActivity.this, SupplierListActivity.class);
        updateItem.putExtra("id", id);
        startActivity(updateItem);
    }
}
