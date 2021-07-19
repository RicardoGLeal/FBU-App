package com.example.rentingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.rentingapp.Models.Item;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateItemActivity extends AppCompatActivity {
    EditText etItemName, etItemDescription, etPrice;
    Button btnCancel, btnCreate;
    Spinner spinnerCategories;

    //UI Views
    private ImageSwitcher imagesIs;
    private Button previousBtn, nextBtn, btnAddImage;
    private LinearLayout layoutControlImages;

    //store image uris in this array list
    private ArrayList<Uri> imageUris;

    //request code to pick images
    public static final int PICK_IMAGES_CODE = 0;

    //position of selected image
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        //Get References
        etItemName = findViewById(R.id.etItemName);
        etItemDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        btnCancel = findViewById(R.id.btnCancel);
        btnCreate = findViewById(R.id.btnCreate);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        layoutControlImages = findViewById(R.id.layoutControlImages);
        imagesIs = findViewById(R.id.imagesIs);
        previousBtn = findViewById(R.id.previousBtn);
        nextBtn = findViewById(R.id.nextBtn);
        btnAddImage = findViewById(R.id.btnAddImage);

        //Assign values
        layoutControlImages.setVisibility(LinearLayout.GONE);
        //Creates a new adapter for the categories spinner.
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(CreateItemActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.categories));
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(categoriesAdapter);
        
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateItem();
            }
        });

        //init list
        imageUris = new ArrayList<>();

        //setup image switcher
        imagesIs.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });

        //OnLongClickListener that is in charge of deleting images. 
        imagesIs.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                imageUris.remove(position);
                Toast.makeText(CreateItemActivity.this, "Image Deleted!", Toast.LENGTH_SHORT).show();
                if(position > 0) {
                    position--;
                }
                if (!imageUris.isEmpty())
                    imagesIs.setImageURI(imageUris.get(position));
                else {
                    imagesIs.removeAllViews();
                    imagesIs.setFactory(new ViewSwitcher.ViewFactory() {
                        @Override
                        public View makeView() {
                            ImageView imageView = new ImageView(getApplicationContext());
                            return imageView;
                        }
                    });
                }
                return true;
            }
        });

        //OnClickListener for the Add Image Button.
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutControlImages.setVisibility(LinearLayout.VISIBLE);
                pickImagesIntent();
            }
        });

        //OnClickListener for the previous button. Loads the previous image
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position > 0) {
                    position--;
                    imagesIs.setImageURI(imageUris.get(position));
                }
                else {
                    Toast.makeText(CreateItemActivity.this, "No Previous Images", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //OnClickListener for the next button. Loads the next image
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < imageUris.size() - 1) {
                    position++;
                    imagesIs.setImageURI(imageUris.get(position));
                }
                else {
                    Toast.makeText(CreateItemActivity.this, "No More Images", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This function is responsible for creating an intent to the phone gallery to select images. It also requests permits, if they have not been granted before
     */
    private void pickImagesIntent() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(pickPhoto , PICK_IMAGES_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if(data.getClipData() != null) {
                    //picked multiple images
                    int count = data.getClipData().getItemCount();
                    for (int i = count-1; i>=0; i--) {
                        //get image uri at specific index
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                    }
                    //set first image to our image switcher
                    imagesIs.setImageURI(imageUris.get(0));
                    position = 0;
                }
                else {
                    //picked single image
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);
                    //set image to our image switcher
                    imagesIs.setImageURI(imageUris.get(0));
                    position = 0;
                }
            }
        }
    }

    /**
     * This function is in charge of creating a new item and publishing it to the database
     */
    private void CreateItem() {
        List<ParseFile> photoFiles = new ArrayList<>();
        for (int i=0; i<imageUris.size(); i++) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            if (imageUris.get(i) != null) {
                Cursor cursor = getContentResolver().query(imageUris.get(i),
                        filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    ParseFile parseFile = new ParseFile(new File(picturePath));
                    photoFiles.add(new ParseFile(new File(picturePath)));
                }
            }
        }

        //Saves all the image into Parse
        for (int i = 0; i<photoFiles.size(); i++) {
            photoFiles.get(i).saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(CreateItemActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }

        //Some values are obtained
        String itemName = etItemName.getText().toString();
        String itemDescription = etItemDescription.getText().toString();
        String itemCategory = spinnerCategories.getSelectedItem().toString();
        Float itemPrice =  Float.valueOf(etPrice.getText().toString());


        //An item is created and its parameters are assigned
        Item item = new Item();
        item.setTitle(itemName);
        item.setDescription(itemDescription);
        item.setCategory(itemCategory);
        item.setOwner(ParseUser.getCurrentUser());
        item.setIsRented(false);
        item.setPrice(itemPrice);
        item.setImages(photoFiles);
        item.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(CreateItemActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CreateItemActivity.this, "Item Created Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateItemActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}