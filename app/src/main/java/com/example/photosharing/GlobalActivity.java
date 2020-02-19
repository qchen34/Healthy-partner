package com.example.photosharing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photosharing.models.Image;
import com.example.photosharing.models.ImageType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GlobalActivity extends AppCompatActivity {

    static final String NAME = "Global";

    private FirebaseFirestore db;
    private RecyclerView imagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        imagesList = findViewById(R.id.global_images);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        imagesList.setLayoutManager(layoutManager);

        populateImages();
    }

    /**
     * Navigate to profile view
     * @param view the current view
     */
    public void goBackToProfile(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void refreshList(View view) {
        populateImages();
    }

    private void populateImages() {
        db
                .collection("images")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Image> images = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Image image = new Image();
                            image.setId(snapshot.getId());
                            image.setThumbnail(snapshot.getString("thumbnail"));
                            image.setPhoto(snapshot.getString("photo"));
                            image.setUserId(snapshot.getString("userId"));
                            Long timestamp = snapshot.getLong("timestamp");
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
                        imagesList.setAdapter(new ImagesAdapter(images));
                    }
                });
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.fullImageView);
        }

        private void setImageView(final Image image) {
            imageView.setImageBitmap(image.getThumbnailCache());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GlobalActivity.this, CommentActivity.class);
                    intent.putExtra(CommentActivity.IMAGE_OBJECT, image.copyImage());
                    intent.putExtra(CommentActivity.PREVIOUS, NAME);
                    startActivity(intent);
                }
            });
        }
    }

    private class ImagesAdapter extends RecyclerView.Adapter<ImageViewHolder> {

        private List<Image> images;

        private ImagesAdapter(List<Image> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(GlobalActivity.this)
                    .inflate(R.layout.layout_image, parent, false);
            return new ImageViewHolder(layoutView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
            final Image image = images.get(position);
            if (image.getThumbnailCache() != null) {
                holder.setImageView(image);
            } else {
                ((PhotoSharing) getApplication()).loadImage(image.getThumbnail(), ImageType.THUMBNAIL).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                        Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                        image.setThumbnailCache(bmp);
                        holder.setImageView(image);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return images.size();
        }
    }
}
