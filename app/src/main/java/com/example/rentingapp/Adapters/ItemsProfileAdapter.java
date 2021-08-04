package com.example.rentingapp.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.rentingapp.Fragments.ItemDetailsFragment;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.R;
import com.parse.ParseFile;
import java.util.List;

/**
 * This adapter is implemented by the RecyclerView of the user's item in their profile.
 * It is responsible for displaying all the items of a user in images.
 */
public class ItemsProfileAdapter extends RecyclerView.Adapter<ItemsProfileAdapter.ViewHolder> {

    private Context context;
    private List<Item> items;

    public ItemsProfileAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsProfileAdapter.ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Clean all elements of the recycler view.
     */
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            itemView.setOnClickListener(this);
        }

        public void bind(Item item) {
            //Bind the post data to the view elements
            ParseFile image = item.getImages().get(0);
            //condition to check if there is an image attached
            if (image != null) {
                RequestOptions mediaOptions = new RequestOptions();
                mediaOptions = mediaOptions.transforms(new CenterCrop(), new RoundedCorners(20));
                Glide.with(context).load(image.getUrl()).apply(mediaOptions).into(ivImage);
            }
        }

        /**
         * OnClickListener of each item. It opens the PostDetailsFragment with the details of the post clicked.
         * @param v
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                AppCompatActivity activity = (AppCompatActivity) context;
                Fragment fragment;
                fragment = new ItemDetailsFragment(items.get(position));
                ((AppCompatActivity) context).getSupportFragmentManager();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        }
    }
}
