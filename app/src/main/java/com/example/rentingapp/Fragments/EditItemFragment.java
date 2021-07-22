package com.example.rentingapp.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rentingapp.Models.Item;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class EditItemFragment extends CreateItemFragment{
    Item item;
    boolean editing;

    public EditItemFragment(boolean editing, Item item) {
        this.item = item;
        this.editing = true;
        photoFiles = new ArrayList<>();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindValues();
    }

    /**
     * Set the item's current information.
     */
    public void bindValues() {
        etItemName.setText(item.getTitle());
        etItemDescription.setText(item.getDescription());
        etPrice.setText(String.valueOf(item.getPrice()));
        spinnerCategories.setSelection(categoriesAdapter.getPosition(item.getCategory()));
        layoutControlImages.setVisibility(LinearLayout.VISIBLE);
        for (int i = 0; i < item.getImages().size(); i++) {
            photoFiles.add(item.getImages().get(i));
            //get image uri at specific index
            Uri imageUri = null;
            try {
                imageUri = Uri.fromFile(item.getImages().get(i).getFile());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            imageUris.add(imageUri);
        }
        //set first image to our image switcher
        imagesIs.setImageURI(imageUris.get(0));
        position = 0;
    }

    /**
     * Returns the current item that is going to be modified
     * @return current item
     */
    @Override
    public Item getItem() {
        return item;
    }
}
