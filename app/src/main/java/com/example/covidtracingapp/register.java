package com.example.covidtracingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;


public class register extends AppCompatActivity {
    CheckBox CBPassword;
    EditText Email, Fname, Mname, Lname, CPnumber, HomeAddress, pwd;
    TextView AccID;
    ImageView QRHolder;
    Button btnsubmit;
    DatabaseReference DB;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String uid;
    OutputStream outputStream;
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
        HomeAddress = findViewById(R.id.pwd);
        pwd = (EditText)findViewById(R.id.pwd);
        btnsubmit = findViewById(R.id.btnsubmit);
        QRHolder = findViewById(R.id.QRHolder);
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
                mAuth.createUserWithEmailAndPassword(Username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            uid = user.getUid();

                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                            try{//generate QR code
                                BitMatrix bitMatrix = multiFormatWriter.encode(uid, BarcodeFormat.QR_CODE,500,500);
                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
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


                                                    Information information = new Information(email, fname, mname, lname, cpnumber, address, url);
                                                    DB.child(uid).setValue(information);
                                                    AccID.setText(uid);
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

                                    }
                                });
                            }catch (Exception e){
                            e.printStackTrace();
                        }

                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(register.this, "You are Already Registered", Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(register.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                });

            }
        });
        //Show and Hide Password using Checkbox

    }


}