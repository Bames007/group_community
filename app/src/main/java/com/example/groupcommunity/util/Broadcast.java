package com.example.groupcommunity.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class Broadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false
            );
            if (noConnectivity) {
                Toast.makeText(context, "No Network Available", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Network Available", Toast.LENGTH_SHORT).show();
            }
        }

        if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
            boolean noLocation = intent.getBooleanExtra(
                    LocationManager.KEY_PROVIDER_ENABLED, false
            );
            if (noLocation) {
                Toast.makeText(context, "Location Turned-Off", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Location Found", Toast.LENGTH_LONG).show();
            }
        }
    }
}
