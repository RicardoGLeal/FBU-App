package com.example.rentingapp.Adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.Models.Rent;
import com.example.rentingapp.Models.User;
import com.example.rentingapp.R;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.example.rentingapp.Controllers.ImagesController.loadCircleImage;

public class RentsAdapter extends RecyclerView.Adapter<RentsAdapter.ViewHolder> {
    public static final String TAG = "RentsAdapter";
    private Context context;
    private List<Rent> rents;
    private boolean ownRentedItems;

    /**
     * Constructor of the adapter.
     * @param context Context of the activity or fragment where it is called.
     * @param rents List of the rents to populate.
     */
    public RentsAdapter(Context context, List<Rent> rents, boolean ownRentedItems) {
        this.context = context;
        this.rents = rents;
        this.ownRentedItems = ownRentedItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rented_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RentsAdapter.ViewHolder holder, int position) {
        Rent rent = rents.get(position);
        holder.bind(rent);
    }

    @Override
    public int getItemCount() {
        return rents.size();
    }

    public void clear() {
        rents.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImage;
        TextView tvItemTitle, tvCategory, tvStartDate, tvEndDate, tvPersonName, tvLocation, tvTotalPrice, tvRenterOrOwnerName, tvRenterOrOwnerLoc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Get fields from view
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            tvPersonName = itemView.findViewById(R.id.tvPersonName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvRenterOrOwnerName = itemView.findViewById(R.id.tvRenterOrOwnerName);
            tvRenterOrOwnerLoc = itemView.findViewById(R.id.tvRenterOrOwnerLoc);
        }

        /**
         * Set the values of the rent.
         * @param rent
         */
        public void bind(Rent rent) {
            Item item= (Item) rent.getParseObject(Rent.KEY_ITEM);

            tvItemTitle.setText(rent.getItem().getTitle());
            tvCategory.setText(rent.getItem().getCategory());
            tvStartDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(rent.getStartDate()));
            tvEndDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(rent.getEndDate()));
            tvTotalPrice.setText(String.valueOf(rent.getTotalPrice()));
            loadCircleImage(context, item.getImages().get(0), ivItemImage);

            ParseUser parseUser;
            if (ownRentedItems) {
                parseUser = rent.getTenant();
                tvRenterOrOwnerName.setText("Renter's Name: ");
                tvRenterOrOwnerLoc.setText("Renter's Location: ");
            }
            else
                parseUser = rent.getOwner();
            tvPersonName.setText(parseUser.getString(User.KEY_NAME));
            tvLocation.setText(parseUser.getString(User.KEY_PLACE_ADDRESS));

        }
    }
}
