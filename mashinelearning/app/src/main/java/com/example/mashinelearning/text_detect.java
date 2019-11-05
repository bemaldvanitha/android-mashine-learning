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
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
//import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;


import java.util.List;

public class text_detect extends AppCompatActivity {
    ImageView imageView;
    Button button5,button6;
    TextView textView;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_detect);
        imageView=findViewById(R.id.imageView);
        textView=findViewById(R.id.textView);
        button5=findViewById(R.id.button5);
        button6=findViewById(R.id.button6);

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                detectTextFromImage();
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
            imageView.setImageBitmap(imageBitmap);

        }

    }

    private void detectTextFromImage(){
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText texts) {
                processTextRecognitionResult(texts);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(text_detect.this, "error ,,, "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void processTextRecognitionResult(FirebaseVisionText firebaseVisionText){
        if(firebaseVisionText.getTextBlocks().size()==0){
            textView.setText("no text");
            Toast.makeText(this, "no text", Toast.LENGTH_SHORT).show();
        }
        for(FirebaseVisionText.TextBlock block :firebaseVisionText.getTextBlocks()){
            textView.setText(block.getText());
        }

        //In case you want to extract each line
			/*
			for (FirebaseVisionText.Line line: block.getLines()) {
				for (FirebaseVisionText.Element element: line.getElements()) {
					mTextView.append(element.getText() + " ");
				}
			}
			*/
    }
}
