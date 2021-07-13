package com.example.rentingapp.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Item")
public class Item extends ParseObject {
    public static final String KEY_TITLE = "title";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGES = "images";
    public static final String KEY_IS_RENTED = "isRented";
    public static final String KEY_CREATED_KEY = "createdAt";
    public static final String KEY_PRICE = "price";

    public void setOwner(ParseUser owner) {
        put(KEY_OWNER, owner);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setImages(List<ParseFile> images) {
        put(KEY_IMAGES, images);
    }

    public void setIsRented(Boolean value) {
        put(KEY_IS_RENTED, value);
    }

    public void setPrice(float price){
        put(KEY_PRICE, price);
    }
}
