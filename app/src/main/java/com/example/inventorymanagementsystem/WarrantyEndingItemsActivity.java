package com.example.inventorymanagementsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class WarrantyEndingItemsActivity extends AppCompatActivity {

    private ImageButton addImage, scanItem;
    ListView listView;
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
        setContentView(R.layout.activity_warranty_ending_items);

        list = new ArrayList<>();
        keys = new ArrayList<>();
        id = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference("Item_Details").child(id);


        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    Item itemInfo = ds.getValue(Item.class);
                    String itemDate = itemInfo.getWarrantyEnd();
                    Date date = new Date();
                    String currentDate= new SimpleDateFormat("dd/mm/yyyy").format(date);
                    if(itemDate.equals(currentDate)) {
                        list.add(dataSnapshot.getValue(Item.class));
                        keys.add(dataSnapshot.getKey());
                    }
                   // adapter.notifyDataSetChanged();
                }
                listView = (ListView) findViewById(R.id.ItemWListView);
                adapter = new ItemViewAdapter(WarrantyEndingItemsActivity.this, R.layout.itemrow, list);
                listView.setAdapter(adapter);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    Item itemInfo = ds.getValue(Item.class);
                    String itemDate = itemInfo.getWarrantyEnd();
                    Date date = new Date();
                    String currentDate= new SimpleDateFormat("dd/mm/yyyy").format(date);
                    if(itemDate.equals(currentDate)) {
                        list.add(dataSnapshot.getValue(Item.class));
                        keys.add(dataSnapshot.getKey());
                    }
                    // adapter.notifyDataSetChanged();
                }
                listView = (ListView) findViewById(R.id.ItemWListView);
                adapter = new ItemViewAdapter(WarrantyEndingItemsActivity.this, R.layout.itemrow, list);
                listView.setAdapter(adapter);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    Item itemInfo = ds.getValue(Item.class);
                    String itemDate = itemInfo.getWarrantyEnd();
                    Date date = new Date();
                    String currentDate= new SimpleDateFormat("dd/mm/yyyy").format(date);
                    if(itemDate.equals(currentDate)) {
                        list.add(dataSnapshot.getValue(Item.class));
                        keys.add(dataSnapshot.getKey());
                    }
                    // adapter.notifyDataSetChanged();
                }
                listView = (ListView) findViewById(R.id.ItemWListView);
                adapter = new ItemViewAdapter(WarrantyEndingItemsActivity.this, R.layout.itemrow, list);
                listView.setAdapter(adapter);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    Item itemInfo = ds.getValue(Item.class);
                    String itemDate = itemInfo.getWarrantyEnd();
                    Date date = new Date();
                    String currentDate= new SimpleDateFormat("dd/mm/yyyy").format(date);
                    if(itemDate.equals(currentDate)) {
                        list.add(dataSnapshot.getValue(Item.class));
                        keys.add(dataSnapshot.getKey());
                    }
                    // adapter.notifyDataSetChanged();
                }
                listView = (ListView) findViewById(R.id.ItemWListView);
                adapter = new ItemViewAdapter(WarrantyEndingItemsActivity.this, R.layout.itemrow, list);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}