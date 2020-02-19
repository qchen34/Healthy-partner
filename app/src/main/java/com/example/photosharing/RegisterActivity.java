package com.example.photosharing;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.photosharing.models.ImageType;
import com.example.photosharing.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Strings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.photosharing.PhotoSharing.REQUEST_IMAGE_CAPTURE;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView profileImage;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmField;
    private EditText usernameField;
    private EditText bioField;
    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_register);

        // Input fields
        emailField = findViewById(R.id.register_email);
        passwordField = findViewById(R.id.register_psw);
        confirmField = findViewById(R.id.confirm_psw);
        usernameField = findViewById(R.id.username);
        bioField = findViewById(R.id.bio);
        profileImage = findViewById(R.id.profile_image);
        auth = FirebaseAuth.getInstance();

        // Add listener for buttons
        findViewById(R.id.register_sign_up).setOnClickListener(this);
    }

    private void disableAllFields() {
        ConstraintLayout layout = findViewById(R.id.register_page);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(false);
        }
    }

    private void enableAllFields() {
        ConstraintLayout layout = findViewById(R.id.register_page);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(true);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK
                && data.getExtras() != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = ((PhotoSharing) getApplication()).resizeImage(imageBitmap, ImageType.PROFILE);
            profileImage.setImageBitmap(imageBitmap);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this,
                    getResources().getString(R.string.error_cancelled),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register_sign_up) {
            disableAllFields();
            User user = new User();
            user.setEmail(emailField.getText().toString());
            user.setUsername(usernameField.getText().toString());
            user.setBio(bioField.getText().toString());
            String password = passwordField.getText().toString();
            String confirm = confirmField.getText().toString();
            if (Strings.isNullOrEmpty(user.getEmail()) || Strings.isNullOrEmpty(user.getUsername())
                    || Strings.isNullOrEmpty(password)
                    || Strings.isNullOrEmpty(confirm)) {
                registerFailed(getResources().getString(R.string.error_required_fields));
                return;
            }
            if (!Objects.equals(password, confirm)) {
                registerFailed(getResources().getString(R.string.error_different_passwords));
                return;
            }
            createUser(user, password);
        }
    }

    private void createUser(final User user, String password){
        auth
                .createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && auth.getCurrentUser() != null) {
                            FirebaseUser currentUser = auth.getCurrentUser();
                            user.setId(currentUser.getUid());
                            saveImage(user);
                        } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                            registerFailed(getResources().getString(R.string.error_week_password));
                        } else {
                            registerFailed(getResources().getString(R.string.error_register_failed));
                        }
                    }
                });
    }

    private void saveUser(final User user) {
        // Create a new user with all the info
        Map<String, Object> info = new HashMap<>();
        info.put("email", user.getEmail());
        info.put("username", user.getUsername());
        info.put("bio", user.getBio());
        if (user.getImage() != null) info.put("image", user.getImage());

        // Add a new document with a generated ID
        db
                .collection("users")
                .document(user.getId())
                .set(info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        registerComplete(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        registerFailed(getResources().getString(R.string.error_register_failed));
                    }
                });
    }

    private void saveImage(final User user) {
        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        if (bitmap != null) {
            final String path = "images/profile/" + user.getId();
            ((PhotoSharing) getApplication()).uploadImage(bitmap, path).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    registerFailed(getResources().getString(R.string.error_save_profile_image_failed));
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    user.setImage(path);
                    saveUser(user);
                }
            });
        } else {
            saveUser(user);
        }
    }

    private void registerComplete(User user) {
        enableAllFields();
        if (user != null) {
            ((PhotoSharing) this.getApplication()).setUser(user);
            startActivity(new Intent(this, ProfileActivity.class));
        }

    }

    private void registerFailed(String message) {
        enableAllFields();
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
