package com.example.rentingapp.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.rentingapp.Fragments.ProfileFragment;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.R;
import com.example.rentingapp.SignUpActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.rentingapp.Controllers.ActionsController.validateField;
import static com.example.rentingapp.Controllers.CustomAlertDialogs.errorDialog;
import static com.example.rentingapp.Controllers.CustomAlertDialogs.loadingDialog;
import static com.example.rentingapp.Controllers.CustomAlertDialogs.successDialog;
import static com.example.rentingapp.Controllers.PermissionsController.checkWriteExternalPermission;

public class CreateItemFragment extends Fragment {
    protected EditText etItemName, etItemDescription, etPrice;
    protected TextInputLayout tilItemName, tilDescription, tilPrice;
    protected Button btnCancel, btnCreate;
    protected Spinner spinnerCategories;
    protected ArrayAdapter<String> categoriesAdapter;

    //UI Views
    protected ImageSwitcher imagesIs;
    protected ImageButton previousBtn, nextBtn, btnAddImage;
    protected LinearLayout layoutControlImages;

    //store image uris in this array list
    protected ArrayList<Uri> imageUris;

    //request code to pick images
    public static final int PICK_IMAGES_CODE = 0;
    protected List<ParseFile> photoFiles;

    //position of selected image
    int position = 0;
    SweetAlertDialog loadingDialog, successDialog, errorDialog;

    public CreateItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Get References
        etItemName = view.findViewById(R.id.etItemName);
        etItemDescription = view.findViewById(R.id.etDescription);
        etPrice = view.findViewById(R.id.etPrice);
        tilItemName = view.findViewById(R.id.tilItemName);
        tilDescription = view.findViewById(R.id.tilDescription);
        tilPrice = view.findViewById(R.id.tilPrice);

        btnCancel = view.findViewById(R.id.btnCancel);
        btnCreate = view.findViewById(R.id.btnCreate);
        spinnerCategories = view.findViewById(R.id.spinnerCategories);
        layoutControlImages = view.findViewById(R.id.layoutControlImages);
        imagesIs = view.findViewById(R.id.imagesIs);
        previousBtn = view.findViewById(R.id.previousBtn);
        nextBtn = view.findViewById(R.id.nextBtn);
        btnAddImage = view.findViewById(R.id.btnAddImage);

        //Assign values
        layoutControlImages.setVisibility(LinearLayout.GONE);
        //Creates a new adapter for the categories spinner.
        categoriesAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.categories));
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(categoriesAdapter);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if all fields are filled
                int count = 0;
                if(validateField(tilItemName, etItemName))
                    count++;
                if(validateField(tilDescription, etItemDescription))
                    count++;
                if(validateField(tilPrice, etPrice))
                    count++;
                if (count == 3) {
                    if(imageUris.isEmpty()) {
                        errorDialog = errorDialog(getContext(), "Please add at least one image");
                        errorDialog.show();
                    }
                    else
                        CreateItem();
                } else
                {
                    errorDialog = errorDialog(getContext(), "Please verify that are the fields are filled");
                    errorDialog.show();
                }
            }
        });

        //init list
        imageUris = new ArrayList<>();
        photoFiles = new ArrayList<>();
        //setup image switcher
        imagesIs.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getContext());
                return imageView;
            }
        });

        //OnLongClickListener that is in charge of deleting images.
        imagesIs.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                imageUris.remove(position);
                if (!photoFiles.isEmpty())
                    photoFiles.remove(position);
                Toast.makeText(getContext(), "Image Deleted!", Toast.LENGTH_SHORT).show();
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
                            ImageView imageView = new ImageView(getContext());
                            return imageView;
                        }
                    });
                    layoutControlImages.setVisibility(LinearLayout.GONE);
                }
                return true;
            }
        });

        //OnClickListener for the Add Image Button.
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissions())
                    SelectPhotos();
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
                    Toast.makeText(getContext(), "No Previous Images", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "No More Images", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This functions is responsible for checking and requesting writing external storage permissions.
     * @return if they
     */
    private boolean checkPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            return false;
        }
        else
            return true;
    }

    /**
     * This function is responsible for creating an intent to the phone gallery to select images.
     */
    private void SelectPhotos() {
        layoutControlImages.setVisibility(LinearLayout.VISIBLE);
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(pickPhoto , PICK_IMAGES_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                }
                else {
                    //picked single image
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);
                }
                //set image to our image switcher
                imagesIs.setImageURI(imageUris.get(0));
                position = 0;
            }
        }
    }

    /**
     * This function is in charge of creating a new item and publishing it to the database
     */
    private void CreateItem() {
        //Creates Loading Dialog
        loadingDialog = loadingDialog(getContext());
        loadingDialog.show();

        if(photoFiles.isEmpty())
            photoFiles = new ArrayList<>();
        for (int i=0; i<imageUris.size(); i++) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            if (imageUris.get(i) != null) {
                Cursor cursor = getContext().getContentResolver().query(imageUris.get(i),
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
                        Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
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
        Item item = getItem();
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
                loadingDialog.dismissWithAnimation();
                if (e != null) {
                    errorDialog = errorDialog(getContext(), e.getMessage());
                    errorDialog.show();
                }
                else {
                    successDialog = successDialog(getContext(), "Item Created Successfully");
                    successDialog.show();
                    successDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    ProfileFragment f2 = new ProfileFragment(ParseUser.getCurrentUser());
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.flContainer, f2);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });
    }
    /**
     * Returns the new item object that is going to be created.
     * @return new item
     */
    protected Item getItem() {
        return new Item();
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
            SelectPhotos();
        else
            Toast.makeText(getContext(), "Permission DENIED!", Toast.LENGTH_SHORT).show();
    }
}