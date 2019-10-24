package com.example.inventorymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    private String id;
    private Button lendButton, warrantyButton;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        lendButton = findViewById(R.id.lentItemsView);
        warrantyButton = findViewById(R.id.warrantyEndItemsView);
        id = getIntent().getStringExtra("id");

        databaseReference = FirebaseDatabase.getInstance().getReference("Lend_Details").child(id);

        lendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lendList = new Intent(DashboardActivity.this, LendListActivity.class);
                lendList.putExtra("id", id);
                startActivity(lendList);
            }
        });

        warrantyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent warrantyList = new Intent(DashboardActivity.this, WarrantyEndingItemsActivity.class);
                warrantyList.putExtra("id", id);
                startActivity(warrantyList);
            }
        });

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_item:
                        Intent a = new Intent(DashboardActivity.this, ItemListActivity.class);
                        a.putExtra("id", id);
                        startActivity(a);
                        break;
                    case R.id.navigation_supplier:
                        Intent b = new Intent(DashboardActivity.this, SupplierListActivity.class);
                        b.putExtra("id", id);
                        startActivity(b);
                        break;
                    case R.id.navigation_more:
                        Intent c = new Intent(DashboardActivity.this, MoreActivity.class);
                        c.putExtra("id", id);
                        startActivity(c);

                        break;
                }
                return false;
            }
        });
    }

}
