package com.example.rentingapp.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Rent")
public class Rent extends ParseObject {
    public static final String KEY_ITEM = "item";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_TENANT = "tenant";
    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";
    public static final String KEY_DAYS_COUNT = "daysCount";
    public static final String KEY_TOTAL_PRICE = "totalPrice";

    public void setItem(Item item) {
        put(KEY_ITEM, item);
    }

    public void setOwner(ParseUser owner) {
        put(KEY_OWNER, owner);
    }

    public void setTenant(ParseUser tenant) {
        put(KEY_TENANT, tenant);
    }

    public void setStartDate(Date startDate) {
        put(KEY_START_DATE, startDate);
    }

    public void setEndDate(Date endDate) {
        put(KEY_END_DATE, endDate);
    }

    public void setDaysCount(int daysCount) {
        put(KEY_DAYS_COUNT, daysCount);
    }

    public void setTotalPrice(float totalPrice) {
        put(KEY_TOTAL_PRICE, totalPrice);
    }
}
