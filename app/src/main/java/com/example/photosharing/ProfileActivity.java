package com.example.photosharing;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.photosharing.models.Image;
import com.example.photosharing.models.ImageType;
import com.example.photosharing.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.photosharing.PhotoSharing.REQUEST_IMAGE_CAPTURE;

public class ProfileActivity extends AppCompatActivity {

    static final String NAME = "profile";

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ImageView profileImage;
    private List<Image> images = new ArrayList<>();
    private FirebaseFirestore db;
    private GridView gridView;
    private FloatingActionButton button;
    private TextView username;
    private TextView bio;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        db = FirebaseFirestore.getInstance();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_profile);

        // get views
        gridView = findViewById(R.id.profile_images);
        button = findViewById(R.id.add_image_button);
        username = findViewById(R.id.profile_username);
        bio = findViewById(R.id.profile_bios);
        profileImage = findViewById(R.id.profile_image);

        // read images
        readUser(FirebaseAuth.getInstance().getCurrentUser());
        readImages();
    }

    private void readUser(FirebaseUser firebaseUser) {
        if (((PhotoSharing) getApplication()).getUser() != null) {
            User user = ((PhotoSharing) getApplication()).getUser();
            username.setText(user.getUsername());
            bio.setText(user.getBio());
            profileImage.setImageBitmap(user.getImageCache());
        }
        ((PhotoSharing) getApplication()).getUserFromDB(firebaseUser.getUid(), new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                ((PhotoSharing) getApplication()).setUser(user);
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                profileImage.setImageBitmap(user.getImageCache());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            auth.signOut();
            ((PhotoSharing) this.getApplication()).setUser(null);
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        return false;
    }

    /**
     * Dispatch the picture intent
     * @param view the view
     */
    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Navigate to global view
     * @param view the current view
     */
    public void navigateToGlobal(View view) {
        startActivity(new Intent(this, GlobalActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK
                && data.getExtras() != null) {
            Bundle extras = data.getExtras();
            final Bitmap imageBitmap = (Bitmap) extras.get("data");
            Intent intent = new Intent(this, CaptionActivity.class);
            intent.putExtra(CaptionActivity.IMAGE, imageBitmap);
            startActivity(intent);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this,
                    getResources().getString(R.string.error_cancelled),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void readImages() {
        button.setClickable(false);
        db
                .collection("images")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        images.clear();
                        for (QueryDocumentSnapshot document: queryDocumentSnapshots) {
                            Image image = new Image();
                            image.setId(document.getId());
                            image.setThumbnail(document.getString("thumbnail"));
                            image.setPhoto(document.getString("photo"));
                            image.setUserId(document.getString("userId"));
                            image.setDescription(document.getString("description"));
                            Long timestamp = document.getLong("timestamp");
                            if (timestamp != null) {
                                image.fromTimeStamp(timestamp);
                            }
                            images.add(image);
                        }
                        Collections.sort(images, new Comparator<Image>() {
                            @Override
                            public int compare(Image o1, Image o2) {
                                return (int) (o2.toTimeStamp() - o1.toTimeStamp());
                            }
                        });
                        button.setClickable(true);
                        updateImages();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        button.setClickable(true);
                    }
                });
    }

    private void updateImages() {
        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return images.size();
            }

            @Override
            public Object getItem(int position) {
                return images.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ImageView thumbnailView = new ImageView(ProfileActivity.this);
                final Image image = images.get(position);
                thumbnailView.setAdjustViewBounds(true);
                thumbnailView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ProfileActivity.this, CommentActivity.class);
                        intent.putExtra(CommentActivity.IMAGE_OBJECT, image.copyImage());
                        intent.putExtra(CommentActivity.PREVIOUS, NAME);
                        startActivity(intent);
                    }
                });
                ((PhotoSharing) getApplication()).loadImage(image.getThumbnail(), ImageType.THUMBNAIL)
                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                                bmp = ((PhotoSharing) getApplication()).resizeImage(bmp, ImageType.PHOTO);
                                image.setThumbnailCache(bmp);
                                thumbnailView.setImageBitmap(bmp);
                            }
                        });
                return thumbnailView;
            }
        });
        button.setClickable(true);
    }
}
