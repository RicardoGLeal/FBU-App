package com.example.rentingapp.Models;

import android.widget.ImageButton;

import com.example.rentingapp.R;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("SavedItem")
public class SavedItem extends ParseObject {
    public static final String KEY_ITEM = "item";
    public static final String KEY_USER = "user";

    public void setItem(Item item) {
        put(KEY_ITEM, item);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Item getItem() {
        return (Item) getParseObject(KEY_ITEM);
    }

    /**
     * This function checks if an item is in the WishList of the logged-in user. If so, changes
     * the save background drawable and sets the item saved.
     * @param item item to check
     * @param iBtnSaveItem save button
     */
    public static void CheckIfInWishList(Item item, ImageButton iBtnSaveItem) {
        ParseQuery<SavedItem> query = ParseQuery.getQuery(SavedItem.class);
        query.whereEqualTo(KEY_ITEM, item);
        query.whereEqualTo(KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<SavedItem>() {
            @Override
            public void done(List<SavedItem> objects, ParseException e) {
                if (e == null)
                    if(!objects.isEmpty()) {
                        iBtnSaveItem.setBackgroundResource(R.drawable.ufi_save_active);
                        item.setSaved();
                    }
            }
        });
    }
}
