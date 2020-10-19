package com.hojun.blueplace.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InfectedPlaceDao {
    @Query("SELECT * FROM infectedplace")
    List<InfectedPlace> getAll();

    @Insert
    void insert(InfectedPlace infectedPlace);

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
