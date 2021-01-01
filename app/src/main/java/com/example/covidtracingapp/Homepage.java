package com.example.covidtracingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.net.MalformedURLException;
import java.net.URL;

public class Homepage extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101 ;
    EditText editText, photoURL, FNAME;
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
        editText = findViewById(R.id.editTextTextPersonName);
        button = findViewById(R.id.btnsave);
        mAuth = FirebaseAuth.getInstance();
        photoURL = findViewById(R.id.PhotoURL);
        logout = findViewById(R.id.logout);
        FNAME = findViewById(R.id.FNAME);
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
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
        loadUserInformation();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url_link = dataSnapshot.child("url").getValue().toString();
                Picasso.get()
                        .load(url_link)
                        .into(image);

                String user_name = dataSnapshot.child("fname").getValue().toString();
                FNAME.setText(user_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();
        String a = mAuth.getCurrentUser().toString();
        photoURL.setText(profileImageUrl);

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(image);
            }
            if (user.getDisplayName() != null) {

            }


        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(Homepage.this, MainActivity.class));
        }
    }
    private void saveUserInformation() {
            String displayName = editText.getText().toString();

            if(displayName.isEmpty()){
                editText.setError("Name is Required");
                editText.requestFocus();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if(user!=null && profileImageUrl != null){
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .setPhotoUri(Uri.parse(profileImageUrl))
                        .build();
                user.updateProfile(profile)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Homepage.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){

           uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
            uploadImageToFirebaseStorage();
        }
    }

    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

    if(uriProfileImage != null){
        profileImageRef.putFile(uriProfileImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;
                                String j = downloadUrl.toString();
                                photoURL.setText(j);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        }

    }

    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }
}