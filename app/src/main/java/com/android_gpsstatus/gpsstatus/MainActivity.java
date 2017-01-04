package com.android_gpsstatus.gpsstatus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android_gpsstatus.gpsstatus.dao.data_sources.CoordinatesDataSource;
import com.android_gpsstatus.gpsstatus.dao.model.Coordinates;
import com.android_gpsstatus.gpsstatus.listeners.GPSLocationListener;
import com.android_gpsstatus.gpsstatus.listeners.SensorListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private GPSLocationListener locationListener;
    private SensorManager sensorManager;
    private SensorListener sensorListener;
    private Sensor accelerometer;
    private Sensor field;
    private CoordinatesDataSource coordinatesDataSource;

    public void goToMapActivity(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putFloat("latitude", locationListener.getLatitude());
        bundle.putFloat("longitude", locationListener.getLongitude());
        intent.putExtras(bundle);

        startActivity(intent);
    }

    public void goToCoordinatesActivity(View view) {
        startActivity(new Intent(this, CoordinatesActivity.class));
    }

    public void goToTrackingActivity(View view) {
        startActivity(new Intent(this, TrackingActivity.class));
    }

    public void saveCoordinates(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nazwij współrzędną");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                coordinatesDataSource.open();
                coordinatesDataSource.createCoordinates(new Coordinates(
                        input.getText().toString(),
                        Double.parseDouble(((TextView)findViewById(R.id.latitude_text_view)).getText().toString()),
                        Double.parseDouble(((TextView)findViewById(R.id.longitude_text_view)).getText().toString())
                ));

                coordinatesDataSource.close();
            }
        });
        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        coordinatesDataSource.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        field = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        coordinatesDataSource = new CoordinatesDataSource(this);

        locationListener = new GPSLocationListener(
                (TextView) findViewById(R.id.status_text_view),
                (TextView) findViewById(R.id.accuracy_text_view),
                (TextView) findViewById(R.id.latitude_text_view),
                (TextView) findViewById(R.id.longitude_text_view),
                (TextView) findViewById(R.id.altitude_text_view),
                (TextView) findViewById(R.id.speed_text_view),
                (TextView) findViewById(R.id.time_text_view),
                (TextView) findViewById(R.id.address_text_view),
                new Geocoder(this, Locale.getDefault())
        );

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        sensorListener = new SensorListener((ImageView)findViewById(R.id.compassImageView), (TextView) findViewById(R.id.rotation_text_view));
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

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorListener, field, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }
}
