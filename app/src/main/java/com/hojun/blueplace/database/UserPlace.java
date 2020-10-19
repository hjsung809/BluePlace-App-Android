package com.hojun.blueplace.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserPlace {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double latitude;
    public double longitude;
    public double size;

    public long first_visit_time;
    public long last_visit_time;
    public int visit_count;
}
