package com.example.rentingapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.R;
import com.parse.ParseFile;

import java.util.List;

import static com.example.rentingapp.Controllers.ImagesController.openImage;

/**
 * This Adapter is implemented by the RecyclerView that shows the secondary images of an item.
 */
public class ItemImagesAdapter extends RecyclerView.Adapter<ItemImagesAdapter.ViewHolder> {

    private Context context;
    private List<ParseFile> images;

    public ItemImagesAdapter(Context context, List<ParseFile> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemImagesAdapter.ViewHolder holder, int position) {
        ParseFile image = images.get(position);
        holder.bind(image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
        }

        /**
         * Places the item's image in the imageView.
         * @param image item's image
         */
        public void bind(ParseFile image) {
            //Bind the post data to the view elements
            //condition to check if there is an image attached
            if (image != null) {
                RequestOptions mediaOptions = new RequestOptions();
                mediaOptions = mediaOptions.transforms(new CenterCrop(), new RoundedCorners(20));
                Glide.with(context).load(image.getUrl()).placeholder(R.drawable.profile_image_empty).apply(mediaOptions).into(ivImage);
                ivImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openImage(image.getUrl(), context, ivImage);
                    }
                });
            }
        }
    }
}
