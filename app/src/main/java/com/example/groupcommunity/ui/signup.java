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
import com.example.groupcommunity.models.user_profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ncorti.slidetoact.SlideToActView;

import frlgrd.animatededittext.AnimatedEditText;

public class signup extends AppCompatActivity {

    private AnimatedEditText email, password, fname, lname, phone;
    private String email_signup, pass_signup, fname_signup, lname_signup, phone_signup;
    private SlideToActView signup;
    private View mParentLayout;
    private FirebaseAuth mAuth;
    private TextView login;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        reference = FirebaseDatabase.getInstance().getReference("user_profile");
        mAuth = FirebaseAuth.getInstance();

        mParentLayout = findViewById(R.id.signup_context);

        email = findViewById(R.id.email_signup);
        password = findViewById(R.id.password_signup);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        phone = findViewById(R.id.phone_signup);
        login = findViewById(R.id.login_link);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, login.class);
                startActivity(intent);
            }
        });

        signup = findViewById(R.id.create);
        signup.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                email_signup = email.getEditText().getText().toString().trim();
                pass_signup = password.getEditText().getText().toString().trim();
                fname_signup = fname.getEditText().getText().toString().trim();
                lname_signup = lname.getEditText().getText().toString().trim();
                phone_signup = phone.getEditText().getText().toString().trim();

                if (fname_signup.isEmpty()) {
                    errorSnackBarMessages("First name is empty");
                    fname.requestFocus();
                    signup.resetSlider();
                }

                if (!fname_signup.matches("[A-Za-z]*")) {
                    errorSnackBarMessages("First name is invalid");
                    fname.requestFocus();
                    signup.resetSlider();
                }
                if (lname_signup.isEmpty()) {
                    errorSnackBarMessages("Last name is empty");
                    lname.requestFocus();
                    signup.resetSlider();
                }
                if (!lname_signup.matches("[A-Za-z]*")) {
                    errorSnackBarMessages("Last name is invalid");
                    lname.requestFocus();
                    signup.resetSlider();
                }
                if (email_signup.isEmpty()) {
                    errorSnackBarMessages("Email is empty");
                    email.requestFocus();
                    signup.resetSlider();
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email_signup).matches()) {
                    errorSnackBarMessages("Email format is invalid");
                    email.requestFocus();
                    signup.resetSlider();
                }
                if (phone_signup.isEmpty()) {
                    errorSnackBarMessages("Phone is empty");
                    phone.requestFocus();
                    signup.resetSlider();
                }
                if (pass_signup.isEmpty()) {
                    errorSnackBarMessages("Password is empty");
                    password.requestFocus();
                    signup.resetSlider();
                }
                if (pass_signup.length() < 6) {
                    errorSnackBarMessages("Password Length should be more than 6 characters");
                    password.requestFocus();
                    signup.resetSlider();
                }
                if (fname_signup.isEmpty() && lname_signup.isEmpty() && email_signup.isEmpty()
                        && pass_signup.isEmpty() && phone_signup.isEmpty()) {
                    errorSnackBarMessages("Fields are empty");
                    signup.resetSlider();
                }
                if (!fname_signup.equals("") && !lname_signup.equals("") && !email_signup.equals("")
                        && !pass_signup.equals("") && !phone_signup.equals("")) {
                    registerUser(email_signup, pass_signup);
                }

            }
        });

    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email_signup, pass_signup)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            makeSnackBarMessages("Account has been created!");

                            String id = mAuth.getUid();

                            user_profile profile = new user_profile(fname_signup, lname_signup, phone_signup, email_signup);

                            reference.child(id).setValue(profile);


                            Intent intent = new Intent(signup.this, home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            finish();
                            startActivity(intent);
                        } else {
                            errorSnackBarMessages(task.getException().getMessage());
                            signup.resetSlider();
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

    private void makeSnackBarMessages(String message) {
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setBackgroundTint(Color.rgb(73, 156, 84))
                .setActionTextColor(Color.rgb(43, 43, 43))
                .show();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            finish();
            startActivity(new Intent(signup.this, home.class));
        }
    }
}