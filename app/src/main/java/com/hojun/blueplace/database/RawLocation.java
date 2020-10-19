package com.hojun.blueplace.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RawLocation {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public float accuracy;
    public double latitude;
    public double longitude;
    public double altitude;
    public float bearing;

    public long utc_time;
    public long measurement_time;
}
