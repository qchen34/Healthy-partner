package com.example.photosharing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.photosharing.models.Image;
import com.example.photosharing.models.ImageType;
import com.example.photosharing.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.util.List;
import java.util.Objects;

public class CaptionActivity extends AppCompatActivity {

    static final String IMAGE = "CAPTION_IMAGE";

    private ImageView captionImage;
    private TextView descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get views
        captionImage = findViewById(R.id.captionImage);
        descriptionText = findViewById(R.id.description);

        // set switch button
        Switch switchButton = findViewById(R.id.switch1);
        switchButton.setChecked(false);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) analyzeImage();
            }
        });

        // read image
        Bitmap image = (Bitmap) Objects.requireNonNull(getIntent().getExtras()).get(IMAGE);
        captionImage.setImageBitmap(image);
    }

    private Bitmap getImageBitMap() {
        BitmapDrawable drawable = (BitmapDrawable) captionImage.getDrawable();
        return drawable.getBitmap();
    }

    private void analyzeImage() {
        Bitmap bitmap = getImageBitMap();
        final FirebaseVisionImage firebaseImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionImageLabeler mlKit = FirebaseVision.getInstance().getOnDeviceImageLabeler();
        mlKit.processImage(firebaseImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                for (FirebaseVisionImageLabel firebaseLabel : firebaseVisionImageLabels) {
                    StringBuilder description = new StringBuilder(descriptionText.getText().toString());
                    if (firebaseLabel.getConfidence() > 0.7) {
                        String label = firebaseLabel.getText();
                        description.append('#').append(label);
                    }
                    descriptionText.setText(description);
                }
            }
        });
    }

    public void uploadImage(View view) {
        Bitmap imageBitmap  = getImageBitMap();
        User user = ((PhotoSharing) getApplication()).getUser();
        Bitmap photoBitmap = ((PhotoSharing) getApplication()).resizeImage(imageBitmap, ImageType.PHOTO);
        Bitmap thumbnail = ((PhotoSharing) getApplication()).resizeImage(photoBitmap, ImageType.THUMBNAIL);
        String description = descriptionText.getText().toString();
        ((PhotoSharing) getApplication()).uploadPhoto(photoBitmap, thumbnail, description, user, new OnSuccessListener<Image>() {
            @Override
            public void onSuccess(Image image) {
                startActivity(new Intent(CaptionActivity.this, ProfileActivity.class));
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CaptionActivity.this,
                        getResources().getString(R.string.error_save_image_failed),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cancel(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }

}
