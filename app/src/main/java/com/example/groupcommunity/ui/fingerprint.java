package com.example.groupcommunity.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groupcommunity.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class fingerprint extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private View mParentLayout;
    private TextView name;
    private Animation expandAnim;
    private ImageView finger_print_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        expandAnim = AnimationUtils.loadAnimation(this, R.anim.expand);

        finger_print_logo = findViewById(R.id.finger_logo);
        finger_print_logo.setAnimation(expandAnim);
        finger_print_logo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {
                finger_print();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onStart() {
        mParentLayout = findViewById(R.id.fingerprint_bg);
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            finish();
            startActivity(new Intent(fingerprint.this, signup.class));
        } else {
            getUserName();
            finger_print();
        }

    }

    private void makeSnackBarMessages(String message) {
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setBackgroundTint(Color.rgb(73, 156, 84))
                .setActionTextColor(Color.rgb(43, 43, 43))
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    protected void finger_print() {
        Executor executor = Executors.newSingleThreadExecutor();
        BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("Fingerprint Authentication")
                .setNegativeButton("Cancel", executor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                }).build();
        final fingerprint fingerprint = this;

        biometricPrompt.authenticate(new CancellationSignal(), executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                fingerprint.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(fingerprint.this, home.class));
                        finish();
                    }
                });
            }
        });
    }

    protected void getUserName() {
        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.welcome_name);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        assert currentUser != null;
        DatabaseReference reference = database.getReference("user_profile").child(currentUser.getUid()).child("fname");
        //to get the value of a single field
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fname = snapshot.getValue(String.class);
                name.setText(" " + fname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}