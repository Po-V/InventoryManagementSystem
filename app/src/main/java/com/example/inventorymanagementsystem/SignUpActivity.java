package com.example.inventorymanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName;
    private Button btnSignIn, btnSignup;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        auth = FirebaseAuth.getInstance();
        btnSignIn = (Button) findViewById(R.id.sign_up_button);
        btnSignup = (Button) findViewById(R.id.SignUp);
        inputEmail = (EditText) findViewById(R.id.sEmail);
        inputName = (EditText) findViewById(R.id.sName);
        inputPassword = (EditText) findViewById(R.id.spassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        databaseReference = FirebaseDatabase.getInstance().getReference("Admins");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
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
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                String id  =    FirebaseAuth.getInstance().getCurrentUser().getUid();
                                User user = new User(id, name);
                                String usersId = databaseReference.push().getKey();
                                databaseReference.child(usersId).setValue(user);

                                Toast.makeText(SignUpActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Sign up failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent dashboard = new Intent (SignUpActivity.this, DashboardActivity.class);
                                    dashboard.putExtra("id", usersId);
                                    startActivity(dashboard);
                                }
                            }
                        });

            }
        });
    }
}
