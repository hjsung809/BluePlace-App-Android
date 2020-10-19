package com.hojun.blueplace.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDataDao {
    @Query("SELECT * FROM userdata")
    LiveData<List<UserData>> getAll();

    @Query("SELECT * FROM userdata WHERE id = :id")
    UserData getValue(String id);

    @Insert
    void insert(UserData userData);

    @Update
    void update(UserData userData);

    @Query("DELETE FROM userdata WHERE id = :id")
    void deleteById(String id);

    @Query("DELETE From userdata")
    void deleteAll();
}
