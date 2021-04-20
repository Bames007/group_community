package com.example.groupcommunity.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groupcommunity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ncorti.slidetoact.SlideToActView;

import frlgrd.animatededittext.AnimatedEditText;

public class login extends AppCompatActivity {

    private AnimatedEditText email, password;
    private String email_login, pass_login;
    private SlideToActView login;
    private View mParentLayout;
    private FirebaseAuth mAuth;
    private TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();

        mParentLayout = findViewById(R.id.login_content);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.create_link);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });

        login.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                email_login = email.getEditText().getText().toString().trim();
                pass_login = password.getEditText().getText().toString().trim();

                if (email_login.isEmpty()) {
                    errorSnackBarMessages("Email is empty");
                    email.requestFocus();
                    login.resetSlider();
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email_login).matches()) {
                    errorSnackBarMessages("Email format is invalid");
                    email.requestFocus();
                    login.resetSlider();
                }
                if (pass_login.isEmpty()) {
                    errorSnackBarMessages("Password is empty");
                    password.requestFocus();
                    login.resetSlider();
                }
                if (pass_login.length() < 6) {
                    errorSnackBarMessages("Password Length should be more than 6 characters");
                    email.requestFocus();
                    login.resetSlider();
                }

                if (email_login.isEmpty() && pass_login.isEmpty()) {
                    email.requestFocus();
                    login.resetSlider();
                    errorSnackBarMessages("Empty Fields");
                    return;
                }
                if (!email_login.equals("") && !pass_login.equals("")) {
                    userLogin();
                }
            }
        });

    }

    private void userLogin() {

        mAuth.signInWithEmailAndPassword(email_login, pass_login)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(login.this, home.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else {
                            errorSnackBarMessages(task.getException().getMessage());
                            login.resetSlider();
                        }

                    }
                });

    }

    private void errorSnackBarMessages(String message) {
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setBackgroundTint(Color.rgb(199, 84, 80))
                .setActionTextColor(Color.rgb(43, 43, 43))
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            finish();
            startActivity(new Intent(login.this, home.class));
        }
    }
}