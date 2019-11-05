package com.example.mashinelearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;

import java.util.List;

public class image_labeling extends AppCompatActivity {

Button button11,button12;
ImageView imageView4;
TextView textView4;
Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_labeling);
        button11=findViewById(R.id.button11);
        button12=findViewById(R.id.button12);
        imageView4=findViewById(R.id.imageView4);
        textView4=findViewById(R.id.textView4);

        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectImageLable(imageBitmap);
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
            imageView4.setImageBitmap(imageBitmap);

        }
    }

    private void detectImageLable(Bitmap imageBitmap){
        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(imageBitmap);
        
        FirebaseVisionOnDeviceImageLabelerOptions options = new FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build();

        FirebaseVisionImageLabeler firebaseVisionImageLabeler= FirebaseVision.getInstance().getOnDeviceImageLabeler(options);

        firebaseVisionImageLabeler.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                extrctlables(firebaseVisionImageLabels);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(image_labeling.this, "error ,,, "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void extrctlables(List <FirebaseVisionImageLabel> firebaseVisionImageLabels){
        String text="";
        if(firebaseVisionImageLabels.size()==0){
            Toast.makeText(this, "no images to lable", Toast.LENGTH_SHORT).show();
            textView4.setText("no images to lable");
        }
        else{
            for(FirebaseVisionImageLabel label:firebaseVisionImageLabels){
                text=text+"item : "+label.getText()+"\n";
                text=text+"confident level: "+label.getConfidence()+"\n";

            }
            textView4.setText(text);
            Toast.makeText(this, "completed", Toast.LENGTH_SHORT).show();
        }
    }
}
