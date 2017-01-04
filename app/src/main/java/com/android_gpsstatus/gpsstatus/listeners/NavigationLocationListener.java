package com.android_gpsstatus.gpsstatus.listeners;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

public class NavigationLocationListener extends Service implements LocationListener {
    private ImageView imageView;
    private double destinationLatitude;
    private double destinationLongitude;

    public NavigationLocationListener(ImageView imageView, double destinationLatitude, double destinationLongitude) {
        this.imageView = imageView;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("Location", "Location changed");

        if (location == null) {
            return;
        }

        setImageRotation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("Provider", "Provider enabled.");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("Provider", "Provider disabled");
    }

    private void setImageRotation(Location location){
        double longitudeDistance = destinationLongitude - location.getLongitude();
        double rotation = Math.toDegrees(Math.atan2(Math.sin(longitudeDistance) * Math.cos(destinationLatitude),
                Math.cos(location.getLatitude()) * Math.sin(destinationLatitude) - Math.sin(location.getLatitude())*Math.cos(destinationLatitude)*Math.cos(longitudeDistance)));
        rotation = (360 - ((rotation + 360) % 360));

        imageView.setRotation((float) rotation);
    }
}
