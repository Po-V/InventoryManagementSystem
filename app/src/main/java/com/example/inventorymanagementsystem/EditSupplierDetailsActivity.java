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

public class EditSupplierDetailsActivity extends AppCompatActivity {

    private EditText supplierName, supplierEmail, supplierContact, supplierAddress, supplierComments;
    private Button updateButton, deleteButton;
    private ImageView personImage;
    Uri FilePathUri=null;
    String uriIm;
    private String id;
    private String iKey;
    final int REQUEST_CODE_GALLERY = 999;
    DatabaseReference databaseReference;
    ArrayList<String> supInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_supplier_details);

        supInfo = new ArrayList<>();
        supplierName = findViewById(R.id.edtSupplierName);
        supplierEmail = findViewById(R.id.edtSupplierEmail);
        supplierContact= findViewById(R.id.edtSupplierPhone);
        supplierAddress = findViewById(R.id.edtSupplierAddress);
        supplierComments = findViewById(R.id.edtSupplierComments);
        updateButton = findViewById(R.id.btnUpdate);
        personImage = findViewById(R.id.updateSupplierIm);
        deleteButton = findViewById(R.id.btnDelete);
        updateButton = findViewById(R.id.btnUpdate);

        id = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference("Supplier_Details").child(id);
        iKey = getIntent().getStringExtra("itemKey");

        databaseReference.child(iKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // supInfo.add(dataSnapshot.getChildren())
                Supplier supInfo = dataSnapshot.getValue(Supplier.class);
                supplierName.setText(supInfo.getName());
                supplierEmail.setText(supInfo.getEmail());
                supplierContact.setText(supInfo.getContact());
                supplierAddress.setText(supInfo.getAddress());
                supplierComments.setText(supInfo.getComments());
                uriIm = supInfo.getImagePath();
                byte[] decodedString = Base64.decode(supInfo.getImagePath(), Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                personImage.setImageBitmap(bmp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        personImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(EditSupplierDetailsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY
                );
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSupplierData();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(iKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot supplierSnapshot: dataSnapshot.getChildren()) {
                            supplierSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
                Toast.makeText(EditSupplierDetailsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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

    public void UpdateSupplierData() {
        final String name = supplierName.getText().toString().trim();
        final String email = supplierEmail.getText().toString().trim();
        final String contact = supplierContact.getText().toString().trim();
        final String address = supplierAddress.getText().toString().trim();
        final String comments = supplierComments.getText().toString().trim();

        Bitmap bitmap = ((BitmapDrawable) personImage.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        final String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

        databaseReference.child(iKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.child(iKey).child("imagePath").setValue(base64Image);
                databaseReference.child(iKey).child("name").setValue(name);
                databaseReference.child(iKey).child("email").setValue(email);
                databaseReference.child(iKey).child("contact").setValue(contact);
                databaseReference.child(iKey).child("address").setValue(address);
                databaseReference.child(iKey).child("comments").setValue(comments);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent updateSupplier = new Intent (EditSupplierDetailsActivity.this, SupplierListActivity.class);
        updateSupplier.putExtra("id", id);
        startActivity(updateSupplier);
    }
}
