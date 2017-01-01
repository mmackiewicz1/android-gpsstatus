package com.android_gpsstatus.gpsstatus;


import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;
import static android.hardware.SensorManager.AXIS_X;
import static android.hardware.SensorManager.AXIS_Z;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.ImageView;
import android.widget.TextView;

public class SensorListener implements SensorEventListener {
    private static final int ROTATION_FIXTURE = 180;

    private ImageView compassImageView;
    private TextView rotationTextView;
    private float[] gravityDataArray;
    private float[] magneticDataArray;

    public SensorListener(ImageView imageView, TextView rotationTextView) {
        compassImageView = imageView;
        this.rotationTextView = rotationTextView;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
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

    private void updateDirection() {
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

        compassImageView.setRotation(computedValues[0]);
        rotationTextView.setText(String.valueOf(computedValues[0]));
    }
}
