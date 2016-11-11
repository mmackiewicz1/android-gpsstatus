package com.android_gpsstatus.gpsstatus;

import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class GPSLocationListener implements LocationListener {
    private TextView statusTextView;
    private TextView accuracyTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView altitudeTextView;
    private TextView speedTextView;
    private TextView timeTextView;
    private TextView addressTextView;
    private Geocoder geocoder;

    public GPSLocationListener(
            TextView statusTextView,
            TextView accuracyTextView,
            TextView latitudeTextView,
            TextView longitudeTextView,
            TextView altitudeTextView,
            TextView speedTextView,
            TextView timeTextView,
            TextView addressTextView,
            Geocoder geocoder) {
        this.statusTextView = statusTextView;
        this.accuracyTextView = accuracyTextView;
        this.latitudeTextView = latitudeTextView;
        this.longitudeTextView = longitudeTextView;
        this.altitudeTextView = altitudeTextView;
        this.speedTextView = speedTextView;
        this.timeTextView = timeTextView;
        this.addressTextView = addressTextView;
        this.geocoder = geocoder;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitudeTextView.setText(String.valueOf(location.getLatitude()));
        longitudeTextView.setText(String.valueOf(location.getLongitude()));
        altitudeTextView.setText(String.valueOf(location.getAltitude()));
        speedTextView.setText(String.valueOf((int)((location.getSpeed() * 3600) / 1000)) + " km/h");
        timeTextView.setText(String.valueOf(new Timestamp(new Date().getTime())));
        addressTextView.setText(getAddress(location.getLatitude(), location.getLongitude()));
        accuracyTextView.setText(String.valueOf(location.getAccuracy()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("Status", "Status changed.");
        Log.i("Status", "Status: " + status);
        Log.i("Status", "Satellite: " + GpsStatus.GPS_EVENT_SATELLITE_STATUS);
        statusTextView.setText(String.valueOf(status));
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
                ret = "No Address returned!";
            }
        } catch (IOException e) {
            Log.e("Geocoder", e.getMessage());
            ret = "Can't get Address!";
        }

        return ret;
    }
}
