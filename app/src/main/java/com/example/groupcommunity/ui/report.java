package com.example.groupcommunity.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.groupcommunity.R;
import com.example.groupcommunity.adapter.placeSuggestion;
import com.example.groupcommunity.util.Broadcast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class report extends Fragment implements OnMapReadyCallback {


    private GoogleMap map;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private static final int VIDEO_REQUEST = 101;
    private Circle circle, circle2, circle3, circle4;
    private String client_long, client_lat, client_address, title_pass, description_pass;
    private boolean home = false;

    private FragmentActivity context;

    Broadcast broadcast = new Broadcast();

    public report() {
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        context = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_report, container, false);
        return v;
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    final AutoCompleteTextView autoCompleteTextView = context.findViewById(R.id.address);

                    final EditText title = context.findViewById(R.id.title_report);
                    final EditText description_field = context.findViewById(R.id.description_report);

                    makeSnackBarMessages("Current Location Found");
                    currentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(report.this);


                    //for the autocomplete address
                    autoCompleteTextView.setAdapter(new placeSuggestion(getActivity(), android.R.layout.simple_list_item_1));
                    autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            client_address = autoCompleteTextView.getText().toString();
                            getLatLngAddress(autoCompleteTextView.getText().toString());
                            makeSnackBarMessages("Location Found");
                        }
                    });


                    final FloatingActionButton live = context.findViewById(R.id.record);
                    live.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                client_address = autoCompleteTextView.getText().toString();
                                description_pass = description_field.getText().toString();
                                title_pass = title.getText().toString();

                                if (description_pass != null && title_pass != null && description_pass != null) {
                                    makeSnackBarMessages("Recording in Progress");
                                    capture_video();
                                } else {
                                    title.setError("No Title Found");
                                    title.requestFocus();

                                    description_field.setError("No Description entered");
                                    description_field.requestFocus();

                                    errorSnackBarMessages("Fill out required fields");
                                }
                            } catch (Exception e) {
                                errorSnackBarMessages("One or more fields are empty ");
                            }
                        }
                    });

                    final ImageView home = context.findViewById(R.id.home);
                    home.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                            fetchLastLocation();
                            autoCompleteTextView.setText(client_address);
                        }
                    });
                } else {
                    errorSnackBarMessages("Turn on Location");
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Intent playIntent = new Intent(getActivity(), upload.class);
            playIntent.putExtra("videoUri", data.getData().toString());
            playIntent.putExtra("descriptionText", description_pass);
//            playIntent.putExtra("description", des);
            playIntent.putExtra("title", title_pass);
            playIntent.putExtra("clientAddress", client_address);

            startActivity(playIntent);
        } catch (NullPointerException e) {
            errorSnackBarMessages(e.getMessage());
        }

    }

    private LatLng getLatLngAddress(String address) {
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if (address != null) {
                Address singleAddress = addressList.get(0);
                LatLng latLng = new LatLng(singleAddress.getLatitude(), singleAddress.getLongitude());

                map.addMarker(new MarkerOptions().position(latLng)
                        .title("Address").snippet(address)).showInfoWindow();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

                circle = map.addCircle(new CircleOptions()
                        .center(new LatLng(singleAddress.getLatitude(), singleAddress.getLongitude()))
                        .radius(500)
                        .strokeWidth(10)
                        .strokeColor(Color.argb(150, 133, 78, 217))
                        .fillColor(Color.argb(128, 144, 211, 255))
                        .clickable(true));
                circle2 = map.addCircle(new CircleOptions()
                        .center(new LatLng(singleAddress.getLatitude(), singleAddress.getLongitude()))
                        .radius(1000)
                        .strokeWidth(10)
                        .strokeColor(Color.argb(100, 133, 78, 217))
                        .fillColor(Color.argb(128, 144, 211, 255))
                        .clickable(true));
                circle3 = map.addCircle(new CircleOptions()
                        .center(new LatLng(singleAddress.getLatitude(), singleAddress.getLongitude()))
                        .radius(1500)
                        .strokeWidth(10)
                        .strokeColor(Color.argb(50, 133, 78, 217))
                        .fillColor(Color.argb(128, 144, 211, 255))
                        .clickable(true));
                return latLng;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        final LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        client_lat = currentLocation.getLatitude() + "";
        client_long = currentLocation.getLongitude() + "";

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> address = geocoder.getFromLocation(
                    currentLocation.getLatitude(), currentLocation.getLongitude(), 1
            );
            client_address = address.get(0).getAddressLine(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        map.setMinZoomPreference(12.0f);
        map.setMaxZoomPreference(18.0f);
        map.addMarker(new MarkerOptions().position(latLng)
                .title("Your current Location").snippet(client_address)).showInfoWindow();
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        circle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .radius(500)
                .strokeWidth(10)
                .strokeColor(Color.argb(150, 133, 78, 217))
                .fillColor(Color.argb(128, 144, 211, 255))
                .clickable(true));
        circle2 = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .radius(1000)
                .strokeWidth(10)
                .strokeColor(Color.argb(100, 133, 78, 217))
                .fillColor(Color.argb(128, 144, 211, 255))
                .clickable(true));
        circle3 = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .radius(1500)
                .strokeWidth(10)
                .strokeColor(Color.argb(50, 133, 78, 217))
                .fillColor(Color.argb(128, 144, 211, 255))
                .clickable(true));

        googleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {

                int strokeColor = circle.getStrokeColor() ^ 0x00ffffff;
                circle.setStrokeColor(strokeColor);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }

    @Override
    public void onStart() {
        IntentFilter intentFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        this.context.registerReceiver(broadcast, intentFilter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getActivity(), signup.class);
            startActivity(intent);
            context.finish();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fetchLastLocation();
        super.onStart();
    }

    private void capture_video() {
        Intent video_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        video_intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
        if (video_intent.resolveActivity(context.getPackageManager()) != null) {
            startActivityForResult(video_intent, VIDEO_REQUEST);
        }
    }

    private void errorSnackBarMessages(String message) {
        Snackbar.make(getActivity().findViewById(R.id.record), message, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setBackgroundTint(Color.rgb(199, 84, 80))
                .setActionTextColor(Color.rgb(43, 43, 43))
                .show();
    }

    private void makeSnackBarMessages(String message) {
        Snackbar.make(getActivity().findViewById(R.id.record), message, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setBackgroundTint(Color.rgb(73, 156, 84))
                .setActionTextColor(Color.rgb(43, 43, 43))
                .show();
    }

}