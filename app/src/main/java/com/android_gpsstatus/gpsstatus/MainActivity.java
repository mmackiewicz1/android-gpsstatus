package com.android_gpsstatus.gpsstatus;

import android.content.Context;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationListener = new GPSLocationListener(
                (TextView)findViewById(R.id.status_text_view),
                (TextView)findViewById(R.id.accuracy_text_view),
                (TextView)findViewById(R.id.latitude_text_view),
                (TextView)findViewById(R.id.longitude_text_view),
                (TextView)findViewById(R.id.altitude_text_view),
                (TextView)findViewById(R.id.speed_text_view),
                (TextView)findViewById(R.id.time_text_view),
                (TextView)findViewById(R.id.address_text_view),
                new Geocoder(this, Locale.getDefault())
        );
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private void assignFields() {

    }
}
