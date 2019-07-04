package com.radhika.geotagging.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.radhika.geotagging.Models.Maps;
import com.radhika.geotagging.Repository.MapsRepository;

import java.util.List;

public class MapsViewModel extends AndroidViewModel {

    private MapsRepository mapsRepository;
    private LiveData<List<Maps>> allMaps;

    public MapsViewModel(@NonNull Application application) {
        super(application);
        mapsRepository = new MapsRepository(application);
        allMaps = mapsRepository.getAllMaps();
    }

    public void insert(Maps maps){
        mapsRepository.insert(maps);
    }

    public void update(Maps maps){
        mapsRepository.update(maps);
    }

    public void delete(Maps maps){
        mapsRepository.delete(maps);
    }

    public LiveData<List<Maps>> getAllMaps() {
        return allMaps;
    }
}
