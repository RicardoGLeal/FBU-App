    package com.example.rentingapp;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.FileProvider;

    import android.Manifest;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.database.Cursor;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.graphics.Picture;
    import android.net.Uri;
    import android.os.Bundle;
    import android.os.Environment;
    import android.provider.MediaStore;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.ProgressBar;
    import android.widget.Toast;

    import com.bumptech.glide.Glide;
    import com.bumptech.glide.load.resource.bitmap.CircleCrop;
    import com.bumptech.glide.request.RequestOptions;
    import com.example.rentingapp.Models.Location;
    import com.example.rentingapp.Models.User;
    import com.google.android.gms.common.api.Status;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.libraries.places.api.Places;
    import com.google.android.libraries.places.api.model.Place;
    import com.google.android.libraries.places.api.model.RectangularBounds;
    import com.google.android.libraries.places.api.model.TypeFilter;
    import com.google.android.libraries.places.api.net.PlacesClient;
    import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
    import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
    import com.parse.ParseException;
    import com.parse.ParseFile;
    import com.parse.ParseUser;
    import com.parse.PointerEncoder;
    import com.parse.SaveCallback;
    import com.parse.SignUpCallback;

    import java.io.File;
    import java.io.IOException;
    import java.io.InputStream;
    import java.net.MalformedURLException;
    import java.net.URL;
    import java.util.Arrays;

    import pub.devrel.easypermissions.EasyPermissions;

    import static com.example.rentingapp.Controllers.rotateBitmapOrientation;

    //import pub.devrel.easypermissions.EasyPermissions;

    public class SignUpActivity extends AppCompatActivity {
        public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
        public static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1;

        public static final String TAG = "SignUpActivity";
        public static final int PICK_IMAGE = 1;
        private File photoFile;
        public String photoFileName = "photo.jpg";

        EditText etUsername, etDescription, etEmail, etPassword, etCountry, etCity, etZIP;
        ImageView ivProfileImage;
        Button btnSignUp;
        String placeId;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sign_up);
            etUsername = findViewById(R.id.etUsername);
            etDescription = findViewById(R.id.etDescription);
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            //etCountry = findViewById(R.id.etCountry);
            //etCity = findViewById(R.id.etCity);
            //etZIP = findViewById(R.id.etZIP);
            ivProfileImage = findViewById(R.id.ivProfileImage);
            btnSignUp = findViewById(R.id.btnSignUp);

            RequestOptions circleProp = new RequestOptions();
            circleProp = circleProp.transform(new CircleCrop());
            Glide.with(getBaseContext())
                    .load(R.drawable.profile_image_empty)
                    .apply(circleProp)
                    .into(ivProfileImage);
            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //galleryPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    selectImage(SignUpActivity.this);
                }
            });

            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signUpUser();
                }
            });

            //Initialize the SDK
            String apiKey = "AIzaSyBFLxnsiBxqmS0xNYg7mCC4mbcVZI-bFbw";
            Places.initialize(getApplicationContext(), apiKey);

            //Create a new PLaces client instance
            PlacesClient placesClient = Places.createClient(this);

            // Initialize the AutocompleteSupportFragment.
            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

            autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);

            autocompleteFragment.setCountries("US");

            //Specify the types of place data to return
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // TODO: Get info about the selected place.
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                    placeId = place.getId();
                }
                @Override
                public void onError(@NonNull Status status) {
                    // TODO: Handle the error.
                    Log.i(TAG, "An error occurred: " + status);
                }
            });
        }

        private void signUpUser() {
            //String country = etCountry.getText().toString();
            //String city = etCity.getText().toString();
            //int ZIP = Integer.parseInt(etZIP.getText().toString());


           /* Location location = new Location();
            location.setCountry(country);
            location.setCity(city);
            location.setZIP(ZIP);
            location.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving, e");
                        Toast.makeText(SignUpActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                    }
                    Log.i(TAG, "Post save was successful!");*/


            if (photoFile != null) {
                ParseFile photo = new ParseFile(photoFile);
                photo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(SignUpActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else
                        {
                            CreateAccount(photo);
                        }
                    }
                });
            }
        }

        private void CreateAccount(ParseFile photo) {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            String description = etDescription.getText().toString();
            String email = etEmail.getText().toString();

            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            if (photoFile != null)
                user.put(User.KEY_PROFILE_PICTURE, photo);
            user.put(User.KEY_DESCRIPTION, description);
            user.put(User.KEY_PLACE_ID, placeId);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving, e");
                        Toast.makeText(SignUpActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(SignUpActivity.this, "User created Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }


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
                        photoFile = getPhotoFileUri(photoFileName);
                        Uri fileProvider = FileProvider.getUriForFile(SignUpActivity.this, "com.codepath.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                        // So as long as the result is not null, it's safe to use the intent.
                        if (takePictureIntent.resolveActivity(SignUpActivity.this.getPackageManager()) != null) {
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

        // Returns the File for a photo stored on disk given the fileName
        public File getPhotoFileUri(String fileName) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(SignUpActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
            }
            // Return the file target for the photo based on filename
            return new File(mediaStorageDir.getPath() + File.separator + fileName);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode != RESULT_CANCELED) {
                switch (requestCode) {
                    case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                            if (resultCode == RESULT_OK) { //If the image took the picture..
                                // by this point we have the camera photo on disk
                                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                                // with this we are sure of the correct orientation.
                                takenImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
                                // Load the taken image into a preview
                                RequestOptions circleProp = new RequestOptions();
                                circleProp = circleProp.transform(new CircleCrop());
                                Glide.with(getBaseContext())
                                        .load(takenImage)
                                        .apply(circleProp)
                                        .into(ivProfileImage);
                            } else { // Result was a failure
                                Toast.makeText(SignUpActivity.this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                            }

                        break;
                    case SELECT_IMAGE_ACTIVITY_REQUEST_CODE:
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
                                    RequestOptions circleProp = new RequestOptions();
                                    circleProp = circleProp.transform(new CircleCrop());
                                    /*Glide.with(getBaseContext())
                                            .load(BitmapFactory.decodeFile(picturePath))
                                            .apply(circleProp)
                                            .into(ivProfileImage);*/
                                    //ivProfileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                    try ( InputStream is = new URL(picturePath).openStream() ) {
                                        Bitmap bitmap = BitmapFactory.decodeStream( is );
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    ivProfileImage.setImageURI(selectedImage);
                                    photoFile = new File(String.valueOf(selectedImage));
                                    cursor.close();
                                }
                            }
                        }
                        break;
                }
            }
        }
    }
