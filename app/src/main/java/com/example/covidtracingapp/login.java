package com.example.covidtracingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class login extends AppCompatActivity {
    EditText CPnumber;
    CheckBox CBPassword;
    EditText Password;
    Button btnlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        CPnumber = findViewById(R.id.CPnumber);
        Password = findViewById(R.id.Password);
        CBPassword = findViewById(R.id.CBPassword);
        CBPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            // hide and show password
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (b) {
                    Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }

        });
        btnlogin = findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
