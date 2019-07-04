package com.radhika.geotagging.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.radhika.geotagging.Models.Maps;

import java.util.List;

@Dao
public interface MapsDao {
    @Insert
    void insert(Maps maps);
    @Update
    void update(Maps maps);
    @Delete
    void delete(Maps maps);
    @Query("SELECT * from maps")
    LiveData<List<Maps>> getAllMaps();
}
