package com.leventenyiro.firebaseprototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText inputUsernameEmail, inputPassword;
    private Button btnLogin, btnReg;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkConnected())
                    Toast.makeText(LoginActivity.this, "Nincs internetkapcsolat!", Toast.LENGTH_SHORT).show();
                else {
                    if (inputUsernameEmail.getText().toString().isEmpty() || inputPassword.getText().toString().isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Valami nincs kitöltve!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user");
                        ref.orderByChild("username").equalTo(inputUsernameEmail.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (dataSnapshot.getChildrenCount() == 1)
                                            login(String.valueOf(snapshot.child("email").getValue()));
                                    }
                                }
                                else
                                    login(inputUsernameEmail.getText().toString());
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                    }
                }
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkConnected())
                    Toast.makeText(LoginActivity.this, "Nincs internetkapcsolat!", Toast.LENGTH_SHORT).show();
                else {
                    startActivity(new Intent(LoginActivity.this, RegActivity.class));
                    finish();
                }
            }
        });
    }

    private void init() {
        inputUsernameEmail = findViewById(R.id.inputUsernameEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnReg = findViewById(R.id.btnReg);
        mAuth = FirebaseAuth.getInstance();
    }

    private void login(String email) {
        final String finalEmail = email;
        mAuth.signInWithEmailAndPassword(email, inputPassword.getText().toString())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (finalEmail.equals("admin@lightairlines.com")) {
                                getUserId(finalEmail);
                            }
                            else if (!user.isEmailVerified()) {
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(LoginActivity.this, "Erősítsd meg az emailed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Be vagy jelentkezve!", Toast.LENGTH_SHORT).show();
                                getUserId(finalEmail);
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Hibás bejelentkezési adatok!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getUserId(String email) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user");
        ref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    getSharedPreferences("variables", Context.MODE_PRIVATE).edit()
                            .putString("userId", snapshot.getKey()).apply();
                    if (snapshot.getKey().equals("1")) {
                        // admin
                        Toast.makeText(LoginActivity.this, "Admin vagy", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        startActivity(new Intent(LoginActivity.this, InnerActivity.class));
                        finish();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
