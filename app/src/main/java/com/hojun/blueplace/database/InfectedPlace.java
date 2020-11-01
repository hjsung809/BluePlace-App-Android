package com.hojun.blueplace.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class InfectedPlace {
    @PrimaryKey
    public int id;

    public String infected_place_name;
    public String infected_place_name_en;
    public String adress;
    public String note;

    public double latitude;
    public double longitude;
    public double size;
    public int level;

    public String first_visit_time;
    public String last_visit_time;
    public int visit_count;
}
