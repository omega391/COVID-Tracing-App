package com.example.covidtracingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class register extends AppCompatActivity {
    CheckBox CBPassword;
    EditText Email, Fname, Mname, Lname, CPnumber, HomeAddress;
    TextView AccID;
    ImageView QRHolder;
    Button btnsubmit;
    DatabaseReference DB;

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
        HomeAddress = findViewById(R.id.HomeAddress);
        btnsubmit = findViewById(R.id.btnsubmit);
        QRHolder = findViewById(R.id.QRHolder);
        //Database reference
        DB = FirebaseDatabase.getInstance().getReference().child("Userinfo");

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInformation();
            }
        });
        //Show and Hide Password using Checkbox

    }
        //save input to database
    private void addInformation() {
        String email = Email.getText().toString().trim();
        String fname = Fname.getText().toString().trim();
        String mname = Mname.getText().toString().trim();
        String lname = Lname.getText().toString().trim();
        String cpnumber = CPnumber.getText().toString().trim();
        String address = HomeAddress.getText().toString().trim();


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
        } else {//to open QRgenerator page and let to import data here
            String id = DB.push().getKey();

            Information information = new Information(email, fname, mname, lname, cpnumber, address);
            DB.child(id).setValue(information);
            AccID.setText(id);
            String Value = AccID.getText().toString();
            Intent i = new Intent(register.this, QRgenerator.class);
            i.putExtra("key", Value);

            startActivity(i);
            finish();

            Toast.makeText(this,"Record added successfully", Toast.LENGTH_LONG).show();
        }

    }
}