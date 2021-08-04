package com.example.rentingapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.Layout;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentingapp.Fragments.FeedFragment;
import com.example.rentingapp.Fragments.SendSmsDialogFragment;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.Models.Rent;
import com.example.rentingapp.Models.User;
import com.example.rentingapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.example.rentingapp.Controllers.ImagesController.loadCircleImage;
import static com.example.rentingapp.Controllers.SendPushNotification.sendRentStatusPush;

/**
 * This adapter is responsible for obtaining the information of each of the rents.
 */
public class RentsAdapter extends RecyclerView.Adapter<RentsAdapter.ViewHolder>{
    public static final String TAG = "RentsAdapter";
    public static final String KEY_APPROVED = "Approved";
    public static final String KEY_REJECTED = "Rejected";
    public static final String KEY_WAITING = "Waiting";
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
        TextView tvItemTitle, tvCategory, tvStartDate, tvEndDate, tvPersonName, tvLocation, tvTotalPrice,
                tvRenterOrOwnerName, tvRenterOrOwnerLoc, tvCellphone, tvStatus;
        GoogleMap map;
        MapView mapView;LinearLayout layoutExpandable;
        CardView cardView;
        Button btnExpand;
        ImageButton btnCall, btnMessage, btnDelete;
        ParseUser user;

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
            layoutExpandable = itemView.findViewById(R.id.layoutExpandable);
            mapView = itemView.findViewById(R.id.lite_listrow_map);
            cardView = itemView.findViewById(R.id.cardView);
            btnExpand = itemView.findViewById(R.id.btnExpand);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnMessage = itemView.findViewById(R.id.btnMessage);
            tvCellphone = itemView.findViewById(R.id.tvCellphone);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            //btnExpand ClickListener to expand the rent CardViews
            btnExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutExpandable.getVisibility()==View.GONE) {
                        //Makes a transition and changes to visible the entire CardView.
                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                        layoutExpandable.setVisibility(View.VISIBLE);
                        btnExpand.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    }
                    else {
                        //Makes a transition and hides the extra part of the CardView.
                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                        layoutExpandable.setVisibility(View.GONE);
                        btnExpand.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    }
                }
            });
            AppCompatActivity activity = (AppCompatActivity) context;

            //Opens SendSMS Dialog Fragment
            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = activity.getSupportFragmentManager();
                    SendSmsDialogFragment sendSmsDialogFragment = SendSmsDialogFragment.newInstance(tvCellphone.getText().toString().trim());
                    sendSmsDialogFragment.show(fm,"sendSmsDialogFragment");
                }
            });

            //Creates an intent to make a call
            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "tel:" + tvCellphone.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    activity.startActivity(intent);
                }
            });
        }

        /**
         * Creates a new marker and moves the camera at the location of the owner or tenant.
         * @param rent
         */
        private void setMapLocation(Rent rent) {
            if (map == null) return;

            if (rent == null) return;
            LatLng latLng;

            //if it's an own rent, gets the LatLng of the tenant.
            if(ownRentedItems)
                latLng = User.getLatLng(rent.getTenant());
            else  {
                //if it's a foreign rent, gets the LatLng of the owner.
                latLng = User.getLatLng(rent.getOwner());
            }

            // Add a marker for this item and set the camera
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
            map.addMarker(new MarkerOptions().position(latLng));

            // Set the map type back to normal.
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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

            //Loads the item image.
            loadCircleImage(context, item.getImages().get(0), ivItemImage);

            //changes the rent's status TextView according to the rental status
            switch (rent.getStatus()) {
                case KEY_REJECTED:
                    tvStatus.setTextColor(context.getResources().getColor(R.color.red));
                    break;
                case KEY_APPROVED:
                    tvStatus.setTextColor(context.getResources().getColor(R.color.quantum_googgreen));
                    break;
                default:
                    tvStatus.setTextColor(context.getResources().getColor(R.color.textColor));
                    break;
            }
            //if the items are owned
            if (ownRentedItems) {
                user = rent.getTenant();
                tvRenterOrOwnerName.setText("Renter's Name: ");
                tvRenterOrOwnerLoc.setText("Renter's Location: ");
            }
            else {
                user = rent.getOwner();
                tvStatus.setBackgroundDrawable(null);
            }

            tvPersonName.setText(user.getString(User.KEY_NAME));
            tvLocation.setText(user.getString(User.KEY_PLACE_ADDRESS));
            tvStatus.setText(rent.getStatus());

            //Creates a new map
            if(mapView != null) {
                mapView.onCreate(null);
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        MapsInitializer.initialize(context);
                        map = googleMap;
                        setMapLocation(rent);
                    }
                });
            }

            tvStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ownRentedItems && !rent.getStatus().equals(KEY_APPROVED)){
                        createConfirmRentDialogBuilder(rent);
                    }
                }
            });
            if(rent.getStatus().equals(KEY_REJECTED)) {
                btnDelete.setVisibility(ImageButton.VISIBLE);
            } else
                btnDelete.setVisibility(ImageButton.GONE);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createDeleteRentDialogBuilder(rent);
                }
            });
        }

        /**
         * Invokes a DialogBuilder in which the item's owner can accept or reject a rental.
         * A Push Notification is sent to the renter informing the result of their request.
         * @param rent rental to approve or reject.
         */
        private void createConfirmRentDialogBuilder(Rent rent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Do you want to approve or reject this rent request?")
                    .setCancelable(true)
                    .setPositiveButton("Approve", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            rent.setStatus(KEY_APPROVED);
                            rent.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    tvStatus.setText(KEY_APPROVED);
                                    sendRentStatusPush(rent, KEY_APPROVED);
                                    Toast.makeText(context, "Rent request accepted", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    }).setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    rent.setStatus(KEY_REJECTED);
                    rent.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            tvStatus.setText(KEY_REJECTED);
                            sendRentStatusPush(rent, KEY_REJECTED);
                            Toast.makeText(context, "Rent request rejected", Toast.LENGTH_SHORT).show();
                            rents.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            dialog.dismiss();
                        }
                    });
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        /**
         * Invokes a DialogBuilder in which the item's renter can delete a rental after being rejected.
         * @param rent rental rejected
         */
        private void createDeleteRentDialogBuilder(Rent rent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to delete this rent request?")
                    .setCancelable(true)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            rent.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(context, "Rent deleted", Toast.LENGTH_SHORT).show();
                                    rents.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                    dialog.dismiss();
                                }
                            });
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
