package com.hojun.blueplace.database;

import android.database.sqlite.SQLiteConstraintException;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CloseUserDao {

    @Query("SELECT * FROM closeUser WHERE id = :id")
    CloseUser getValue(String id);

    @Query("SELECT * FROM closeUser")
    LiveData<List<CloseUser>> getAll();

    @Insert
    void insert(CloseUser closeUser);

    @Insert
    void insert(List<CloseUser> closeUser);

    @Insert
    void insertAll(List<CloseUser> closeUsers);

    @Update
    void update(CloseUser closeUser);

    @Delete
    void delete(CloseUser closeUser);

    @Query("DELETE FROM closeUser WHERE id=:id")
    void deleteById(int id);

    @Query("DELETE From closeUser" +
            "" +
            "")

    void deleteAll();
}
