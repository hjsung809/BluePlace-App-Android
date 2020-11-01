package com.hojun.blueplace.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserPlaceDao {
    @Query("SELECT * FROM userplace")
    LiveData<List<UserPlace>> getAll();

    @Query("SELECT * FROM userplace")
    List<UserPlace> getAllForInfectedPlace();

    @Insert
    void insert(UserPlace userPlace);

    @Insert
    void insertAll(UserPlace... userPlaces);

    @Delete
    void delete(UserPlace userPlace);

    @Query("DELETE FROM userplace WHERE id=:id")
    void deleteById(int id);

    @Query("DELETE From userplace")
    void deleteAll();
}
