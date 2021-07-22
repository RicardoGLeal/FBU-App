package com.example.rentingapp.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentingapp.BuildConfig;
import com.example.rentingapp.Controllers.ImagesController;
import com.example.rentingapp.Models.User;
import com.example.rentingapp.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.rentingapp.Controllers.ActionsController.getGeneralLocation;
import static com.example.rentingapp.Controllers.ActionsController.setUserValues;
import static com.example.rentingapp.Controllers.ActionsController.validateField;
import static com.example.rentingapp.Controllers.ImagesController.loadCircleImage;
import static com.example.rentingapp.Controllers.ImagesController.loadFileImage;
import static com.example.rentingapp.Controllers.ImagesController.loadTakenImage;
import static com.example.rentingapp.Controllers.PermissionsController.checkWriteExternalPermission;


public class EditProfileDialogFragment extends DialogFragment {
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private static final int PLACES_AUTOCOMPLETE_REQUEST_CODE = 3;

    public static final String TAG = "EditProfileDlgFragment";
    private File photoFile;
    public String photoFileName = "photo.jpg";

    private ParseUser user;
    TextInputLayout tilName, tilUsername, tilDescription, tilEmail, tilPassword;
    EditText etName, etUsername, etDescription, etEmail, etPassword;
    TextView tvLocationName;
    ImageView ivProfileImage;
    Button btnUpdateProfile;
    String placeId, placeName, placeAddress, generalLocation;
    Double placeLat, placeLng;

    public EditProfileDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new Dialog Fragment to update the profile of the current user.
     * @param user the user that is logged in.
     * @return
     */
    public static EditProfileDialogFragment newInstance(ParseUser user) {
        EditProfileDialogFragment frag = new EditProfileDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = getArguments().getParcelable("user");
        tilName = view.findViewById(R.id.tilName);
        tilUsername = view.findViewById(R.id.tilUsername);
        tilDescription = view.findViewById(R.id.tilDescription);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);

        etName = view.findViewById(R.id.etName);
        etUsername = view.findViewById(R.id.etUsername);
        etDescription = view.findViewById(R.id.etDescription);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        tvLocationName = view.findViewById(R.id.tvLocationName);

        bindValues();

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(getContext());
            }
        });

        tvLocationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configurePlacesAPI();
            }
        });
        
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
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
                    preUpdateUser();
                else
                    Toast.makeText(getContext(), "Please verify that are the fields are filled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preUpdateUser() {
        if (photoFile != null) {
            ParseFile photo = new ParseFile(photoFile);
            photo.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(getContext(), "Error while saving"+e, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                        UpdateAccount(photo);
                }
            });
        }
        else
            UpdateAccount(null);
    }

    /**
     * Creates the user and sends it to the Parse Database
     * @param photo profile picture
     */
    private void UpdateAccount(ParseFile photo) {
        ParseUser user = ParseUser.getCurrentUser();
        if(user != null) {
            setUserValues(user, etName.getText().toString(), etUsername.getText().toString(),
                    etPassword.getText().toString(), etEmail.getText().toString(), photoFile, photo,
                    etDescription.getText().toString(), placeId, placeName, placeAddress, placeLat, placeLng, generalLocation);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while updating" + e);
                        Toast.makeText(getContext(), "Error while updating", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(getContext(), "User updated Successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                        ProfileFragment f2 = new ProfileFragment(ParseUser.getCurrentUser());
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.flContainer, f2);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            });
        }
    }

    /**
     * Initializes the Google PLaces API, which has a listener for when the user types an address.
     */
    private void configurePlacesAPI() {
        //Initialize the SDK
        Places.initialize(getContext(), BuildConfig.GOOGLE_MAPS_API_KEY);

        //Create a new PLaces client instance
        PlacesClient placesClient = Places.createClient(getContext());

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.ADDRESS_COMPONENTS);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(getContext());
        startActivityForResult(intent, PLACES_AUTOCOMPLETE_REQUEST_CODE);
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
                if (options[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    // Create a File reference for future access
                    photoFile = ImagesController.getPhotoFileUri(photoFileName, TAG, getContext());
                    Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                    // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                    // So as long as the result is not null, it's safe to use the intent.
                    if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                        // Start the image capture intent to take photo
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * Set the user's current information.
     */
    private void bindValues() {
        loadCircleImage(getContext(), user.getParseFile(User.KEY_PROFILE_PICTURE), ivProfileImage);
        etName.setText(user.getString(User.KEY_NAME));
        etUsername.setText(user.getUsername());
        etDescription.setText(user.getString(User.KEY_DESCRIPTION));
        etEmail.setText(user.getEmail());
        tvLocationName.setText(user.getString(User.KEY_PLACE_NAME));
        placeId = user.getString(User.KEY_PLACE_ID);
        placeName = user.getString(User.KEY_PLACE_NAME);
        placeAddress = user.getString(User.KEY_PLACE_ADDRESS);
        placeLng = User.getLatLng(user).longitude;
        placeLat = User.getLatLng(user).latitude;
        generalLocation = user.getString(User.KEY_GENERAL_LOCATION);
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
                case PLACES_AUTOCOMPLETE_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        Place place = Autocomplete.getPlaceFromIntent(data);
                        //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                        placeId = place.getId();
                        placeLat = place.getLatLng().latitude;
                        placeLng = place.getLatLng().longitude;
                        placeName = place.getName();
                        placeAddress = place.getAddress();
                        generalLocation = getGeneralLocation(place);
                        tvLocationName.setText(placeName);

                    } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                        //Handle the error.
                        Status status = Autocomplete.getStatusFromIntent(data);
                        Log.i(TAG, status.getStatusMessage());
                    } else if (resultCode == RESULT_CANCELED) {
                        // The user canceled the operation.
                    }
                    break;
                case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: //TAKE PICTURE
                    if (resultCode == RESULT_OK) { //If the image took the picture..
                        // by this point we have the camera photo on disk
                        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        // with this we are sure of the correct orientation.
                        takenImage = ImagesController.rotateBitmapOrientation(photoFile.getAbsolutePath());
                        // Load the taken image into a preview
                        loadTakenImage(takenImage, ivProfileImage, getContext());
                    } else { // Result was a failure
                        Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case SELECT_IMAGE_ACTIVITY_REQUEST_CODE: //CHOOSE PICTURE
                    if (resultCode == RESULT_OK && data != null) {
                        checkWriteExternalPermission(getContext());
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContext().getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);

                                photoFile = new File(picturePath);
                                loadFileImage(photoFile, ivProfileImage, getContext());
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }


}