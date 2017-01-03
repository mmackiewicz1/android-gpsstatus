package com.android_gpsstatus.gpsstatus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android_gpsstatus.gpsstatus.dao.data_sources.CoordinatesDataSource;
import com.android_gpsstatus.gpsstatus.dao.model.Coordinates;

public class CoordinatesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates);

        fillData();
    }

    private void fillData() {
        final CoordinatesDataSource coordinatesDataSource = new CoordinatesDataSource(this);
        coordinatesDataSource.open();

        for (final Coordinates coordinates : coordinatesDataSource.getAllCoordinates()) {
            TextView textView = new TextView(this) {{
                setText(coordinates.getName() + ": " + coordinates.getLatitude() + ", " + coordinates.getLongitude());
                setPadding(10, 10, 10, 10);
                setClickable(true);
                setTextColor(Color.BLACK);
            }};

            final TableLayout tableLayout = ((TableLayout)findViewById(R.id.coordinatesListLayout));
            TableRow tableRow = new TableRow(this);
            tableRow.addView(textView);
            tableLayout.addView(tableRow);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setBuilder(coordinates.getName(), coordinates.getId(), tableLayout, coordinatesDataSource);
                }
            });
        }

        coordinatesDataSource.close();
    }

    private void setBuilder(final String name, final long id, final TableLayout tableLayout, final CoordinatesDataSource coordinatesDataSource) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nazwa: " + name);

        builder.setNeutralButton("Usuń", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tableLayout.removeAllViews();
                coordinatesDataSource.open();
                coordinatesDataSource.deleteCoordinates(id);
                coordinatesDataSource.close();
                fillData();
            }
        });

        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
