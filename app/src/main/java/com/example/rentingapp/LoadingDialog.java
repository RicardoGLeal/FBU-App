package com.example.rentingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingDialog {

    Activity activity;
    AlertDialog dialog;

    LoadingDialog(Activity myActivity) {
        activity = myActivity;
    }

    /**
     * Invokes the loading dialog fragment
     */
    void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    /**
     * Dismisses the loading dialog fragment
     */
    void dismissDialog() {
        dialog.dismiss();
    }
}
