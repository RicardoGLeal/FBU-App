package com.example.rentingapp.Controllers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.material.datepicker.CalendarConstraints;

public class RangeValidator implements CalendarConstraints.DateValidator {

    long minDate, maxDate;

    public RangeValidator(long minDate, long maxDate) {
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    RangeValidator(Parcel parcel) {
        minDate = parcel.readLong();
        maxDate = parcel.readLong();
    }

    @Override
    public boolean isValid(long date) {
        return !(minDate > date || maxDate < date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(minDate);
        dest.writeLong(maxDate);
    }

    public static final Parcelable.Creator<RangeValidator> CREATOR = new Parcelable.Creator<RangeValidator>() {

        @Override
        public RangeValidator createFromParcel(Parcel parcel) {
            return new RangeValidator(parcel);
        }

        @Override
        public RangeValidator[] newArray(int size) {
            return new RangeValidator[size];
        }
    };
}


