package com.hojun.blueplace.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Clique {
    // 연관된 것들만 저장. active 필드 추가.
    @PrimaryKey(autoGenerate = false)
    public int id;
    public int ownerId;

    public String name;
    public boolean active;
}
