package com.example.groupcommunity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.groupcommunity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splash extends AppCompatActivity {
    private static int SPLASH_SCREEN = 5000;
    private FirebaseAuth mAuth;
    private Animation expandAnim;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        logo = findViewById(R.id.logo);

        expandAnim = AnimationUtils.loadAnimation(this, R.anim.expand);
        logo.setAnimation(expandAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    finish();
                    startActivity(new Intent(splash.this, signup.class));
                } else {
                    Intent intent = new Intent(getApplicationContext(), fingerprint.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_SCREEN);
    }
}