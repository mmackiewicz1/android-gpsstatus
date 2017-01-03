package com.android_gpsstatus.gpsstatus.dao.data_sources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android_gpsstatus.gpsstatus.dao.helpers.SQLiteHelper;
import com.android_gpsstatus.gpsstatus.dao.model.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class CoordinatesDataSource {
    private static final String[] columns = { SQLiteHelper.ID_COLUMN, SQLiteHelper.NAME_COLUMN, SQLiteHelper.LATITUDE_COLUMN, SQLiteHelper.LONGITUDE_COLUMN};

    private SQLiteDatabase sqLiteDatabase;
    private SQLiteHelper sqLiteHelper;

    public CoordinatesDataSource(Context context) {
        sqLiteHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        Log.i("Path:", sqLiteDatabase.getPath().toString());
    }

    public void close() {
        sqLiteHelper.close();
    }

    public Coordinates createCoordinates(Coordinates coordinates) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteHelper.NAME_COLUMN, coordinates.getName());
        contentValues.put(SQLiteHelper.LATITUDE_COLUMN, coordinates.getLatitude());
        contentValues.put(SQLiteHelper.LONGITUDE_COLUMN, coordinates.getLongitude());

        long id = sqLiteDatabase.insert(SQLiteHelper.COORDINATES_TABLE, null, contentValues);
        Cursor cursor = sqLiteDatabase.query(SQLiteHelper.COORDINATES_TABLE, columns, SQLiteHelper.ID_COLUMN + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Coordinates newCoordinates = cursorToCoordinates(cursor);
        cursor.close();

        return newCoordinates;
    }

    public void deleteCoordinates(long id) {
        System.out.println("Coordinates deleted with id: " + id);
        sqLiteDatabase.delete(SQLiteHelper.COORDINATES_TABLE, SQLiteHelper.ID_COLUMN + " = " + id, null);
    }

    public List<Coordinates> getAllCoordinates() {
        List<Coordinates> coordinatesList = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query(SQLiteHelper.COORDINATES_TABLE, columns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Coordinates coordinates = cursorToCoordinates(cursor);
            coordinatesList.add(coordinates);
            cursor.moveToNext();
        }

        cursor.close();

        return coordinatesList;
    }

    private Coordinates cursorToCoordinates(Cursor cursor) {
        Coordinates coordinates = new Coordinates();
        coordinates.setId(cursor.getLong(0));
        coordinates.setName(cursor.getString(1));
        coordinates.setLatitude(cursor.getDouble(2));
        coordinates.setLongitude(cursor.getDouble(3));

        return coordinates;
    }
}
