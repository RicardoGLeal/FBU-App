package com.example.rentingapp.Controllers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsController {

    /**
     * Verifies is the user has already accepted the write external permission, returns true or false
     * depending the case.
     * @param context
     */
    public static boolean checkWriteExternalPermission(Context context) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
            return false;
        else
            return true;
    }
}
