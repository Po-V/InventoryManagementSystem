package com.example.inventorymanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManageUserActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName;
    private Button btnaddUser, deleteUser;
    private ProgressBar progressBar;
    private String id;
    private FirebaseAuth auth;
    private FirebaseAuth auth2;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        id = getIntent().getStringExtra("id");
        final FirebaseUser userNow = FirebaseAuth.getInstance().getCurrentUser();
        btnaddUser = (Button) findViewById(R.id.addUser);
        deleteUser = (Button) findViewById(R.id.deleteUser);
        inputEmail = (EditText) findViewById(R.id.newEmail);
        inputName = (EditText) findViewById(R.id.newName);
        inputPassword = (EditText) findViewById(R.id.newpassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        databaseReference = FirebaseDatabase.getInstance().getReference("Admins");

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("[https://inventorymanagementsyste-3b0ca.firebaseio.com/]")
                .setApiKey("AIzaSyCjMKdTa92VrcSakje3_szMHfl8STNlGcQ")
                .setApplicationId("inventorymanagementsyste-3b0ca").build();

        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "InventoryManagementSystem");
            auth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            auth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("InventoryManagementSystem"));
        }

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ManageUserActivity.this);
                dialog.setTitle("ARE YOU SURE ?");
                dialog.setMessage("Deleting this account means you will not be able to access the app.");
                dialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userNow.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ManageUserActivity.this, "deleted account", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ManageUserActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(ManageUserActivity.this, "failed to delete" , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                dialog.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        btnaddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                final String name = inputName.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth2.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(ManageUserActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                String id  =    FirebaseAuth.getInstance().getCurrentUser().getUid();

                                if (!task.isSuccessful()) {
                                    Toast.makeText(ManageUserActivity.this, "failed to add user" + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    User user = new User(id, name);
                                    databaseReference.child(id).setValue(user);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(ManageUserActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    auth2.signOut();
                                }
                            }
                        });

            }
        });

    }
}
