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
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmarkDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionLatLng;

import java.util.List;

public class landmark extends AppCompatActivity {
Button button14,button15;
ImageView imageView5;
TextView textView5;
Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);
        button14=findViewById(R.id.button14);
        button15=findViewById(R.id.button15);
        imageView5=findViewById(R.id.imageView5);
        textView5=findViewById(R.id.textView5);

        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectLandMark();
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
            imageView5.setImageBitmap(imageBitmap);

        }
    }

    private void detectLandMark(){

        FirebaseVisionCloudDetectorOptions options =
                new FirebaseVisionCloudDetectorOptions.Builder()
                        .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                        .setMaxResults(15)
                        .build();
        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionCloudLandmarkDetector firebaseVisionCloudLandmarkDetector= FirebaseVision.getInstance().getVisionCloudLandmarkDetector(options);

        firebaseVisionCloudLandmarkDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionCloudLandmark>>() {
            @Override
            public void onSuccess(List<FirebaseVisionCloudLandmark> firebaseVisionCloudLandmarks) {
                getLandmarkInfo(firebaseVisionCloudLandmarks);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(landmark.this, "error"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getLandmarkInfo(List <FirebaseVisionCloudLandmark> landmarks){
        if(landmarks.size()==0){
            Toast.makeText(this, "no land marks", Toast.LENGTH_SHORT).show();
            textView5.setText("no land mark detected");
        }
        else{
            String text="";
            String landmark="";
            float confident=0;
            double latitude=0,longitude=0;

            for(FirebaseVisionCloudLandmark firebaseVisionCloudLandmark: landmarks){
                landmark=firebaseVisionCloudLandmark.getLandmark();
                confident=firebaseVisionCloudLandmark.getConfidence();

                for(FirebaseVisionLatLng latLng:firebaseVisionCloudLandmark.getLocations()){
                    latitude=latLng.getLatitude();
                    longitude=latLng.getLongitude();
                }
            }
            text=text+"Landmark : "+landmark+"\n";
            text=text+"Confident : "+confident+"\n";
            text=text+"Latitude : "+latitude+"\n";
            text=text+"Longitude : "+longitude+"\n";

            textView5.setText(text);
        }
    }

}
