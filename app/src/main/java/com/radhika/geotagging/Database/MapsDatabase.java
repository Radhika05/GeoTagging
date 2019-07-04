package com.radhika.geotagging.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.radhika.geotagging.Dao.MapsDao;
import com.radhika.geotagging.Models.Maps;

@Database(entities = Maps.class, version = 1)
public abstract class MapsDatabase extends RoomDatabase {
    private static MapsDatabase instance;
    public abstract MapsDao mapsDao();
    public static synchronized MapsDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MapsDatabase.class,"maps_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
