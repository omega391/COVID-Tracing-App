package com.example.covidtracingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button btnregister, btnlogin;
//    FirebaseUser user;
//    String uid;
    FirebaseAuth mAuth;
    final int CAMERA_REQUEST = 1888;
    final int MY_CAMERA_PERMISSION_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        user = FirebaseAuth.getInstance().getCurrentUser();
//        uid = user.getUid();
        btnregister = (Button) findViewById(R.id.btnregister);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        FirebaseAuth.getInstance().signOut();

 // check for Camera Permissions
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){

        }else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 100);
        }
 // check for Permission of Storage Access

        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 44);
        }

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openregister();

            }
        });
       btnlogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(MainActivity.this, login.class);
               startActivity(intent);
           }
       });
    }
    //Open Registar page
    public void openregister(){
        Intent intent = new Intent(this, register.class);
        startActivity(intent);
    }

}
