package com.android_gpsstatus.gpsstatus.listeners;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class GPSLocationListener extends Service implements LocationListener {
    private TextView statusTextView;
    private TextView accuracyTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView altitudeTextView;
    private TextView speedTextView;
    private TextView timeTextView;
    private TextView addressTextView;
    private Geocoder geocoder;
    private ImageView imageView;

    private float latitude;
    private float longitude;

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public GPSLocationListener(
            TextView statusTextView,
            TextView accuracyTextView,
            TextView latitudeTextView,
            TextView longitudeTextView,
            TextView altitudeTextView,
            TextView speedTextView,
            TextView timeTextView,
            TextView addressTextView,
            Geocoder geocoder,
            ImageView imageView) {
        this.statusTextView = statusTextView;
        this.accuracyTextView = accuracyTextView;
        this.latitudeTextView = latitudeTextView;
        this.longitudeTextView = longitudeTextView;
        this.altitudeTextView = altitudeTextView;
        this.speedTextView = speedTextView;
        this.timeTextView = timeTextView;
        this.addressTextView = addressTextView;
        this.geocoder = geocoder;
        this.imageView = imageView;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("Location", "Location changed");

        if (location == null) {
            return;
        }

        latitude = (float)location.getLatitude();
        longitude = (float)location.getLongitude();

        latitudeTextView.setText(String.valueOf(location.getLatitude()));
        longitudeTextView.setText(String.valueOf(location.getLongitude()));
        altitudeTextView.setText(String.valueOf(location.getAltitude()));
        speedTextView.setText(String.valueOf((int)((location.getSpeed() * 3600) / 1000)) + " km/h");
        timeTextView.setText(String.valueOf(new Timestamp(location.getTime())));
        addressTextView.setText(getAddress(location.getLatitude(), location.getLongitude()));
        accuracyTextView.setText(String.valueOf(location.getAccuracy()) + " m");

        imageView.setRotation(location.getBearing());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("Status", "Status changed.");
        Log.i("Status", "Status: " + status);
        Log.i("Status", "Provider: " + provider);
        statusTextView.setText(String.valueOf(status));
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("Provider", "Provider enabled.");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("Provider", "Provider disabled");
    }

    public String getAddress(double lat, double lon) {
        String ret;
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                strReturnedAddress.append(returnedAddress.getAddressLine(0)).append("\n");
                strReturnedAddress.append(returnedAddress.getLocality()).append("\n");
                strReturnedAddress.append(returnedAddress.getAddressLine(1)).append("\n");
                strReturnedAddress.append(returnedAddress.getCountryName()).append("\n");
                ret = strReturnedAddress.toString();
            } else {
                ret = "Brak zwróconego adresu!";
            }
        } catch (IOException e) {
            Log.e("Geocoder", e.getMessage());
            ret = "Can't get Address!";
        }

        return ret;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
