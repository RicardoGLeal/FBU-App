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
    public static final String KEY_STATUS = "status";


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

    public void setStatus(String status) {
        put(KEY_STATUS, status);
    }

   public Item getItem() {
        return (Item) getParseObject(KEY_ITEM);
    }

    public ParseUser getOwner() {
        return getParseUser(KEY_OWNER);
    }
    public Date getStartDate() {
        return getDate(KEY_START_DATE);
    }

    public Date getEndDate() {
        return getDate(KEY_END_DATE);
    }

    public ParseUser getTenant() {
        return getParseUser(KEY_TENANT);
    }

    public double getTotalPrice() {
        return getDouble(KEY_TOTAL_PRICE);
    }

    public String getStatus() {
        return getString(KEY_STATUS);
    }
}
