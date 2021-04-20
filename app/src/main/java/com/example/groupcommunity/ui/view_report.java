package com.example.groupcommunity.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcommunity.R;
import com.example.groupcommunity.models.report_holder;
import com.example.groupcommunity.util.Broadcast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class view_report extends Fragment {
    private ImageView settings;
    private TextView name;
    private RecyclerView newlyaddedRecycler;
    private DatabaseReference databaseReference, userRef;
    private View mParentLayout;
    private FirebaseAuth mAuth;
    private String full_name;
    Broadcast broadcast = new Broadcast();
    private FragmentActivity context;

    public view_report() {
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        context = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    protected String getUserName() {
        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

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
                name.setText(full_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return full_name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_report, container, false);
        name = v.findViewById(R.id.user_full_name);
        name.setText(getUserName());

        settings = v.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(v.getContext(), com.example.groupcommunity.ui.splash.class));
            }
        });

        mParentLayout = v.findViewById(R.id.view_report_content);

        //for newly added items
        newlyaddedRecycler = v.findViewById(R.id.newly_addedRecyclerView);
        newlyaddedRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("reports");
        userRef = databaseReference.child("reports");
        databaseReference.keepSynced(true);


        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<report_holder>()
                .setQuery(databaseReference, report_holder.class)
                .build();
        FirebaseRecyclerAdapter<report_holder, reportViewHolder> adapter_newlyadded = new FirebaseRecyclerAdapter<report_holder, reportViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull reportViewHolder reportViewHolder, int i, @NonNull report_holder report_holder) {
                String reporter = report_holder.getName();
                String title = report_holder.getTitle();
                String videoUrl = report_holder.getVideourl();
                String address = report_holder.getAddress();
                String time = report_holder.getTime();
                String date = report_holder.getDate();
                String description = report_holder.getDescription();


                reportViewHolder.reporter.setText(reporter);
                reportViewHolder.report_title.setText(title);
                reportViewHolder.report_address.setText(address);
                reportViewHolder.report_time.setText(time + " " + date);
                reportViewHolder.report_description.setText(description);

                try {
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder().build();

                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                    view_report.reportViewHolder.exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
                    Uri video = Uri.parse(videoUrl);
                    DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("reports");
                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                    MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
                    view_report.reportViewHolder.playerView.setPlayer(view_report.reportViewHolder.exoPlayer);
                    view_report.reportViewHolder.playerView.setUseController(true);

                    view_report.reportViewHolder.exoPlayer.prepare(mediaSource);
                    view_report.reportViewHolder.exoPlayer.setPlayWhenReady(false);
                    view_report.reportViewHolder.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

                } catch (Exception e) {
                    errorSnackBarMessages(e.getMessage());
                }
            }

            @NonNull
            @Override
            public reportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_display, parent, false);
                return new reportViewHolder(view);
            }
        };
        newlyaddedRecycler.setAdapter(adapter_newlyadded);
        adapter_newlyadded.startListening();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.context.registerReceiver(broadcast, intentFilter);
    }

    public static class reportViewHolder extends RecyclerView.ViewHolder {

        TextView reporter, report_title, report_description, report_time, report_address;
        public static SimpleExoPlayer exoPlayer;
        private static PlayerView playerView;
        public static RelativeLayout parent;

        public reportViewHolder(@NonNull View itemView) {
            super(itemView);

            parent = itemView.findViewById(R.id.report_parent);
            reporter = itemView.findViewById(R.id.reporter_name);
            report_title = itemView.findViewById(R.id.report_title);
            report_description = itemView.findViewById(R.id.report_description);
            report_time = itemView.findViewById(R.id.report_time);
            report_address = itemView.findViewById(R.id.report_location);

            playerView = itemView.findViewById(R.id.video_view);

        }

    }

    public void errorSnackBarMessages(String message) {
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setBackgroundTint(Color.rgb(199, 84, 80))
                .setActionTextColor(Color.rgb(43, 43, 43))
                .show();
    }

}