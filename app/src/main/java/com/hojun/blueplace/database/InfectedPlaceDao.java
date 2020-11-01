package com.hojun.blueplace.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface InfectedPlaceDao {
    @Query("SELECT * FROM infectedplace")
    LiveData<List<InfectedPlace>> getAll();

    @Insert
    void insert(InfectedPlace infectedPlace);

    @Update
    void update(InfectedPlace infectedPlace);

    @Insert
    void insertAll(InfectedPlace... infectedPlaces);

    @Delete
    void delete(InfectedPlace infectedPlace);

    @Query("DELETE FROM infectedplace WHERE id=:id")
    void deleteById(int id);

    @Query("DELETE From infectedplace" +
            "" +
            "")
    void deleteAll();
}
