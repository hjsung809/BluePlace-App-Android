package com.hojun.blueplace.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserData {
    @PrimaryKey
    @NonNull
    public String id;
    public String value;
}
