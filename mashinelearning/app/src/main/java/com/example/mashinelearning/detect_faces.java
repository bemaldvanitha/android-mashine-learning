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
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class detect_faces extends AppCompatActivity {

Button button7,button10;
ImageView imageView3;
TextView textView3;
private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_faces);
        button7=findViewById(R.id.button7);
        button10=findViewById(R.id.button10);
        imageView3=findViewById(R.id.imageView3);
        textView3=findViewById(R.id.textView3);

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectFaces(imageBitmap);
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
            imageView3.setImageBitmap(imageBitmap);

        }
    }

    private void detectFaces(Bitmap imageBitmap){
        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(imageBitmap);

        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();



        FirebaseVisionFaceDetector firebaseVisionFaceDetector= FirebaseVision.getInstance().getVisionFaceDetector(options);
        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                getInfroFaces(firebaseVisionFaces);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(detect_faces.this, "fail ,,,, "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getInfroFaces(List<FirebaseVisionFace> firebaseVisionFaces){
        String aa="";
        float smile=0,lefteye=0,righteye=0,angley=0,anglez=0;
        int id=0;
        if(firebaseVisionFaces.size()!=0) {
            for (FirebaseVisionFace face : firebaseVisionFaces) {
                if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                    smile = face.getSmilingProbability();
                }
                if (face.getLeftEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                    lefteye = face.getLeftEyeOpenProbability();
                }
                if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                    righteye = face.getRightEyeOpenProbability();
                }
                angley = face.getHeadEulerAngleY();
                anglez=face.getHeadEulerAngleZ();
                id=face.getTrackingId();

            }
            aa=aa+"Id "+Integer.toString(id);
            aa=aa+"\nAngle Y "+Float.toString(angley);
            aa=aa+"\nAngle Z "+Float.toString(anglez);

            if(smile>0.5){
                aa=aa+"\nSmile "+"YES\n";
            }
            else{
                aa=aa+"\nSmile "+"NO\n";
            }

            if(lefteye>0.5){
                aa=aa+"left_eye "+"OPEN\n";
            }
            else{
                aa=aa+"left_eye "+"Close\n";
            }

            if(righteye>0.5){
                aa=aa+"right_eye "+"Open\n";
            }
            else{
                aa=aa+"right_eye "+"Close\n";
            }
            Toast.makeText(this, ""+Float.toString(smile)+",,,"+Float.toString(lefteye)+",,,"+Float.toString(righteye), Toast.LENGTH_SHORT).show();
            textView3.setText(aa);
        }
        else{
            textView3.setText("no face detected");
            Toast.makeText(this, "no face", Toast.LENGTH_SHORT).show();
        }
    }
}
