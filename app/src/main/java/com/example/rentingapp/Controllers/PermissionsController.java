package com.example.rentingapp.Controllers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsController {

    /**
     * Verifies is the user has already accepted the write external permission, if not, an Dialog Fragment
     * appears requesting it.
     *
     * @param context
     */
    public static boolean checkWriteExternalPermission(Context context) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
            return false;
            //ActivityCompat.requestPermissions((AppCompatActivity)context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        else
            return true;
    }
}

    /**
     * This functions is responsible for checking and requesting writing external storage permissions.
     * @return if they
     */
