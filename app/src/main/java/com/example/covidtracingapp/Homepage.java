package com.example.covidtracingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
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

import at.markushi.ui.CircleButton;

public class Homepage extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101 ;
    EditText editText, photoURL;
    TextView FNAME, MNAME, LNAME, CPNUM, ADDRESS, EMAIL;
    ImageView image;
    Button button, logout;
    CircleButton qrbtn;
    Uri uriProfileImage;
    String profileImageUrl;
    FirebaseAuth mAuth;
    StorageReference mStorageReference;
    DatabaseReference databaseReference;
    FirebaseUser user;
    String uid;
    ProgressDialog progressDialog;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        image = findViewById(R.id.imageView);

        mAuth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout);
        FNAME = findViewById(R.id.FNAME);
        MNAME = findViewById(R.id.MNAME);
        LNAME = findViewById(R.id.LNAME);
        CPNUM = findViewById(R.id.CPNUM);
        ADDRESS = findViewById(R.id.ADDRESS);
        EMAIL = findViewById(R.id.EMAIL);
        qrbtn = findViewById(R.id.qrbtn);


        // Content of Dialog of QR
        final ImageView qrView = findViewById(R.id.qrImageView);
        final Button donebtn = findViewById(R.id.donebtn);
        dialog = new Dialog(Homepage.this);
// Loading ProgressDialog
        progressDialog = new ProgressDialog(Homepage.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
// gets the users UserID when user Logs in.
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Userinfo").child(uid);

// Logout Button
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

// Fetching Data From firebase Database using UserID.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Fetch QR url link that is storage on Firebase Storage.
                final String url_link = dataSnapshot.child("dpUrl").getValue().toString();
                // used Picasso to set QR using URL fetched from Database.
                Picasso.get()
                        .load(url_link)
                        .into(image);


                qrbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       Intent intent = new Intent(Homepage.this, QRCODE.class);
                       startActivity(intent);
                    }
                });

                String firstname = dataSnapshot.child("fname").getValue().toString();
                String middlename = dataSnapshot.child("mname").getValue().toString();
                String lastname = dataSnapshot.child("lname").getValue().toString();
                String cpnumb = dataSnapshot.child("cpnumber").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String address = dataSnapshot.child("address").getValue().toString();
                FNAME.setText(firstname);
                MNAME.setText(middlename);
                LNAME.setText(lastname);
                CPNUM.setText(cpnumb);
                EMAIL.setText(email);
                ADDRESS.setText(address);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Homepage.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDialog() {


    }

    // Onstart to Check if a User is Already Logged In.
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(Homepage.this, MainActivity.class));
        }
    }
    // uses AlertDialog to confirm if user intentionally pressed Back to Sign out.
    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Sign-out")
                .setMessage("Do you want to Sign out?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                    }
                }).create().show();

    }
}