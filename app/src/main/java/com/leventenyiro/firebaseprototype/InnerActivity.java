package com.leventenyiro.firebaseprototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InnerActivity extends AppCompatActivity {

    private TextView userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner);

        userInfo = findViewById(R.id.userInfo);

        userInfo();
    }

    public void userInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user");
        ref.orderByKey().equalTo(getSharedPreferences("variables", Context.MODE_PRIVATE).getString("userId", ""))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            userInfo.setText("Név: " + snapshot.child("username").getValue().toString() + "\n" +
                                    "Email: " + snapshot.child("email").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
