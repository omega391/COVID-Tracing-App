package com.example.covidtracingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.bitmap_recycle.ByteArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.j2objc.annotations.ReflectionSupport;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.regex.Pattern;


public class register extends AppCompatActivity {
    CheckBox CBPassword;
    EditText Email, Fname, Mname, Lname, CPnumber, HomeAddress, pwd;
    TextView AccID, ID2;
    ImageView dpHolder;
    Button btnsubmit;
    DatabaseReference DB;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String uid;
    OutputStream outputStream;
    ProgressDialog progressDialog;
    final int PICK_IMAGES = 100;
    final int CAMERA_REQUEST = 1888;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AccID = findViewById(R.id.ID);
        Email = findViewById(R.id.Email);
        Fname = findViewById(R.id.Fname);
        Mname = findViewById(R.id.Mname);
        Lname = findViewById(R.id.Lname);
        CPnumber = findViewById(R.id.CPnumber);
        HomeAddress = findViewById(R.id.homeAddress);
        pwd = (EditText)findViewById(R.id.pwd);
        btnsubmit = findViewById(R.id.btnsubmit);
        dpHolder = findViewById(R.id.dpHolder);
        ID2 = findViewById(R.id.ID2);
        dpHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context;
                new AlertDialog.Builder(register.this)
                    .setTitle("Upload Profile Picture")
                    .setMessage("Capture Picture using Camera or get Picture From Storage")
                    .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);

                        }
                    })
                    .setPositiveButton("Upload From Storage", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selectdp();
                        }
                    }).create().show();

            }
        });
        mAuth = FirebaseAuth.getInstance();
        //Database reference
        DB = FirebaseDatabase.getInstance().getReference().child("Userinfo");

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String Username = Email.getText().toString().trim();
                String password = pwd.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(Username).matches()){
                    Email.setError("Please enter a Valid email");
                    Email.requestFocus();
                    return;
                }
                if(password.length()<6){
                    pwd.setError("Minimum length of password should be 6");
                    pwd.requestFocus();
                    return;

                }
                progressDialog = new ProgressDialog(register.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );
                mAuth.createUserWithEmailAndPassword(Username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            uid = user.getUid();
                            final StorageReference dpImage = FirebaseStorage.getInstance().getReference("profile/" + uid + ".jpg");

                                BitmapDrawable drawable = (BitmapDrawable)dpHolder.getDrawable();
                                Bitmap bitmap2 = drawable.getBitmap();
                                ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                                bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
                                byte[] data2 = baos2.toByteArray();
                                UploadTask uploadtask2 = dpImage.putBytes(data2);

                                uploadtask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        dpImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final Uri dpurl = uri;
                                                final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + uid + ".jpg");
                                                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                                try{
                                                    //generate QR code
                                                    BitMatrix bitMatrix = multiFormatWriter.encode(uid, BarcodeFormat.QR_CODE,500,500);
                                                    BarcodeEncoder                    barcodeEncoder = new BarcodeEncoder();
                                                    final Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100,baos);
                                                    byte[] data = baos.toByteArray();
                                                UploadTask uploadtask = profileImageRef.putBytes(data);
                                                uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                Uri downlurl = uri;
                                                                String email = Email.getText().toString().trim();
                                                                String fname = Fname.getText().toString().trim();
                                                                String mname = Mname.getText().toString().trim();
                                                                String lname = Lname.getText().toString().trim();
                                                                String cpnumber = CPnumber.getText().toString().trim();
                                                                String address = HomeAddress.getText().toString().trim();
                                                                String url = downlurl.toString();
                                                                String dpUrl = dpurl.toString();



                                                                //prevent user for saving with null values
                                                                if (Email.length()==0){
                                                                    Email.setError("Invalid input");
                                                                }
                                                                else if (Fname.length() == 0) {
                                                                    Fname.setError("Invalid input");
                                                                } else if (Mname.length() == 0) {
                                                                    Mname.setError("Invalid input");
                                                                } else if (Lname.length() == 0) {
                                                                    Lname.setError("Invalid input");
                                                                } else if (CPnumber.length() == 0) {
                                                                    CPnumber.setError("Invalid input");
                                                                } else if (HomeAddress.length() == 0) {
                                                                    HomeAddress.setError("Invalid input");
                                                                } else {

                                                                    //to open QRgenerator page and let to import data here
                                                                    String id = DB.push().getKey();
                                                                    // Get which String is inserted to Database
                                                                    Information information = new Information(email, fname, mname, lname, cpnumber, address, url, dpUrl);
                                                                    DB.child(uid).setValue(information);
                                                                    AccID.setText(uid);
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(register.this, "User Registered Successful", Toast.LENGTH_SHORT).show();

                                                                    Intent i = new Intent(register.this, QRgenerator.class);
                                                                    i.putExtra("key", uid);
                                                                    startActivity(i);
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                                uploadtask.addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(register.this, "Registration Failed, Please Try Again Later.", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    }
                                                });

                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }

                                            }
                                        });
                                    }

                                });

                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(register.this, "You are Already Registered", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else{
                                Toast.makeText(register.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }


                        }
                    }
                });

            }
        });


    }

    public void selectdp (){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGES);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == register.RESULT_OK){
            Bitmap photo = (Bitmap)data.getExtras().get("data");
            dpHolder.setImageBitmap(photo);
        }
        if (requestCode == PICK_IMAGES && resultCode == register.RESULT_OK && null != data){
            Uri selectedImageUri = data.getData();
            dpHolder.setImageURI(selectedImageUri);
            Drawable drawable = dpHolder.getDrawable();
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
              dpHolder.setImageBitmap(bitmap);

        }
    }
//        FRAAAAAANZZZZZZZ
//
//                FRAAAAAANZZZZZZZ
//        FRAAAAAANZZZZZZZ
//                FRAAAAAANZZZZZZZ
//        FRAAAAAANZZZZZZZ
//                FRAAAAAANZZZZZZZ
//        FRAAAAAANZZZZZZZ
// https://github.com/omega391/COVID-Tracing-App/invitations
}
