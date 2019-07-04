package com.radhika.geotagging.Helpers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.radhika.geotagging.R;

import java.util.ArrayList;

public final class RuntimePermissionHelper {
    public static final int PERMISSION_REQUEST_CODE = 1;
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private Activity activity;
    private ArrayList<String> requiredPermissions;
    private ArrayList<String> deniedPermissions = new ArrayList<String>();
    private static RuntimePermissionHelper runtimePermissionHelper;

    private RuntimePermissionHelper(Activity activity) {
        this.activity = activity;
    }

    public static synchronized RuntimePermissionHelper getInstance(Activity activity) {
        if (runtimePermissionHelper == null) {
            runtimePermissionHelper = new RuntimePermissionHelper(activity);
        }
        return runtimePermissionHelper;
    }
    private void initPermissions() {
        requiredPermissions = new ArrayList<String>();
        requiredPermissions.add(PERMISSION_CAMERA);
        requiredPermissions.add(STORAGE);
        requiredPermissions.add(FINE_LOCATION);
        requiredPermissions.add(COARSE_LOCATION);
    }

    public void requestPermissionsIfDenied() {
        deniedPermissions = getdeniedPermissionsList();
        if (canShowPermissionRationaleDialog()) {
            showMessageOKCancel(activity.getResources().getString(R.string.permission_message),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            askPermissions();
                        }
                    });
            return;
        }
        askPermissions();
    }

    public void requestPermissionsIfDenied(final String permission) {
        if (canShowPermissionRationaleDialog(permission)) {
            showMessageOKCancel(activity.getResources().getString(R.string.permission_message),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            askPermission(permission);
                        }
                    });
            return;
        }
        askPermission(permission);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean canShowPermissionRationaleDialog() {
        boolean shouldShowRationale = false;
        for (String permission : deniedPermissions) {
            boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
            if (shouldShow) {
                shouldShowRationale = true;
            }
        }
        return shouldShowRationale;
    }

    public boolean canShowPermissionRationaleDialog(String permission) {
        boolean shouldShowRationale = false;
        boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        if (shouldShow) {
            shouldShowRationale = true;
        }
        return shouldShowRationale;
    }

    private void askPermissions() {
        if (deniedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(activity, deniedPermissions.toArray(new String[deniedPermissions.size()]), PERMISSION_REQUEST_CODE);
        }
    }

    private void askPermission(String permission) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(activity, AddServiceFragment.class);
//                        intent.putExtra("permissions_denied", true);
//                        activity.startActivity(intent);
//                        activity.finish();
                    }
                })
                .create()
                .show();
    }


    public boolean isAllPermissionAvailable() {
        boolean isAllPermissionAvailable = true;
        initPermissions();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                isAllPermissionAvailable = false;
                break;
            }
        }
        return isAllPermissionAvailable;
    }

    public ArrayList<String> getdeniedPermissionsList() {
        ArrayList<String> list = new ArrayList<String>();
        for (String permission : requiredPermissions) {
            int result = ActivityCompat.checkSelfPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                list.add(permission);
            }
        }
        return list;
    }

    public boolean isPermissionAvailable(String permission) {
        boolean isPermissionAvailable = true;
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            isPermissionAvailable = false;
        }
        return isPermissionAvailable;
    }
}
