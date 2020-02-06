package com.leventenyiro.firebaseprototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.ValueEventListener;

public class RegActivity extends AppCompatActivity {

    private EditText inputUsername, inputEmail, inputFirstname, inputLastname, inputPassword;
    private Button btnBack, btnReg;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputUsername.getText().toString().isEmpty() ||
                        inputEmail.getText().toString().isEmpty() ||
                        inputFirstname.getText().toString().isEmpty() ||
                        inputLastname.getText().toString().isEmpty() ||
                        inputPassword.getText().toString().isEmpty()) {
                    Toast.makeText(RegActivity.this, "Valami nincs kitöltve!", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(inputEmail.getText().toString(), inputPassword.getText().toString())
                            .addOnCompleteListener(RegActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (!user.isEmailVerified()) {
                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    onBackPressed();
                                                    Toast.makeText(RegActivity.this, "Erősítsd meg a jelszót!", Toast.LENGTH_LONG).show();
                                                    User u = new User();
                                                    u.setUsername(inputUsername.getText().toString());
                                                    u.setEmail(inputEmail.getText().toString());
                                                    u.setFirstname(inputFirstname.getText().toString());
                                                    u.setLastname(inputLastname.getText().toString());
                                                    db.child(String.valueOf(id + 1)).setValue(u);
                                                }
                                            });
                                        }
                                    }
                                    else {
                                        Toast.makeText(RegActivity.this, "Sikertelen regisztráció!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void init() {
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputFirstname = findViewById(R.id.inputFirstname);
        inputLastname = findViewById(R.id.inputLastname);
        inputPassword = findViewById(R.id.inputPassword);
        btnBack = findViewById(R.id.btnBack);
        btnReg = findViewById(R.id.btnReg);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("user");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    id = dataSnapshot.getChildrenCount();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegActivity.this, LoginActivity.class));
        finish();
    }
}
