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
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class EditItemActivity extends AppCompatActivity {

    private EditText itemName, itemPrice, itemQuantity, itemLocation, itemCategory, itemPurchaseD, itemSupName, itemComments;
    private TextView itemWendD;
    private Button saveButton, deleteButton, lendbtn;
    private ImageView itemImage;
    String uriIm;
    String id;
    String iKey;
    final int REQUEST_CODE_GALLERY = 999;
    DatabaseReference databaseReference;
    ArrayList<String> itemInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        itemInfo = new ArrayList<>();
        itemName = findViewById(R.id.edtitemName);
        itemPrice = findViewById(R.id.edtitemPrice);
        itemQuantity = findViewById(R.id.edtitemQuantity);
        itemLocation = findViewById(R.id.edtitemLocation);
        itemCategory = findViewById(R.id.edtitemCategory);
        itemPurchaseD = findViewById(R.id.edtitemPurchaseDate);
        itemSupName = findViewById(R.id.edtitemSupplierName);
        itemWendD = findViewById(R.id.edtitemWarrantyEnd);
        itemComments = findViewById(R.id.edtitemComments);
        saveButton = findViewById(R.id.edtsaveItem);
        itemImage = findViewById(R.id.edtitemIm);
        deleteButton = findViewById(R.id.edtDeleteItem);
        lendbtn = findViewById(R.id.lendItem);

        id = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference("Item_Details").child(id);
        iKey = getIntent().getStringExtra("itemKey");

        databaseReference.child(iKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // supInfo.add(dataSnapshot.getChildren())
                Item itemInfo = dataSnapshot.getValue(Item.class);
                itemName.setText(itemInfo.getName());
                itemPrice.setText(itemInfo.getPrice());
                itemQuantity.setText(itemInfo.getQuantity());
                itemLocation.setText(itemInfo.getLocation());
                itemCategory.setText(itemInfo.getCategory());
                itemPurchaseD.setText(itemInfo.getPurchaseDate());
                itemSupName.setText(itemInfo.getSupplName());
                itemWendD.setText(itemInfo.getWarrantyEnd());
                itemComments.setText(itemInfo.getComments());
                byte[] decodedString = Base64.decode(itemInfo.getImagePath(), Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                itemImage.setImageBitmap(bmp);
                //uriIm = supInfo.getImagePath();
                //String ip = supInfo.getImagePath();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(EditItemActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY
                );
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateItemData();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(iKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                            itemSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        lendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(EditItemActivity.this, LentItemActivity.class);
                a.putExtra("id", id);
                a.putExtra("name", itemName.getText());
                startActivity(a);
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
                Toast.makeText(EditItemActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
                itemImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void UpdateItemData() {
        final String name = itemName.getText().toString().trim();
        final String price = itemPrice.getText().toString().trim();
        final String quantity = itemQuantity.getText().toString().trim();
        final String location = itemLocation.getText().toString().trim();
        final String category = itemCategory.getText().toString().trim();
        final String purchaseDate = itemPurchaseD.getText().toString().trim();
        final String warrantyEnd = itemWendD.getText().toString().trim();
        final String supplName = itemSupName.getText().toString().trim();
        final String comment = itemComments.getText().toString().trim();

        Bitmap bitmap = ((BitmapDrawable) itemImage.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        final String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

        databaseReference.child(iKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.child(iKey).child("name").setValue(name);
                databaseReference.child(iKey).child("price").setValue(price);
                databaseReference.child(iKey).child("quantity").setValue(quantity);
                databaseReference.child(iKey).child("location").setValue(location);
                databaseReference.child(iKey).child("category").setValue(category);
                databaseReference.child(iKey).child("purchaseDate").setValue(purchaseDate);
                databaseReference.child(iKey).child("warrantyEnd").setValue(warrantyEnd);
                databaseReference.child(iKey).child("supplName").setValue(supplName);
                databaseReference.child(iKey).child("comments").setValue(comment);
                databaseReference.child(iKey).child("imagePath").setValue(base64Image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent updateItem = new Intent (EditItemActivity.this, ItemListActivity.class);
        updateItem.putExtra("id", id);
        startActivity(updateItem);
    }
}
