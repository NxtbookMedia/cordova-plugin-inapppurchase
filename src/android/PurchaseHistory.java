package com.alexdisler.inapppurchases;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of items the user purchased in the past. May include canceled
 * and refunded items.
 */
public class PurchaseHistory {
    ArrayList<PurchaseHistoryItem> mPurchaseHistoryItems = new ArrayList<PurchaseHistoryItem>();

    PurchaseHistory() { }

    public List<PurchaseHistoryItem> getAllItems() {
        return mPurchaseHistoryItems;
    }

    public void addItem(PurchaseHistoryItem item) {
        mPurchaseHistoryItems.add(item);
    }
}
