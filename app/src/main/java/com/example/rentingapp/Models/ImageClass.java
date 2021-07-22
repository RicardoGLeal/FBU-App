package com.example.rentingapp.Models;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.rentingapp.Controllers.ImagesController;
import com.example.rentingapp.SignUpActivity;

import java.io.File;

public abstract class ImageClass {
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1;

    public static final String TAG = "SignUpActivity";
    private File photoFile;
    public String photoFileName = "photo.jpg";
    /**
     * This function opens an AlertDialogFragment with the options that the user has to set their profile picture.
     * @param context
     */
    public void selectdImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");
        AppCompatActivity activity = (AppCompatActivity) context;
        builder.setItems(options, new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    // Create a File reference for future access
                    photoFile = ImagesController.getPhotoFileUri(photoFileName, TAG, activity);
                    Uri fileProvider = FileProvider.getUriForFile(activity, "com.codepath.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                    // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                    // So as long as the result is not null, it's safe to use the intent.
                    if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                        // Start the image capture intent to take photo
                        activity.startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activity.startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}
