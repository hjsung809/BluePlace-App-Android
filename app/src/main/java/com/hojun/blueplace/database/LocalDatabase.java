package com.hojun.blueplace.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {InfectedPlace.class, RawLocation.class, UserData.class,  UserPlace.class, CloseUser.class}, version = 2, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {
    private volatile static LocalDatabase INSTANCE;

    public abstract RawLocationDao rawLocationDao();
    public abstract UserDataDao userDataDao();
    public abstract UserPlaceDao userPlaceDao();
    public abstract InfectedPlaceDao infectedPlaceDao();
    public abstract CloseUserDao closeUserDao();

    public static LocalDatabase getInstance(Context context){
        if(INSTANCE == null){
            synchronized (LocalDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context,LocalDatabase.class,"local-db").build();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }
}
