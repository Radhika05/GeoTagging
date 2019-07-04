package com.radhika.geotagging.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.radhika.geotagging.Common.GpsUtils;
import com.radhika.geotagging.Common.Utils;
import com.radhika.geotagging.Helpers.RuntimePermissionHelper;
import com.radhika.geotagging.Models.Maps;
import com.radhika.geotagging.R;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint("MissingPermission")
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, View.OnClickListener {
    private List<Marker> lstMarker = new ArrayList<>();
    private ProgressBar progressBar;
    private List<Maps> lstMaps = new ArrayList<>();
    private ImageButton btnSaveMarker;
    private GoogleMap mMap;
    private GpsUtils gps;
    private TextView tvError;
    private Gson gson;
    private Maps maps;
    private boolean isSet, isSingleView;
    private LocationListener locationListenerGps = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (!isSet) {
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 20.0f));
                    isSet = true;
                    progressBar.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("sas", provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("sas", provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("sas", provider);
        }
    };
    private RuntimePermissionHelper runtimePermissionHelper;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        progressBar = findViewById(R.id.pb_loader);
        tvError = findViewById(R.id.tv_error);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnSaveMarker = findViewById(R.id.btn_save_marker);
        btnSaveMarker.setOnClickListener(this);
        gps = new GpsUtils(this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Serializable serializable = bundle.getSerializable("maps");
            if (serializable != null) {
                JsonElement jsonElement = gson.toJsonTree(serializable);
                maps = gson.fromJson(jsonElement, Maps.class);
                if (maps != null) {
                    isSingleView = true;
                }
            } else {
                bindLstMaps();
            }
        } else {
            bindLstMaps();
        }
    }

    private void bindLstMaps() {
        gps.setLocationListenerGps(locationListenerGps);
        String lst_maps = getIntent().getStringExtra("lst_maps");
        if (!TextUtils.isEmpty(lst_maps)) {
            Type type = new TypeToken<List<Maps>>() {
            }.getType();
            lstMaps = gson.fromJson(lst_maps, type);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        if (maps != null) {
            LatLng latLng = new LatLng(maps.getLatitude(), maps.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20.0f));
            try {
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(GpsUtils.GetAddressStringFromLatLng(latLng, this))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                progressBar.setVisibility(View.GONE);
                btnSaveMarker.setImageResource(R.drawable.ic_arrow_back);
                tvError.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (lstMaps != null && lstMaps.size() > 0) {
            for (Maps maps : lstMaps) {
                LatLng latLng = new LatLng(maps.getLatitude(), maps.getLongitude());
                try {
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(GpsUtils.GetAddressStringFromLatLng(latLng, this))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    lstMarker.add(marker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mMap.setOnMapLongClickListener(this);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setOnMapLongClickListener(this);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(Utils.isOnline(this)) {
            addMarker(latLng);
        } else {
            Toast.makeText(this, R.string.internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void addMarker(LatLng latLng) {
        try {
            String address = GpsUtils.GetAddressStringFromLatLng(latLng, this);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.setOnMarkerClickListener(this);
            lstMarker.add(marker);
            lstMaps.add(new Maps(address, latLng.latitude, latLng.longitude, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (maps == null) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.image_marker)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            position = lstMarker.indexOf(marker);
                            openCamera();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("maps", maps);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(2, intent);
            finish();
        }
        return false;
    }

    private void openCamera() {
        if (Build.VERSION.SDK_INT >= 23) {
            runtimePermissionHelper = RuntimePermissionHelper.getInstance(this);
            if (runtimePermissionHelper.isAllPermissionAvailable()) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 11);
            } else {
                runtimePermissionHelper.setActivity(this);
                runtimePermissionHelper.requestPermissionsIfDenied();
            }
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 11);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 11) {
                Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                assert photo != null;
                Maps maps = lstMaps.get(position);
                maps.setImage(Utils.saveToInternalStorage(photo, this));
                lstMaps.set(position, maps);
                Toast.makeText(this, "Image saved successfully.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (maps == null) {
            if (lstMaps != null && lstMaps.size() > 0) {
                Intent data = new Intent();
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                gson = gsonBuilder.create();
                data.putExtra("maps", gson.toJson(lstMaps));
                setResult(1, data);
                finish();
            } else {
                Toast.makeText(this, R.string.add_marker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            finish();
        }
    }
}
