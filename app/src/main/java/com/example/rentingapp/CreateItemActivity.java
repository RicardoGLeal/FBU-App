package com.example.rentingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
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
    private Button previousBtn, nextBtn, pickImagesBtn;
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
        pickImagesBtn = findViewById(R.id.pickImagesBtn);

        layoutControlImages.setVisibility(LinearLayout.GONE);

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(CreateItemActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.categories));
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(categoriesAdapter);

        /*ivItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

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

        pickImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutControlImages.setVisibility(LinearLayout.VISIBLE);
                pickImagesIntent();
            }
        });

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

    private void pickImagesIntent() {
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
                    photoFiles.add(new ParseFile(new File(picturePath)));
                }
            }
        }

        String itemName = etItemName.getText().toString();
        String itemDescription = etItemDescription.getText().toString();
        String itemCategory = spinnerCategories.getSelectedItem().toString();
        Float itemPrice =  Float.valueOf(etPrice.getText().toString());

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