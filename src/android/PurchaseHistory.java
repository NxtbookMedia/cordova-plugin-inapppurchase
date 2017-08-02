package com.alexdisler.inapppurchases;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of past purchases made by the current user, including canceled
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
