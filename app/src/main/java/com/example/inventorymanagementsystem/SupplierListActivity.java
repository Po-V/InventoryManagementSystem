package com.example.inventorymanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class SupplierListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Supplier> list;
    ArrayList<String> keys;
    SupplierViewAdapter adapter = null;
    ImageView imageViewUpdate;
    DatabaseReference databaseReference;
    final int REQUEST_CODE_GALLERY = 888;
    String id;
    private ImageButton addImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_list);

        listView = (ListView) findViewById(R.id.supplierListView);
        addImage = (ImageButton) findViewById(R.id.fab);
        list = new ArrayList<>();
        keys = new ArrayList<>();
        id = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference("Supplier_Details").child(id);
        adapter = new SupplierViewAdapter(this, R.layout.supplierrow, list);
        listView.setAdapter(adapter);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(Supplier.class));
                keys.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(Supplier.class));
                keys.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                list.add(dataSnapshot.getValue(Supplier.class));
                keys.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(Supplier.class));
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
                Intent updateSupplier = new Intent (SupplierListActivity.this, EditSupplierDetailsActivity.class);
                updateSupplier.putExtra("id", id);
                updateSupplier.putExtra("supplierKey", mKey);
                //Toast.makeText(getApplicationContext(), mKey,Toast.LENGTH_SHORT).show();
                startActivity(updateSupplier);

            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        Intent a = new Intent(SupplierListActivity.this, DashboardActivity.class);
                        a.putExtra("id", id);
                        startActivity(a);
                        break;
                    case R.id.navigation_item:
                        Intent b = new Intent(SupplierListActivity.this, ItemListActivity.class);
                        b.putExtra("id", id);
                        startActivity(b);
                        break;
                    case R.id.navigation_more:
                        Intent c = new Intent(SupplierListActivity.this, MoreActivity.class);
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
                Intent addItem = new Intent(SupplierListActivity.this, AddSupplierActivity.class);
                addItem.putExtra("id", id);
                startActivity(addItem);
            }
        });
    }
}
