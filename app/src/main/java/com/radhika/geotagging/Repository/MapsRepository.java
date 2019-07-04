package com.radhika.geotagging.Repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.radhika.geotagging.Dao.MapsDao;
import com.radhika.geotagging.Database.MapsDatabase;
import com.radhika.geotagging.Models.Maps;

import java.util.List;

public class MapsRepository {
    private MapsDao mapsDao;
    private LiveData<List<Maps>> allMaps;

    public MapsRepository(Application application){
        MapsDatabase mapsDatabase = MapsDatabase.getInstance(application);
        mapsDao = mapsDatabase.mapsDao();
        allMaps = mapsDao.getAllMaps();
    }

    public void insert(Maps maps){
        new InsertMapsAsync(mapsDao).execute(maps);
    }

    public void update(Maps maps){
        new UpdateMapsAsync(mapsDao).execute(maps);
    }

    public void delete(Maps maps){
        new DeleteMapsAsync(mapsDao).execute(maps);
    }

    public LiveData<List<Maps>> getAllMaps(){
        return allMaps;
    }

    public static class InsertMapsAsync extends AsyncTask<Maps, Void, Void>{
        MapsDao mapsDao;
        private InsertMapsAsync(MapsDao mapsDao){
            this.mapsDao = mapsDao;
        }
        @Override
        protected Void doInBackground(Maps... maps) {
            mapsDao.insert(maps[0]);
            return null;
        }
    }

    public static class UpdateMapsAsync extends AsyncTask<Maps, Void, Void>{
        MapsDao mapsDao;
        private UpdateMapsAsync(MapsDao mapsDao){
            this.mapsDao = mapsDao;
        }
        @Override
        protected Void doInBackground(Maps... maps) {
            mapsDao.update(maps[0]);
            return null;
        }
    }

    public static class DeleteMapsAsync extends AsyncTask<Maps, Void, Void>{
        MapsDao mapsDao;
        private DeleteMapsAsync(MapsDao mapsDao){
            this.mapsDao = mapsDao;
        }
        @Override
        protected Void doInBackground(Maps... maps) {
            mapsDao.delete(maps[0]);
            return null;
        }
    }
}
