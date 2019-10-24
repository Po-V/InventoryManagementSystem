package com.example.inventorymanagementsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class AddItemActivity extends AppCompatActivity {

    private EditText itemName, itemPrice, itemQuantity, itemLocation, itemCategory, itemPurchaseD, itemSupName, itemWendD, itemComments;
    private Button saveButton;
    private ImageView itemImage;
    Uri FilePathUri;
    final int REQUEST_CODE_GALLERY = 999;
    DatePickerDialog picker;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    DatabaseReference databaseReference;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        itemName = findViewById(R.id.itemName);
        itemPrice = findViewById(R.id.itemPrice);
        itemQuantity = findViewById(R.id.itemQuantity);
        itemLocation = findViewById(R.id.itemLocation);
        itemCategory = findViewById(R.id.itemCategory);
        itemPurchaseD = findViewById(R.id.itemPurchaseDate);
        itemSupName = findViewById(R.id.itemSupplierName);
        itemWendD = findViewById(R.id.itemWarrantyEnd);
        itemComments = findViewById(R.id.itemComments);
        itemWendD.setInputType(InputType.TYPE_NULL);
        saveButton = findViewById(R.id.saveItem);
        itemImage = findViewById(R.id.itemIm);
        final Calendar cldr = Calendar.getInstance();

        id = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference("Item_Details").child(id);

        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(AddItemActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY
                );
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadItemData();
                Date date = cldr.getTime() ;
                scheduleNotification(getNotification( itemWendD .getText().toString()) , date.getTime()) ;
                Intent updateItem = new Intent (AddItemActivity.this, ItemListActivity.class);
                updateItem.putExtra("id", id);
                startActivity(updateItem);
            }
        });

        itemWendD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(AddItemActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                itemWendD.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
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
                Toast.makeText(AddItemActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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

    public void scheduleNotification (Notification notification, long delay){
        Intent notificationIntent = new Intent( this, NotificationPublisher. class ) ;
        notificationIntent.putExtra(NotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(NotificationPublisher. NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , delay , pendingIntent) ;
    }

    private Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( "WARRANTY ENDING ITEM" ) ;
        builder.setContentText(content + itemName.getText().toString().trim() + "has ending warranty") ;
        builder.setSmallIcon(R.drawable.logo) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }

    public void UploadItemData() {
        String name = itemName.getText().toString().trim();
        String price = itemPrice.getText().toString().trim();
        String quantity = itemQuantity.getText().toString().trim();
        String location = itemLocation.getText().toString().trim();
        String category = itemCategory.getText().toString().trim();
        String purchaseDate = itemPurchaseD.getText().toString().trim();
        String warrantyEnd = itemWendD.getText().toString().trim();
        String supplName = itemSupName.getText().toString().trim();
        String comment = itemComments.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Please enter item name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(quantity)) {
            Toast.makeText(getApplicationContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) itemImage.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Item i = new Item(base64Image, name, price, quantity, location, category, purchaseDate, warrantyEnd, supplName, comment);
        String itemId = databaseReference.push().getKey();
        // Adding image upload id s child element into databaseReference.
        databaseReference.child(itemId).setValue(i);
    }
}
