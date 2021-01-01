package com.example.covidtracingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class Homepage extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101 ;
    EditText editText, photoURL;
    TextView FNAME, MNAME, LNAME, CPNUM, ADDRESS, EMAIL;
    ImageView image;
    Button button, logout;
    Uri uriProfileImage;
    String profileImageUrl;
    FirebaseAuth mAuth;
    StorageReference mStorageReference;
    DatabaseReference databaseReference;
    FirebaseUser user;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        image = findViewById(R.id.imageView);
        button = findViewById(R.id.btnsave);
        mAuth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout);
        FNAME = findViewById(R.id.FNAME);
        MNAME = findViewById(R.id.MNAME);
        LNAME = findViewById(R.id.LNAME);
        CPNUM = findViewById(R.id.CPNUM);
        ADDRESS = findViewById(R.id.ADDRESS);
        EMAIL = findViewById(R.id.EMAIL);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Userinfo").child(uid);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(Homepage.this, login.class);
                startActivity(i);
                finish();
                Toast.makeText(Homepage.this, "You have been Logged out.", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url_link = dataSnapshot.child("url").getValue().toString();
                Picasso.get()
                        .load(url_link)
                        .into(image);

                String firstname = dataSnapshot.child("fname").getValue().toString();
                String middlename = dataSnapshot.child("mname").getValue().toString();
                String lastname = dataSnapshot.child("lname").getValue().toString();
                String cpnumb = dataSnapshot.child("cpnumber").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String address = dataSnapshot.child("address").getValue().toString();
                FNAME.setText(middlename);
                MNAME.setText(firstname);
                LNAME.setText(lastname);
                CPNUM.setText(cpnumb);
                EMAIL.setText(email);
                ADDRESS.setText(address);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Homepage.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(Homepage.this, MainActivity.class));
        }
    }
}