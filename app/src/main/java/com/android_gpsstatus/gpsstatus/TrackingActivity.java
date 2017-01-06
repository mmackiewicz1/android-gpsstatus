package com.android_gpsstatus.gpsstatus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android_gpsstatus.gpsstatus.listeners.NavigationLocationListener;

public class TrackingActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor field;
    private NavigationLocationListener locationListener;

    public void startNavigation(View view) {
        EditText latitudeEditText = ((EditText)findViewById(R.id.latitude_text_view));
        EditText longitudeEditText = ((EditText)findViewById(R.id.longitude_text_view));

        if (isDataFilled(latitudeEditText, longitudeEditText)) {
            Log.i("Tag", "Registering listener");
            latitudeEditText.setTextColor(Color.BLACK);
            longitudeEditText.setTextColor(Color.BLACK);

            sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            field = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            locationListener = new NavigationLocationListener(
                    (ImageView)findViewById(R.id.arrowImageView),
                    Double.parseDouble(latitudeEditText.getText().toString()),
                    Double.parseDouble(longitudeEditText.getText().toString())
            );

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            }

            sensorManager.registerListener(locationListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(locationListener, field, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_activity);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Twój GPS jest wyłączony. Czy chcesz go włączyć?")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private boolean isDataFilled(EditText latitudeEditText, EditText longitudeEditText) {
        boolean isDataValid = true;

        if (latitudeEditText.getText().length() == 0) {
            isDataValid = false;
            latitudeEditText.setTextColor(Color.RED);
        }

        if (longitudeEditText.getText().length() == 0) {
            isDataValid = false;
            longitudeEditText.setTextColor(Color.RED);
        }

        return isDataValid;
    }

    protected void onResume() {
        super.onResume();
        if (locationListener != null) {
            sensorManager.registerListener(locationListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(locationListener, field, SensorManager.SENSOR_DELAY_UI);
        }
    }

    protected void onPause() {
        super.onPause();
        if (locationListener != null) {
            sensorManager.unregisterListener(locationListener);
        }
    }
}
