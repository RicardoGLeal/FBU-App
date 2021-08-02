package com.example.rentingapp.Controllers;

import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CustomAlertDialogs {

    /**
     * Creates an instance of a SweetAlertDialog implementing the PROGRESS_TYPE.
     * @param context context
     * @return
     */
    public static SweetAlertDialog loadingDialog(Context context) {
        SweetAlertDialog loadingDialog = new SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loadingDialog.setTitleText("Loading");
        loadingDialog.setCancelable(false);
        return loadingDialog;
    }

    /**
     * Creates an instance of a SweetAlertDialog implementing the SUCCESS_TYPE
     * @param context context.
     * @param contentText message to be desplayed.
     * @return
     */
    public static SweetAlertDialog successDialog(Context context, String contentText) {
        SweetAlertDialog successDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        successDialog.setTitleText("Success!");
        successDialog.setContentText(contentText);
        successDialog.setConfirmText("OK");
        successDialog.setCancelable(false);
        return successDialog;
    }

    public static SweetAlertDialog errorDialog(Context context, String contentText) {
        SweetAlertDialog successDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        successDialog.setTitleText("Error!");
        successDialog.setContentText(contentText);
        successDialog.setConfirmText("OK");
        successDialog.setCancelable(false);
        return successDialog;
    }
}
