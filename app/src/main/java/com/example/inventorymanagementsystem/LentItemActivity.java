package com.example.inventorymanagementsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class LentItemActivity extends AppCompatActivity {

    private EditText itemName, lendPerson, lendDate, returnDate, itemComments;
    private Button saveButton;
    DatePickerDialog picker;
    String id;
    DatabaseReference databaseReference;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lent_item);

        itemName = findViewById(R.id.itemName);
        lendPerson = findViewById(R.id.lendPerson);
        lendDate = findViewById(R.id.lendOn);
        returnDate = findViewById(R.id.returnOn);
        itemComments = findViewById(R.id.itemComments);
        saveButton = findViewById(R.id.saveDetails);
        lendDate.setInputType(InputType.TYPE_NULL);
        returnDate.setInputType(InputType.TYPE_NULL);
        id = getIntent().getStringExtra("id");
        final Calendar cldr = Calendar.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("Lend_Details").child(id);

        lendDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(LentItemActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                lendDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        returnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(LentItemActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                returnDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLendingInfo();
                Date date = cldr.getTime() ;
                scheduleNotification(getNotification( lendDate .getText().toString()) , date.getTime()) ;
                Intent updateItem = new Intent (LentItemActivity.this, LendListActivity.class);
                updateItem.putExtra("id", id);
                startActivity(updateItem);
            }
        });
    }

    public void saveLendingInfo(){
        String name = itemName.getText().toString().trim();
        String lendName = lendPerson.getText().toString().trim();
        String lendOn = lendDate.getText().toString().trim();
        String returnOn = returnDate.getText().toString().trim();
        String lendComments = itemComments.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Please enter item name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(lendName)) {
            Toast.makeText(getApplicationContext(), "Please enter lend name", Toast.LENGTH_SHORT).show();
            return;
        }

        LendItem i = new LendItem(name, lendName, lendOn, returnOn, lendComments);
        String lendId = databaseReference.push().getKey();
        databaseReference.child(lendId).setValue(i);
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
        builder.setContentTitle( "LENT ITEM DUE" ) ;
        builder.setContentText(content + itemName.getText().toString().trim() + "is due") ;
        builder.setSmallIcon(R.drawable.logo) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }

}
