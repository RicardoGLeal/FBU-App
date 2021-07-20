package com.example.rentingapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.rentingapp.Controllers.ActionsController;
import com.example.rentingapp.Fragments.ItemDetailsFragment;
import com.example.rentingapp.Fragments.ProfileFragment;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.Models.User;
import com.example.rentingapp.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.example.rentingapp.Controllers.ActionsController.getDistanceInKm;
import static com.example.rentingapp.GooglePlacesClient.Initialize;
import static com.example.rentingapp.GooglePlacesClient.placesClient;

/**
 * This adapter is implemented by the RecyclerView of the Item's Feed.
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Item> items, allItems, itemsFiltered;
    public static final String TAG = "ItemsAdapter";

    public ItemsAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
        this.itemsFiltered = new ArrayList<>(items);
        this.allItems = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {

        //run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Item> filteredList = new ArrayList<>();

            //filter for when searching an item by title
            if(constraint.toString().isEmpty())
            {
                if(allItems.isEmpty())
                    allItems = new ArrayList<>(items);
                filteredList.addAll(allItems);
            }
            else {
                for (Item item : allItems) {
                    if (item.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        //runs on a ui thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            items.clear();
            items.addAll((Collection<? extends Item>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivProfilePicture, ivItemImage;
        TextView tvItemName, tvOwnersName, tvCategory, tvDescription, tvPrice, tvLocation, tvDistance, tvPostDate;
        String placeId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Get references
            ivProfilePicture = itemView.findViewById(R.id.ivProfileImage);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvOwnersName = itemView.findViewById(R.id.tvOwnersName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvPostDate = itemView.findViewById(R.id.tvPostDate);
            itemView.setOnClickListener(this);
        }

        public void bind(Item item) {
            //Assign values
            ParseFile profilePicture = item.getOwner().getParseFile("profilePicture");
            RequestOptions circleProp = new RequestOptions();
            circleProp = circleProp.transform(new CircleCrop());
            Glide.with(context)
                    .load(profilePicture != null ? profilePicture.getUrl() : R.drawable.profile_image_empty)
                    .placeholder(R.drawable.profile_image_empty)
                    .apply(circleProp)
                    .into(ivProfilePicture);

            List<ParseFile> images = item.getImages();

            Glide.with(context)
                    .load(images.get(0).getUrl())
                    .placeholder(R.drawable.profile_image_empty)
                    .into(ivItemImage);
            tvItemName.setText(item.getTitle());
            tvOwnersName.setText(item.getOwner().getString(User.KEY_NAME));
            tvCategory.setText(item.getCategory());
            tvDescription.setText(item.getDescription());
            tvPrice.setText(String.valueOf(item.getPrice()));
            tvPostDate.setText(ActionsController.getRelativeTimeAgo(item.getCreatedAt().toString()));
            placeId = item.getOwner().getString(User.KEY_PLACE_ID);
            tvLocation.setText(item.getOwner().getString(User.KEY_GENERAL_LOCATION));
            tvDistance.setText(item.getDistance() +" Km away");

            tvOwnersName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProfile(item);
                }
            });

            ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProfile(item);
                }
            });
        }

        /**
         * Opens ItemDetailsFragment with the details of the item clicked.
         * @param v
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                AppCompatActivity activity = (AppCompatActivity) context;
                Fragment fragment = new ItemDetailsFragment(items.get(position));
                ((AppCompatActivity) context).getSupportFragmentManager();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        }

        /**
         * Function that goes to the profile of the user clicked.
         * @param item item opened
         */
        private void goToProfile(Item item) {
            AppCompatActivity activity = (AppCompatActivity) context;
            Fragment fragment = new ProfileFragment(item.getOwner());
            activity.getSupportFragmentManager();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
        }
    }
}
