package com.example.rentingapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.Models.User;
import com.example.rentingapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.parse.ParseFile;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private Context context;
    private List<Item> items;

    public ItemsAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
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

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePicture, ivItemImage;
        TextView tvItemName, tvOwnersName, tvCategory, tvDescription, tvPrice, tvLocation, tvDistance, tvPostDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
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
        }

        public void bind(Item item) {
            ParseFile profilePicture = item.getOwner().getParseFile("profilePicture");
            RequestOptions circleProp = new RequestOptions();
            circleProp = circleProp.transform(new CircleCrop());
            Glide.with(context)
                    .load(profilePicture!=null?profilePicture.getUrl(): R.drawable.profile_image_empty)
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
        }

        public void getPlace() {
            
        }
    }
}
