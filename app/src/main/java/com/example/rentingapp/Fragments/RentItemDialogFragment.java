package com.example.rentingapp.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;

import com.example.rentingapp.Models.Item;
import com.example.rentingapp.Models.User;
import com.example.rentingapp.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.DAYS;

public class RentItemDialogFragment extends DialogFragment {

    private TextView tvItemTitle, tvOwnersName, tvPricePerDay, tvTotalDays, tvTotalPrice, tvStartDate, tvEndDate;

    public RentItemDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_rent_item, container);
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
        // Fetch item from bundle
        Item item = getArguments().getParcelable("item");
        // Set fields values obtained from the item.
        tvItemTitle.setText(item.getTitle());
        tvOwnersName.setText(item.getOwner().getString(User.KEY_NAME));
        tvPricePerDay.setText(String.valueOf(item.getPrice()));

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create the Material date range picker.
                MaterialDatePicker.Builder<Pair<Long, Long>> dateRangePicker = MaterialDatePicker.Builder.dateRangePicker();
                Calendar instance = Calendar.getInstance();
                MaterialDatePicker<Pair<Long, Long>> picker = dateRangePicker.build();
                picker.show(getChildFragmentManager(), picker.toString());
                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {

                    //onPositiveButton method confirms the dates in the date range picker. It returns the Pair of dates selected
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        Date startDate = new Date(selection.first);
                        Date endDate = new Date(selection.second);
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
        });

        //mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        //getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        //mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
