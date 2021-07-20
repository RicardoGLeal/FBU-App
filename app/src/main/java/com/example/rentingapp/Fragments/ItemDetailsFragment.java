package com.example.rentingapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.rentingapp.Adapters.ItemImagesAdapter;
import com.example.rentingapp.Controllers.ActionsController;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.Models.User;
import com.example.rentingapp.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import static com.example.rentingapp.Controllers.ActionsController.getDistanceInKm;
import static com.example.rentingapp.Controllers.ActionsController.getRelativeTimeAgo;

public class ItemDetailsFragment extends Fragment {
    public static final String TAG = "ItemDetailsFragment";
    private Item item;
    private List<ParseFile> allImages;
    private ImageView ivMainItemImage, ivProfileImage;
    private RecyclerView rvItemImages;
    private TextView tvItemName, tvOwnersName, tvDescription, tvCategory, tvPrice, tvLocation, tvDistance, tvPostDate;
    private Button btnRentItem;
    private ItemImagesAdapter adapter;

    //Constructor that receives a item.
    public ItemDetailsFragment(Item item) {
        this.item = item;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //get references
        super.onViewCreated(view, savedInstanceState);
        ivMainItemImage = view.findViewById(R.id.ivMainItemImage);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        rvItemImages = view.findViewById(R.id.rvItemImages);
        tvItemName = view.findViewById(R.id.tvItemName);
        tvOwnersName = view.findViewById(R.id.tvOwnersName);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvCategory = view.findViewById(R.id.tvCategory);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvPostDate = view.findViewById(R.id.tvPostDate);
        btnRentItem = view.findViewById(R.id.btnRentItem);

        // Hide rent button if the item is yours.
        if(ParseUser.getCurrentUser().getObjectId().equals(item.getOwner().getObjectId()))
            btnRentItem.setVisibility(Button.GONE);

        //set values
        loadMainItemImage();
        loadProfilePicture();
        tvItemName.setText(item.getTitle());
        tvOwnersName.setText(item.getOwner().getString(User.KEY_NAME));
        tvDescription.setText(item.getDescription());
        tvCategory.setText(item.getCategory());
        tvPrice.setText(String.valueOf(item.getPrice()));
        tvLocation.setText(item.getOwner().getString(User.KEY_GENERAL_LOCATION));
        tvDistance.setText(String.valueOf(getDistanceInKm(item, ParseUser.getCurrentUser())));
        tvPostDate.setText(getRelativeTimeAgo(item.getCreatedAt().toString()));
        allImages = item.getImages();
        allImages.remove(0);

        adapter = new ItemImagesAdapter(getContext(), allImages);
        rvItemImages.setAdapter(adapter);
        rvItemImages.setLayoutManager(new GridLayoutManager(getContext(),3));

        btnRentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowRentItemDialogFragment();
            }
        });

        tvOwnersName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile(item);
            }
        });

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile(item);
            }
        });
    }

    /**
     * Opens and creates the DialogFragment when Renting an Item.
     */
    private void ShowRentItemDialogFragment() {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentManager fm = activity.getSupportFragmentManager();
        RentItemDialogFragment rentItemDialogFragment = RentItemDialogFragment.newInstance(item);
        rentItemDialogFragment.show(fm,"fragment_rent_item");
    }

    /**
     * Loads the Main Item's Image.
     */
    private void loadMainItemImage() {
        Glide.with(getContext())
                .load(item.getImages().get(0).getUrl())
                .placeholder(R.drawable.profile_image_empty)
                .into(ivMainItemImage);
    }

    /**
     * Loads the Profile Picture of the item's owner.
     */
    private void loadProfilePicture() {
        RequestOptions circleProp = new RequestOptions();
        circleProp = circleProp.transform(new CircleCrop());
        ParseFile profilePicture = item.getOwner().getParseFile(User.KEY_PROFILE_PICTURE);
        Glide.with(getContext())
                .load(profilePicture!=null?profilePicture.getUrl(): R.drawable.profile_image_empty)
                .placeholder(R.drawable.profile_image_empty)
                .apply(circleProp)
                .into(ivProfileImage);
    }

    /**
     * Function that goes to the profile of the user clicked.
     * @param item item opened
     */
    private void goToProfile(Item item) {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        Fragment fragment = new ProfileFragment(item.getOwner());
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
    }
}