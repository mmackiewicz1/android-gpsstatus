package com.android_gpsstatus.gpsstatus.listeners;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;
import static android.hardware.SensorManager.AXIS_X;
import static android.hardware.SensorManager.AXIS_Z;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

public class NavigationLocationListener extends Service implements LocationListener, SensorEventListener {
    private static final int ROTATION_FIXTURE = 180;

    private ImageView imageView;
    private double destinationLatitude;
    private double destinationLongitude;
    private double locationRotation = 0;
    private float[] gravityDataArray;
    private float[] magneticDataArray;

    public NavigationLocationListener(ImageView imageView, double destinationLatitude, double destinationLongitude) {
        this.imageView = imageView;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i("Tag", "Sensor event invoked");
        switch(event.sensor.getType()) {
            case TYPE_ACCELEROMETER:
                gravityDataArray = event.values.clone();
                break;
            case TYPE_MAGNETIC_FIELD:
                magneticDataArray = event.values.clone();
                break;
            default:
                return;
        }

        if (gravityDataArray != null && magneticDataArray != null) {
            updateDirection();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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

        setLocationRotation(location);
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

    private void setLocationRotation(Location location){
        double longitudeDifference = Math.toRadians(location.getLongitude() - destinationLongitude);
        double currentLatitudeRadians = Math.toRadians(location.getLatitude());
        double destinationLatitudeRadians = Math.toRadians(destinationLatitude);
        locationRotation = (Math.toDegrees(Math.atan2(
                Math.sin(longitudeDifference) * Math.cos(currentLatitudeRadians),
                Math.cos(destinationLatitudeRadians) * Math.sin(currentLatitudeRadians) - Math.sin(destinationLatitudeRadians) * Math.cos(currentLatitudeRadians) * Math.cos(longitudeDifference)
        )) + 360) % 360;
    }

    private void updateDirection() {
        Log.i("Tag", "Updating rotation");
        float[] temporaryArray = new float[9];
        SensorManager.getRotationMatrix(temporaryArray, null, gravityDataArray, magneticDataArray);

        float[] rotationArray = new float[9];
        SensorManager.remapCoordinateSystem(temporaryArray, AXIS_X, AXIS_Z, rotationArray);

        float[] computedValues = new float[3];
        SensorManager.getOrientation(rotationArray, computedValues);

        for (int i = 0; i < computedValues.length; i++) {
            Double degrees = (computedValues[i] * ROTATION_FIXTURE) / Math.PI;
            computedValues[i] = degrees.floatValue();
        }

        imageView.setRotation(((float)locationRotation + computedValues[0]) % 360);
    }
}
