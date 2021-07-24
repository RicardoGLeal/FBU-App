package com.example.rentingapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.rentingapp.Controllers.ImagesController;
import com.example.rentingapp.Models.User;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.parse.SaveCallback;

import static com.example.rentingapp.Controllers.ActionsController.getGeneralLocation;
import static com.example.rentingapp.Controllers.ActionsController.setUserValues;
import static com.example.rentingapp.Controllers.ActionsController.validateField;
import static com.example.rentingapp.Controllers.ImagesController.loadCircleImage;
import static com.example.rentingapp.Controllers.ImagesController.loadFileImage;
import static com.example.rentingapp.Controllers.ImagesController.loadTakenImage;
import static com.example.rentingapp.Controllers.PermissionsController.checkWriteExternalPermission;

public class SignUpActivity extends AppCompatActivity {
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1;

    public static final String TAG = "SignUpActivity";
    private File photoFile;
    public String photoFileName = "photo.jpg";
    LoadingDialog loadingDialog;

    TextInputLayout tilName, tilUsername, tilDescription, tilEmail, tilPassword;
    EditText etName, etUsername, etDescription, etEmail, etPassword;
    ImageView ivProfileImage;
    Button btnSignUp;
    String placeId, placeName, placeAddress, generalLocation;
    Double placeLat, placeLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        tilName = findViewById(R.id.tilName);
        tilUsername = findViewById(R.id.tilUsername);
        tilDescription = findViewById(R.id.tilDescription);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etDescription = findViewById(R.id.etDescription);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        btnSignUp = findViewById(R.id.btnSignUp);
        generalLocation = "";
        loadingDialog = new LoadingDialog(SignUpActivity.this);

        RequestOptions circleProp = new RequestOptions();
        circleProp = circleProp.transform(new CircleCrop());
        Glide.with(getBaseContext())
                .load(R.drawable.profile_image_empty)
                .apply(circleProp)
                .into(ivProfileImage);
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(SignUpActivity.this);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if all fields are filled
                int count = 0;
                if(validateField(tilName, etName))
                    count++;
                if(validateField(tilUsername, etUsername))
                    count++;
                if(validateField(tilPassword, etPassword))
                    count++;
                if(validateField(tilEmail, etEmail))
                    count++;
                if(validateField(tilDescription, etDescription))
                    count++;
                if (count == 5)
                    preCreateUser();
                else
                    Toast.makeText(SignUpActivity.this, "Please verify that are the fields are filled", Toast.LENGTH_SHORT).show();
            }
        });
        configurePlacesAPI();
    }
    /**
     * This function opens an AlertDialogFragment with the options that the user has to set their profile picture.
     * @param context
     */
    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                String value = options[item].toString();
                switch (value) {
                    case "Take Photo":
                        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        // Create a File reference for future access
                        photoFile = ImagesController.getPhotoFileUri(photoFileName, TAG, SignUpActivity.this);
                        Uri fileProvider = FileProvider.getUriForFile(SignUpActivity.this, "com.codepath.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                        // So as long as the result is not null, it's safe to use the intent.
                        if (takePictureIntent.resolveActivity(SignUpActivity.this.getPackageManager()) != null) {
                            // Start the image capture intent to take photo
                            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                        }
                        break;
                    case "Choose from Gallery":
                        if (checkWriteExternalPermission(getApplicationContext()))
                            PickPhoto();
                        else
                            ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                        break;
                    case "Cancel":
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }

    /**
     * Calls an implicit intent to the camera roll, where the user can pick a photo.
     */
    private void PickPhoto() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    /**
     * Initializes the Google PLaces API, which has a listener for when the user types an address.
     */
    private void configurePlacesAPI() {
        //Initialize the SDK
        Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_MAPS_API_KEY);

        //Create a new PLaces client instance
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);

        autocompleteFragment.setCountries("US", "MX");

        //Specify the types of place data to return
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.ADDRESS_COMPONENTS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Get info about the selected place.
                //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                placeId = place.getId();
                placeLat = place.getLatLng().latitude;
                placeLng = place.getLatLng().longitude;
                placeName = place.getName();
                placeAddress = place.getAddress();
                generalLocation = getGeneralLocation(place);

            }
            @Override
            public void onError(@NonNull Status status) {
                //Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    /**
     * Called when the user clicks the SignUp button. Creates a ParseFile for the photo and goes to CreateAccount.
     */
    private void preCreateUser() {
        loadingDialog.startLoadingDialog();
        if (photoFile != null) {
            ParseFile photo = new ParseFile(photoFile);
            photo.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(getApplicationContext(), "Error while saving"+e, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                        CreateAccount(photo);
                }
            });
        }
        else
            CreateAccount(null);
    }

    /**
     * Creates the user and sends it to the Parse Database
     * @param photo profile picture
     */
    private void CreateAccount(ParseFile photo) {
        ParseUser user = new ParseUser();
        setUserValues(user, etName.getText().toString(), etUsername.getText().toString(),
                etPassword.getText().toString(), etEmail.getText().toString(), photoFile, photo,
                etDescription.getText().toString(), placeId, placeName, placeAddress, placeLat, placeLng, generalLocation);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving"+e);
                    Toast.makeText(SignUpActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(SignUpActivity.this, "User created Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                loadingDialog.dismissDialog();
            }
        });
    }

    /**
     * Called when the user selects a Photo from their Gallery or after taking a new one with the camera.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: //TAKE PICTURE
                    if (resultCode == RESULT_OK) { //If the image took the picture..
                        // by this point we have the camera photo on disk
                        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        // with this we are sure of the correct orientation.
                        takenImage = ImagesController.rotateBitmapOrientation(photoFile.getAbsolutePath());
                        // Load the taken image into a preview
                        loadTakenImage(takenImage, ivProfileImage, getApplicationContext());
                    } else { // Result was a failure
                        Toast.makeText(SignUpActivity.this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case SELECT_IMAGE_ACTIVITY_REQUEST_CODE: //CHOOSE PICTURE
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                photoFile = new File(picturePath);
                                loadFileImage(photoFile, ivProfileImage, getApplicationContext());
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }

    /**
     * This function is called when the user accepted or rejected the permission of writing in the
     * external storage.
     * @param requestCode permission's request code
     * @param permissions permissions requested
     * @param grantResults result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            PickPhoto();
        else
            Toast.makeText(SignUpActivity.this, "Permission DENIED!", Toast.LENGTH_SHORT).show();
    }
}
