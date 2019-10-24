package com.example.inventorymanagementsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class ItemListActivity extends AppCompatActivity {

    private ImageButton addImage, scanItem;
    ListView listView;
    private String barcodeVal;
    ArrayList<Item> list;
    ArrayList<String> keys;
    ItemViewAdapter adapter = null;
    ImageView imageViewUpdate;
    DatabaseReference databaseReference;
    public static final int PERMISSION_REQUEST = 200;
    public static final int REQUEST_CODE = 100;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        addImage = (ImageButton) findViewById(R.id.fab);
        scanItem = (ImageButton) findViewById(R.id.scansearch);
        listView = (ListView) findViewById(R.id.itemListView);
        list = new ArrayList<>();
        keys = new ArrayList<>();
        id = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference("Item_Details").child(id);
        adapter = new ItemViewAdapter(this, R.layout.itemrow, list);
        listView.setAdapter(adapter);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(Item.class));
                keys.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(Item.class));
                keys.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                list.add(dataSnapshot.getValue(Item.class));
                keys.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(Item.class));
                keys.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                //Supplier supplier = (Supplier) adapter.getAdapter().getItem(position);
                // String selectedFromList =(Supplier) (list.getItemAtPosition(position));
                //String uid = databaseReference.orderByChild(supplier).getRef().getKey();
                String mKey = keys.get(position);
                Intent updateItem = new Intent (ItemListActivity.this, EditItemActivity.class);
                updateItem.putExtra("id", id);
                updateItem.putExtra("itemKey", mKey);
                startActivity(updateItem);

            }
        });

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        Intent a = new Intent(ItemListActivity.this, DashboardActivity.class);
                        a.putExtra("id", id);
                        startActivity(a);
                        break;
                    case R.id.navigation_supplier:
                        Intent b = new Intent(ItemListActivity.this, SupplierListActivity.class);
                        b.putExtra("id", id);
                        startActivity(b);
                        break;
                    case R.id.navigation_more:
                        Intent c = new Intent(ItemListActivity.this, MoreActivity.class);
                        c.putExtra("id", id);
                        startActivity(c);

                        break;
                }
                return false;
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addItem = new Intent(ItemListActivity.this, AddItemActivity.class);
                addItem.putExtra("id", id);
                startActivity(addItem);
            }
        });

        scanItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemListActivity.this, ScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                //parcelable in order to pass objects between activities
                final Barcode barcode = data.getParcelableExtra("barcode");
                barcodeVal = barcode.rawValue;

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(barcodeVal)){
                            Intent updateItem = new Intent (ItemListActivity.this, EditItemActivity.class);
                            updateItem.putExtra("id", id);
                            updateItem.putExtra("itemKey", barcodeVal);
                            startActivity(updateItem);
                        }
                        else{
                            Toast.makeText(ItemListActivity.this, "Item does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
