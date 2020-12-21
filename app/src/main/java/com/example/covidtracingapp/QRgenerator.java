package com.example.covidtracingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class QRgenerator extends AppCompatActivity {

    Button  download, btnreturn;
    ImageView QRHolder;

    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);
        btnreturn = (Button)findViewById(R.id.btnreturn);
        QRHolder = findViewById(R.id.QRHolder);
        download = (Button)findViewById(R.id.download);
        textView2 = (TextView)findViewById(R.id.textView2);
        //call register page function
        String myIntent = super.getIntent().getStringExtra("key");
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{//generate QR code
            BitMatrix bitMatrix = multiFormatWriter.encode(myIntent, BarcodeFormat.QR_CODE,500,500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            final Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            QRHolder.setImageBitmap(bitmap);
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "code_scanner"
                            , null);
                    Toast.makeText(QRgenerator.this, "Saved to Gallery", Toast.LENGTH_SHORT)
                            .show();


                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    btnreturn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(QRgenerator.this, login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    });
    }
    public void openlogin(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }


}
