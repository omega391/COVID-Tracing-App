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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Homepage extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101 ;
    EditText editText, photoURL;
    ImageView image;
    Button button;
    Uri uriProfileImage;
    String profileImageUrl;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        image = findViewById(R.id.imageView);
        editText = findViewById(R.id.editTextTextPersonName);
        button = findViewById(R.id.btnsave);
        mAuth = FirebaseAuth.getInstance();
        photoURL = findViewById(R.id.PhotoURL);
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

    }

    private void loadUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();


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
                       profileImageUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                       photoURL.setText(profileImageUrl);
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