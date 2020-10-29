package com.hojun.blueplace.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CloseUser {
    @PrimaryKey(autoGenerate = false)
    public int id;
    public String email;
    public String phoneNumber;

    public boolean active;
}
