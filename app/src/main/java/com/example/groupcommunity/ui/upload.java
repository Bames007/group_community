package com.example.groupcommunity.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groupcommunity.R;
import com.example.groupcommunity.models.report_holder;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ncorti.slidetoact.SlideToActView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class upload extends AppCompatActivity {

    private Uri videoUpload;
    private TextView address, title, description, description2;
    private String videoUrl, full_name, username;
    private String client_address, title_text, description_text;
    private View mParentLayout;
    private SlideToActView upload;
    private static int SPLASH_SCREEN = 5000;
    private StorageReference storageReference;
    private DatabaseReference reference;
    private ImageView share;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

//
//    protected String getUserName() {
//        mAuth = FirebaseAuth.getInstance();
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//
//        return full_name;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mParentLayout = findViewById(R.id.content);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert currentUser != null;
        DatabaseReference reference = database.getReference("user_profile").child(currentUser.getUid()).child("fname");
        DatabaseReference reference2 = database.getReference("user_profile").child(currentUser.getUid()).child("lname");
        //to get the value of a single field
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                full_name = "" + snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                full_name = full_name + " " + snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        description2 = findViewById(R.id.description2);
        upload = findViewById(R.id.upload);
        share = findViewById(R.id.share_report);


        VideoView live_video = findViewById(R.id.live_video);
        address = findViewById(R.id.address);
        title = findViewById(R.id.title_upload);
        description = findViewById(R.id.description_upload);

        try {
            MediaController mediaController = new MediaController(this);

            Uri videoUri = Uri.parse(getIntent().getExtras().getString("videoUri"));
            videoUpload = videoUri;
            live_video.setVideoURI(videoUri);
            live_video.setMediaController(mediaController);
            mediaController.setAnchorView(live_video);
            live_video.start();
            live_video.getDuration();
            live_video.canPause();
            live_video.canSeekBackward();
            live_video.canSeekForward();

            client_address = getIntent().getExtras().getString("clientAddress");
            address.setText(client_address);

            title_text = getIntent().getExtras().getString("title");
            title.setText(title_text);

            description_text = getIntent().getExtras().getString("descriptionText");
            description.setText(description_text);

        } catch (NullPointerException e) {
            errorSnackBarMessages(e.getMessage());
            upload.resetSlider();
        }
        upload.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                setUpload();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exec();
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

    private boolean exec() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/mp4");
        String subject = "Title: " + title_text;
        String title = "Hey! An " + client_address;
        String sharebody = "Description: " + description_text;
        String fulltext = "*" + title_text + "*" + "(" + client_address + ") \n" + description_text;
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoUpload.toString()));
        intent.putExtra(Intent.EXTRA_REFERRER_NAME, "Eddy");
        intent.putExtra(Intent.EXTRA_TEXT, fulltext);
        startActivity(Intent.createChooser(intent, "Community Watch"));

        makeSnackBarMessages("Thank you for your feedback. ");
        return true;
    }

    protected void setUpload() {

        storageReference = FirebaseStorage.getInstance().getReference().child("report_videos");

        String user = FirebaseAuth.getInstance().getUid();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd, yyyy");
        final String date = dateformat.format(calendar.getTime());

        SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
        final String time = timeformat.format(calendar.getTime());

        final String upload_id = user + "" + time;


        final StorageReference storageRef = storageReference.child(videoUpload.getLastPathSegment() + upload_id + ".mp4");

        final UploadTask uploadTask = storageRef.putFile(videoUpload);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorSnackBarMessages(e.toString());
                upload.resetSlider();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                makeSnackBarMessages("Video uploaded Successfully");
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        videoUrl = storageRef.getDownloadUrl().toString();
                        return storageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        videoUrl = task.getResult().toString();

                        report_holder reportHolder = new report_holder(full_name, title_text, client_address, videoUrl, description_text, date, time);
                        reference = FirebaseDatabase.getInstance().getReference("reports");
                        reference.child(upload_id).setValue(reportHolder).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    makeSnackBarMessages("Report has been uploaded");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent2 = new Intent(upload.this, home.class);
                                            startActivity(intent2);
                                            finish();
                                        }
                                    }, SPLASH_SCREEN);
                                } else {
                                    errorSnackBarMessages(task.getException().toString());
                                    upload.resetSlider();
                                }
                            }
                        });

                    }
                });
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(upload.this, home.class));
    }
}