package com.example.covidtracingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class QRCODE extends AppCompatActivity {

    Button donebtn2;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ImageView Viewqr;
    String uid;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_c_o_d_e);
        Viewqr = findViewById(R.id.Viewqr);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        donebtn2 = findViewById(R.id.donebtn2);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Userinfo").child(uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
             String qr = datasnapshot.child("url").getValue().toString();

                Picasso.get().load(qr).into(Viewqr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError ERROR) {

            }
        });
        donebtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QRCODE.this, Homepage.class);
                startActivity(intent);
                finish();
            }
        });



    }
}