package com.example.rentingapp.Controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.rentingapp.FullSizeImageActivity;
import com.example.rentingapp.Models.User;
import com.example.rentingapp.R;
import com.example.rentingapp.SignUpActivity;
import com.parse.ParseFile;

import java.io.File;
import java.io.IOException;

public class ImagesController {

    /**
     * Returns the File for a photo stored on disk given the fileName
     * @param fileName name of the file
     * @return a File object.
     */
    public static File getPhotoFileUri(String fileName, String TAG, Context context) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }


    public static Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

    /**
     * Sets an image with a rounded circle property, implementing glide.
     * @param context context of the activity where the image is.
     * @param image image that is going to load.
     * @param slot imageView where the image is going to be.
     */
    public static void loadCircleImage(Context context, ParseFile image, ImageView slot){
        RequestOptions circleProp = new RequestOptions();
        circleProp = circleProp.transform(new CircleCrop());
        Glide.with(context)
                .load(image != null ? image.getUrl() : R.drawable.profile_image_empty)
                .placeholder(R.drawable.profile_image_empty)
                .apply(circleProp)
                .into(slot);
    }

    /**
     * This function is responsible for scaling an image so that it is displayed in full size
     * @param photoUrl url of the photo
     * @param context context of the activity where the image is.
     * @param ivProfileImage imageView.
     */
    public static void openImage(String photoUrl, Context context, ImageView ivProfileImage) {
        Intent intent = new Intent(context, FullSizeImageActivity.class);
        // Pass data object in the bundle and populate details activity.
        intent.putExtra("photoUrl", photoUrl);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((AppCompatActivity) context, (View)ivProfileImage, "image");
        context.startActivity(intent, options.toBundle());
    }

    /**
     * Loads the image from the camera roll into the profile picture slot, using Glide.
     */
    public static void loadFileImage(File photoFile, ImageView ivProfileImage, Context context) {
        RequestOptions circleProp = new RequestOptions();
        circleProp = circleProp.transform(new CircleCrop());
        Glide.with(context)
                .load(photoFile)
                .apply(circleProp)
                .into(ivProfileImage);
    }

    /**
     * Loads the image taken from the camera into the profile picture slot, using Glide.
     */
    public static void loadTakenImage(Bitmap takenImage, ImageView imageView, Context context) {
        RequestOptions circleProp = new RequestOptions();
        circleProp = circleProp.transform(new CircleCrop());
        Glide.with(context)
                .load(takenImage)
                .apply(circleProp)
                .into(imageView);
    }

}
