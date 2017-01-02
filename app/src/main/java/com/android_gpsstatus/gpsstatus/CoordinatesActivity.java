package com.android_gpsstatus.gpsstatus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android_gpsstatus.gpsstatus.dao.data_sources.CoordinatesDataSource;
import com.android_gpsstatus.gpsstatus.dao.model.Coordinates;

public class CoordinatesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates);

        CoordinatesDataSource coordinatesDataSource = new CoordinatesDataSource(this);
        coordinatesDataSource.open();

        for (Coordinates coordinates : coordinatesDataSource.getAllCoordinates()) {
            TextView textView = new TextView(this);
            textView.setText(coordinates.getName() + ": " + coordinates.getLatitude() + ", " + coordinates.getLongitude());
            ((LinearLayout)findViewById(R.id.coordinatesListLayout)).addView(textView);
        }

        coordinatesDataSource.close();
    }
}
