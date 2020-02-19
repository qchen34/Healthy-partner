package com.example.photosharing;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Strings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailField;
    private EditText passwordField;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        // jump to profile if current user is not null
        updateUI(auth.getCurrentUser());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        // Input fields
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);

        // Add listener for buttons
        findViewById(R.id.sign_up_btn).setOnClickListener(this);
        findViewById(R.id.login_btn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_up_btn) {
            startActivity(new Intent(this, RegisterActivity.class));
        } else if (v.getId() == R.id.login_btn) {
            // validate fields
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            if (Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(password)) {
                Toast.makeText(LoginActivity.this,
                        getResources().getString(R.string.error_required_fields),
                        Toast.LENGTH_LONG).show();
                return;
            }
            signIn(email, password);
        }

    }

    private void signIn(String email, String password) {
        disableAllFields();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            enableAllFields();
                            FirebaseUser currentUser = auth.getCurrentUser();
                            updateUI(currentUser);
                        } else {
                            enableAllFields();
                            Toast.makeText(LoginActivity.this,
                                    R.string.error_authentication_failed,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(final FirebaseUser currentUser) {
        if (currentUser != null) {
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }


    private void disableAllFields() {
        ConstraintLayout layout = findViewById(R.id.login_page);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(false);
        }
    }

    private void enableAllFields() {
        ConstraintLayout layout = findViewById(R.id.login_page);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(true);

        }
    }
}
