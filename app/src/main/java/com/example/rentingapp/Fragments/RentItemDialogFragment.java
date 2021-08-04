package com.example.rentingapp.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.DateInterval;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;

import com.example.rentingapp.Models.Item;
import com.example.rentingapp.Models.Rent;
import com.example.rentingapp.Models.User;
import com.example.rentingapp.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.rentingapp.Controllers.ActionsController.getDistanceInKm;
import static com.example.rentingapp.Controllers.ActionsController.limitRanges;
import static com.example.rentingapp.Controllers.CustomAlertDialogs.errorDialog;
import static com.example.rentingapp.Controllers.CustomAlertDialogs.loadingDialog;
import static com.example.rentingapp.Controllers.CustomAlertDialogs.successDialog;
import static com.example.rentingapp.Controllers.SendPushNotification.sendRentRequestPush;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * This DialogFragment is responsible for showing the user the details of their rental request,
 * as well as requesting the dates on which
 */
public class RentItemDialogFragment extends DialogFragment {
    public static final String TAG = "RentItemDialogFragment";
    private TextView tvItemTitle, tvOwnersName, tvPricePerDay, tvTotalDays, tvTotalPrice, tvStartDate, tvEndDate;
    private Button btnConfirm, btnCancel;
    private Item item;
    private Date startDate, endDate;
    List<DateInterval> datesAlreadyReserved;
    SweetAlertDialog loadingDialog, successDialog, errorDialog;

    public RentItemDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_rent_item, container, false);
        // Set transparent background and no title
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }

    /**
     * Creates a new Dialog Fragment with an item sent.
     * @param item the item that is wanted to rent.
     * @return
     */
    public static RentItemDialogFragment newInstance(Item item) {
        RentItemDialogFragment frag = new RentItemDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("item", item);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get fields from view
        tvItemTitle = view.findViewById(R.id.tvItemTitle);
        tvOwnersName = view.findViewById(R.id.tvOwnersName);
        tvPricePerDay = view.findViewById(R.id.tvPricePerDay);
        tvTotalDays = view.findViewById(R.id.tvTotalDays);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnCancel = view.findViewById(R.id.btnCancel);

        // Fetch item from bundle
        item = getArguments().getParcelable("item");

        // Set fields values obtained from the item.
        tvItemTitle.setText(item.getTitle());
        tvOwnersName.setText(item.getOwner().getString(User.KEY_NAME));
        tvPricePerDay.setText(String.valueOf(item.getPrice()));
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItemRents();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tvStartDate.getText().toString().isEmpty() && !tvEndDate.getText().toString().isEmpty())
                    RentItem();
                else {
                    errorDialog = errorDialog(getContext(), "Please verify that are the fields are filled");
                    errorDialog.show();
                }

            }
        });
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * Gets all the rents that the item already has.
     */
    private void getItemRents() {
        datesAlreadyReserved = new ArrayList<>();
        // Specify which class to query
        ParseQuery<Rent> query = ParseQuery.getQuery(Rent.class);
        //Restrict if there are selected categories
        query.whereEqualTo(Rent.KEY_ITEM, item);
        // Retrieve all the posts
        query.findInBackground(new FindCallback<Rent>() {
            @Override
            public void done(List<Rent> rents, ParseException e) {
                if (e != null) {
                    return;
                }
                for (Rent rent: rents) {
                    datesAlreadyReserved.add(new DateInterval(rent.getStartDate(), rent.getEndDate()));
                }
                buildCalendar();
            }
        });
    }

    /**
     * Create the Material date range picker.
     */
    private void buildCalendar() {
        //Initialize the Material Date Picker
        MaterialDatePicker.Builder<Pair<Long, Long>> dateRangePicker = MaterialDatePicker.Builder.dateRangePicker();
        //dateRangePicker.setCalendarConstraints(limitRange().build());
        if(!datesAlreadyReserved.isEmpty())
            dateRangePicker.setCalendarConstraints(limitRanges(datesAlreadyReserved).build());

        MaterialDatePicker<Pair<Long, Long>> picker = dateRangePicker.build();
        picker.show(getChildFragmentManager(), picker.toString());
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {

            //onPositiveButton method confirms the dates in the date range picker. It returns the Pair of dates selected
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                startDate = new Date(selection.first);
                endDate = new Date(selection.second);
                String strStartDate = new SimpleDateFormat("dd-MM-yyyy").format(startDate);
                String strEndDate = new SimpleDateFormat("dd-MM-yyyy").format(endDate);
                long diff = endDate.getTime() - startDate.getTime();
                long daysNumber = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                tvStartDate.setText(strStartDate);
                tvEndDate.setText(strEndDate);
                tvTotalDays.setText(String.valueOf(daysNumber));
                tvTotalPrice.setText(String.valueOf(daysNumber*item.getPrice()));
            }
        });
    }

    /**
     * Creates the rent object and saves it in the database.
     */
    private void RentItem() {
        //Creates a new Rent object and sets all its fields.
        Rent rent = new Rent();
        rent.setItem(item);
        rent.setOwner(item.getOwner());
        rent.setTenant(ParseUser.getCurrentUser());
        rent.setStartDate(startDate);
        rent.setEndDate(endDate);
        rent.setDaysCount(Integer.valueOf(tvTotalDays.getText().toString()));
        rent.setTotalPrice(Float.valueOf(tvTotalPrice.getText().toString()));

        //creates an loadingDialog and shows it.
        loadingDialog = loadingDialog(getContext());
        loadingDialog.show();
        rent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                loadingDialog.dismissWithAnimation();
                if (e != null) {
                    //creates an errorDialog and shows it.
                    errorDialog = errorDialog(getContext(), e.getMessage());
                    errorDialog.show();
                    return;
                } else {
                    sendRentRequestPush(item);
                    //creates an successDialog showing the confirmation.
                    successDialog = successDialog(getContext(), "Rented Item Successfully");
                    successDialog.show();
                    successDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            //dismiss both the alert dialog and this dialog fragment.
                            successDialog.dismissWithAnimation();
                            dismiss();
                        }
                    });
                }
            }
        });
    }

    /**
     * This class represents a period range in which a item is rented.
     */
    public class DateInterval {
        Date initialDate;
        Date endDate;

        //Constructor
        public DateInterval(Date initialDate, Date endDate) {
            this.initialDate = initialDate;
            this.endDate = endDate;
        }
        //Getters and Setters

        public Date getInitialDate() {
            return initialDate;
        }

        public void setInitialDate(Date initialDate) {
            this.initialDate = initialDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }
    }
}
