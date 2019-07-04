package com.radhika.geotagging.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.radhika.geotagging.Adapter.MapsAdapter;
import com.radhika.geotagging.Common.GpsUtils;
import com.radhika.geotagging.Common.Utils;
import com.radhika.geotagging.Helpers.RuntimePermissionHelper;
import com.radhika.geotagging.Models.Maps;
import com.radhika.geotagging.R;
import com.radhika.geotagging.ViewModels.MapsViewModel;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    GpsUtils gps;
    Gson gson;
    private RuntimePermissionHelper runtimePermissionHelper;
    private FloatingActionButton floatingActionButton;
    private boolean isGPS;
    private RecyclerView recyclerView;
    private MapsAdapter mapsAdapter;
    private List<Maps> lstMaps;
    private MapsViewModel mapsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gps = new GpsUtils(this);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        recyclerView = findViewById(R.id.rv_maps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mapsAdapter = new MapsAdapter();
        recyclerView.setAdapter(mapsAdapter);
        mapsViewModel = ViewModelProviders.of(this).get(MapsViewModel.class);
        getAllMaps(null);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mapsViewModel.delete(mapsAdapter.getMapAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Node deleted successfully.", Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void openGoogleMapsActivity() {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        if(lstMaps != null && lstMaps.size() > 0) {
            intent.putExtra("lst_maps", gson.toJson(lstMaps));
        }
        startActivityForResult(intent, 1);
    }

    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= 23) {
            runtimePermissionHelper = RuntimePermissionHelper.getInstance(MainActivity.this);
            if (runtimePermissionHelper.isAllPermissionAvailable()) {
                gps.turnGPSOn(new GpsUtils.onGpsListener() {
                    @Override
                    public void gpsStatus(boolean isGPSEnable) {
                        isGPS = isGPSEnable;
                        if (isGPS) {
                            if(Utils.isOnline(MainActivity.this)) {
                                openGoogleMapsActivity();
                            } else {
                                Toast.makeText(MainActivity.this, R.string.internet_connection, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            } else {
                runtimePermissionHelper.setActivity(MainActivity.this);
                runtimePermissionHelper.requestPermissionsIfDenied();
            }
        } else {
            gps.turnGPSOn(new GpsUtils.onGpsListener() {
                @Override
                public void gpsStatus(boolean isGPSEnable) {
                    isGPS = isGPSEnable;
                    if (isGPSEnable) {
                        if(Utils.isOnline(MainActivity.this)) {
                            openGoogleMapsActivity();
                        }else {
                            Toast.makeText(MainActivity.this, R.string.internet_connection, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED)
        {
            return;
        }
        if (requestCode == 1) {
            Type type = new TypeToken<List<Maps>>() {
            }.getType();
            List<Maps> localLstMaps = gson.fromJson(data.getStringExtra("maps"), type);
            if (localLstMaps != null && localLstMaps.size() > 0) {
                for (Maps maps : localLstMaps) {
                    if(maps.getId() != 0){
                        mapsViewModel.update(maps);
                    }else {
                        mapsViewModel.insert(maps);
                    }
                }
            }
        } else {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Serializable serializable = bundle.getSerializable("maps");
                    if (serializable != null) {
                        JsonElement jsonElement = gson.toJsonTree(serializable);
                        Maps maps = gson.fromJson(jsonElement, Maps.class);
                        getAllMaps(maps);
                    }
                }
            }
        }
    }

    private void getAllMaps(Maps maps) {
        int id = 0;
        if(maps != null){
            id = maps.getId();
        }
        mapsViewModel.getAllMaps().observe(this, new Observer<List<Maps>>() {
            @Override
            public void onChanged(List<Maps> maps) {
                lstMaps = maps;
                mapsAdapter.setMaps(maps);
            }
        });
    }
}
