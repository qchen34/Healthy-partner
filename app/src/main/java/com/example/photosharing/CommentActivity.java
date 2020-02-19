package com.example.photosharing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photosharing.models.Comment;
import com.example.photosharing.models.Image;
import com.example.photosharing.models.ImageType;
import com.example.photosharing.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CommentActivity extends AppCompatActivity {

    static final String IMAGE_OBJECT = "IMAGE_OBJECT";
    static final String PREVIOUS = "PREVIOUS";

    private EditText commentInput;
    private Image image;
    private ImageView imageView;
    private FirebaseFirestore db;
    private TextView imageDescription;
    private User user;
    private RecyclerView commentList;
    private List<Comment> comments = new ArrayList<>();
    private TextView authorView;
    private String previous;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_comment, menu);
        if (user.getId().equals(image.getUserId())) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(true);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        image = (Image) Objects.requireNonNull(getIntent().getExtras()).get(IMAGE_OBJECT);
        previous = (String) getIntent().getExtras().get(PREVIOUS);
        db = FirebaseFirestore.getInstance();

        // views
        commentInput = findViewById(R.id.comment_input);
        imageView = findViewById(R.id.fullImage);
        imageDescription = findViewById(R.id.image_description);
        commentList = findViewById(R.id.comments);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        commentList.setLayoutManager(layoutManager);
        commentInput.onEditorAction(EditorInfo.IME_ACTION_DONE); // close keyboard
        authorView = findViewById(R.id.author);

        // post functions
        user = ((PhotoSharing) getApplication()).getUser();
        readImage();
        readComments();
        populateComments();
        readAuthor();
    }

    private void readAuthor() {
        ((PhotoSharing) getApplication()).getUser(image.getUserId(), new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                String author = getResources().getString(R.string.captured_by, user.getUsername());
                authorView.setText(author);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db
                    .collection("images")
                    .document(image.getId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            String imagePath = "images/photo/" + image.getId();
                            String thumbnailPath = "images/thumbnail/" + image.getId();
                            StorageReference imageRef = storage.getReference().child(imagePath);
                            imageRef.delete();
                            StorageReference thumbnailRef = storage.getReference().child(thumbnailPath);
                            thumbnailRef.delete();
                            if (ProfileActivity.NAME.equals(previous)) {
                                startActivity(new Intent(CommentActivity.this, ProfileActivity.class));
                            } else if (GlobalActivity.NAME.equals(previous)) {
                                startActivity(new Intent(CommentActivity.this, GlobalActivity.class));
                            } else {
                                finish();
                            }
                        }
                    });

            return true;
        }
        return false;
    }


    private void readImage() {
        ((PhotoSharing) getApplication()).loadImage(image.getPhoto(), ImageType.PHOTO)
        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bmp);
                imageDescription.setText(image.getDescription());
            }
        });
    }

    private void readComments() {
        db
                .collection("comments")
                .whereEqualTo("imageId", image.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        comments.clear();
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Comment comment = new Comment();
                            comment.setId(snapshot.getId());
                            comment.setUserId(snapshot.getString("userId"));
                            comment.setImageId(snapshot.getString("imageId"));
                            comment.setContent(snapshot.getString("content"));
                            Long timestamp = snapshot.getLong("timestamp");
                            if (timestamp != null) comment.fromTimeStamp(timestamp);
                            comments.add(comment);
                            populateComments();
                        }
                    }
                });
    }

    private void populateComments() {
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return (int) (o2.toTimeStamp() - o1.toTimeStamp());
            }
        });
        commentList.setAdapter(new CommentsAdapter());
    }

    public void sendComment(View view) {
        String commentText = commentInput.getText().toString();
        commentInput.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (commentText.isEmpty()) {
            Toast.makeText(this,
                    getResources().getString(R.string.error_no_comment),
                    Toast.LENGTH_LONG).show();
            return;
        }
        final Comment comment = new Comment();
        comment.setId(UUID.randomUUID().toString());
        comment.setContent(commentText);
        comment.setImageId(image.getId());
        comment.setUserId(user.getId());

        Map<String, Object> info = new HashMap<>();
        info.put("userId", comment.getUserId());
        info.put("content", comment.getContent());
        info.put("imageId", comment.getImageId());
        db
                .collection("comments")
                .document(comment.getId())
                .set(info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        comments.add(comment);
                        populateComments();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CommentActivity.this,
                                getResources().getString(R.string.error_save_comment_failed),
                                Toast.LENGTH_LONG).show();
                    }
                });

    }

    private class CommentViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatarView;
        private TextView userView;
        private TextView contentView;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarView = itemView.findViewById(R.id.comment_avatar);
            userView = itemView.findViewById(R.id.comment_user);
            contentView = itemView.findViewById(R.id.comment_content);
        }

        private void setAvatarView(Bitmap bitmap) {
            avatarView.setImageBitmap(bitmap);
        }

        private void setUserView(String username) {
            userView.setText(username);
        }

        private void setContentView(String description) {
            contentView.setText(description);
        }
    }

    private class CommentsAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(CommentActivity.this)
                    .inflate(R.layout.layout_comment, parent, false);
            return new CommentViewHolder(layoutView);
        }

        @Override
        public void onBindViewHolder(@NonNull final CommentViewHolder holder, int position) {
            final Comment comment = comments.get(position);
            ((PhotoSharing) getApplication()).getUser(comment.getUserId(), new OnSuccessListener<User>() {
                @Override
                public void onSuccess(User user) {
                    holder.setAvatarView(user.getImageCache());
                    holder.setUserView(user.getUsername());
                    holder.setContentView(comment.getContent());
                }
            });
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }
    }
}
