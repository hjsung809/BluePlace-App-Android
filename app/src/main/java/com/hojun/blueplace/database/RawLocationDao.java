package com.hojun.blueplace.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RawLocationDao {
    @Query("SELECT * FROM rawlocation ORDER BY id DESC LIMIT 20")
    LiveData<List<RawLocation>> getAll();

    @Query("SELECT * FROM rawlocation order by utc_time desc limit 1")
    RawLocation getRecent();

    @Query("SELECT * FROM rawlocation")
    List<RawLocation> getAllForPlace();

    @Insert
    void insert(RawLocation rawLocation);

    @Insert
    void insertAll(RawLocation... rawLocations);

    @Query("DELETE From rawlocation")
    void deleteAll();

    @Delete
    void delete(RawLocation rawLocation);

    @Query("DELETE From rawlocation WHERE utc_time < :utc_time")
    void deleteByTime(long utc_time);

    @Query("DELETE From rawlocation WHERE id=:id")
    void deleteById(int id);
}
