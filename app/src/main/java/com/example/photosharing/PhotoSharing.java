package com.example.photosharing;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.photosharing.models.Image;
import com.example.photosharing.models.ImageType;
import com.example.photosharing.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

public class PhotoSharing extends Application {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private User user;
    private Map<String, User> users = new ConcurrentHashMap<>();

    /**
     * @return get the current user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the current user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Upload image to firebase storage
     * @param bitmap the image bitmap
     * @param path the path to the image
     * @return a task to upload the image
     */
    public UploadTask uploadImage(@Nonnull Bitmap bitmap, @Nonnull String path) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        byte[] data = byteArray.toByteArray();
        StorageReference profileImageRef = storage.getReference().child(path);
        return profileImageRef.putBytes(data);
    }

    /**
     * Upload the image to firebase
     * @param photo the bitmap of the photo
     * @param thumbnail the bitmap of the thumbnail
     * @param description the description of the image
     * @param currentUser the current user
     * @param onSuccessListener the on success listener for the image
     * @param onFailureListener the on failure listener to handle the error
     */
    public void uploadPhoto(@Nonnull final Bitmap photo, @Nonnull final Bitmap thumbnail, String description, User currentUser, final OnSuccessListener<Image> onSuccessListener, final OnFailureListener onFailureListener) {
        final Image image = new Image();
        image.setId(UUID.randomUUID().toString());
        image.setUserId(currentUser.getId());
        image.setThumbnail("images/thumbnail/" + image.getId());
        image.setPhoto("images/photo/" + image.getId());
        image.setDescription(description);
        uploadImage(thumbnail, image.getThumbnail()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadImage(photo, image.getPhoto()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> info = new HashMap<>();
                        info.put("userId", image.getUserId());
                        info.put("thumbnail", image.getThumbnail());
                        info.put("photo", image.getPhoto());
                        info.put("description", image.getDescription());
                        info.put("timestamp", image.toTimeStamp());
                        db
                                .collection("images")
                                .document(image.getId())
                                .set(info)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        onSuccessListener.onSuccess(image);
                                    }
                                })
                                .addOnFailureListener(onFailureListener);
                    }
                }).addOnFailureListener(onFailureListener);
            }
        }).addOnFailureListener(onFailureListener);


    }

    /**
     * Resize image to expected size
     * @param bitmap the image bitmap
     * @param type the type of image
     * @return the resized image
     */
    public Bitmap resizeImage(Bitmap bitmap, ImageType type) {
        if (bitmap == null) return null;
        return Bitmap.createScaledBitmap(bitmap, type.getMaxLength(), type.getMaxLength(), false);
    }

    /**
     * Load image from firebase storage
     * @param path the path to the file
     * @param type the type of the image
     * @return a task to load the bytes of the image
     */
    public Task<byte[]> loadImage(@Nonnull String path, ImageType type) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child(path);
        return reference.getBytes(type.getMaxSize());
    }

    public void getUserFromDB(final String userId, final OnSuccessListener<User> onSuccessListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final User result = new User();
                        result.setId(userId);
                        result.setUsername(documentSnapshot.getString("username"));
                        result.setBio(documentSnapshot.getString("bio"));
                        result.setEmail(documentSnapshot.getString("email"));
                        result.setImage(documentSnapshot.getString("image"));
                        loadImage(result.getImage(), ImageType.PROFILE)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                                        Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                                        result.setImageCache(bmp);
                                        onSuccessListener.onSuccess(result);
                                    }
                                });
                    }
                });
    }

    public void getUser(final String userId, final OnSuccessListener<User> onSuccessListener){
        if (user.getId().equals(userId)) {
            onSuccessListener.onSuccess(user);
        } else if (users.containsKey(userId)) {
            onSuccessListener.onSuccess(users.get(userId));
        } else {
            getUserFromDB(userId, onSuccessListener);
        }
    }
}
