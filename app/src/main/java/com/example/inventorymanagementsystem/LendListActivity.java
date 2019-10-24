package com.example.inventorymanagementsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LendListActivity extends AppCompatActivity {

    ListView listView;
    DatabaseReference databaseReference;
    ArrayList<String> keys;
    private String id;
    ArrayList<LendItem> list;
    LendViewAdapter adapter = null;
    LendViewAdapter adapter2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_list);

        listView = (ListView) findViewById(R.id.lentListView);
        id = getIntent().getStringExtra("id");
        list = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Lend_Details").child(id);
        adapter = new LendViewAdapter(this, R.layout.lendrow, list);
        //adapter2 = adapter;
        listView.setAdapter(adapter);


        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(LendItem.class));
                keys.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(LendItem.class));
                keys.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                list.add(dataSnapshot.getValue(LendItem.class));
                keys.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(LendItem.class));
                keys.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long args) {
                AlertDialog.Builder adb=new AlertDialog.Builder(LendListActivity.this);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + position);
                final String mKey = keys.get(position);
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child(mKey).addValueEventListener(new ValueEventListener() {
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
                        adapter.notifyDataSetChanged();
                    }});
                adb.show();
            }
        });
    }
}
