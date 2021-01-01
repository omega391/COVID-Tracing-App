package com.example.covidtracingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    EditText CPnumber;
    CheckBox CBPassword;
    EditText Password;
    Button btnlogin;
    FirebaseAuth mAuth;
//    FirebaseUser user;
//    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        user = FirebaseAuth.getInstance().getCurrentUser();
//        uid = user.getUid();
        CPnumber = findViewById(R.id.CPnumber);
        Password = findViewById(R.id.Password);
        CBPassword = findViewById(R.id.CBPassword);
        mAuth = FirebaseAuth.getInstance();
        btnlogin = findViewById(R.id.btnlogin);

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
 //Login Button   
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            login();
            }
        });
    }
    private void login(){
        String Username = CPnumber.getText().toString().trim();
        String password = Password.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(Username).matches()){
            CPnumber.setError("Please Enter Valid Email.");
            CPnumber.requestFocus();
            return;
        }
        if(password.isEmpty()){
            Password.setError("Password is Required");
            Password.requestFocus();
            return;
        }
        if(password.length()<6){
            Password.setError("Please Re-enter Password");
            Password.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(Username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    finish();
                    Intent intent = new Intent(login.this, Homepage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else {
                    Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null){
//            Toast.makeText(login.this, uid, Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(login.this, Homepage.class));
        }
    }
}
