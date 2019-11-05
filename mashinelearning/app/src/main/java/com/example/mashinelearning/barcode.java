package com.example.mashinelearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.util.List;

public class barcode extends AppCompatActivity {

ImageView imageView2;
TextView textView2;
    private Bitmap imageBitmap;
Button button8,button9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        imageView2=findViewById(R.id.imageView2);
        textView2=findViewById(R.id.textView2);
        button8=findViewById(R.id.button8);
        button9=findViewById(R.id.button9);

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectFromCode();
            }
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView2.setImageBitmap(imageBitmap);

        }

    }

    private void detectFromCode(){
        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(imageBitmap);

        /*FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_QR_CODE,
                                FirebaseVisionBarcode.FORMAT_AZTEC)
                        .build();*/

        FirebaseVisionBarcodeDetector firebaseVisionBarcodeDetector= FirebaseVision.getInstance().getVisionBarcodeDetector();

        firebaseVisionBarcodeDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                getInfromationBarcode(firebaseVisionBarcodes);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(barcode.this, "fail,,,, "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getInfromationBarcode(List<FirebaseVisionBarcode> barcodes){
        String text="";
        for(FirebaseVisionBarcode barcode:barcodes){
            text=text+barcode.getRawValue();

        }
        if(text.isEmpty() || barcodes.size()==0){
            Toast.makeText(this, "no barcode", Toast.LENGTH_SHORT).show();
        }
        textView2.setText(text);
    }
}
