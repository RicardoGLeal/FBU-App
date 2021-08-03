package com.example.rentingapp.Controllers;

import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CustomAlertDialogs {

    /**
     * Creates an instance of a SweetAlertDialog implementing the PROGRESS_TYPE which is shown when
     * something is loading.
     * @param context context
     * @return loadingDialog
     */
    public static SweetAlertDialog loadingDialog(Context context) {
        SweetAlertDialog loadingDialog = new SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loadingDialog.setTitleText("Loading");
        loadingDialog.setCancelable(false);
        return loadingDialog;
    }

    /**
     * Creates an instance of a SweetAlertDialog implementing the SUCCESS_TYPE which is used to show
     * successes to the user.
     * @param context context.
     * @param contentText message to be displayed.
     * @return successDialog
     */
    public static SweetAlertDialog successDialog(Context context, String contentText) {
        SweetAlertDialog successDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        successDialog.setTitleText("Success!");
        successDialog.setContentText(contentText);
        successDialog.setConfirmText("OK");
        successDialog.setCancelable(false);
        return successDialog;
    }

    /**
     * Creates an instance of a SweetAlertDialog implementing the ERROR_TYPE which is used to show
     * errors to the user.
     * @param context context.
     * @param contentText message to be displayed.
     * @return errorDialog
     */
    public static SweetAlertDialog errorDialog(Context context, String contentText) {
        SweetAlertDialog successDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        successDialog.setTitleText("Error!");
        successDialog.setContentText(contentText);
        successDialog.setConfirmText("OK");
        successDialog.setCancelable(false);
        return successDialog;
    }

    /**
     * Creates an instance of a SweetAlertDialog implementing the WARNING_TYPE which is used to ask
     * the user for confirmations or show they warnings.
     * @param context context.
     * @param contentText message to be displayed.
     * @return errorDialog
     */
    public static SweetAlertDialog confirmDialog(Context context, String contentText) {
        SweetAlertDialog successDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        successDialog.setTitleText("Are you sure?");
        successDialog.setContentText(contentText);
        successDialog.setConfirmText("YES");
        successDialog.setCancelable(false);
        successDialog.setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                successDialog.dismissWithAnimation();
            }
        });
        return successDialog;
    }
}
