package com.alexdisler.inapppurchases;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Metadata from the Play Store API about a past purchase (which the user may
 * no longer have access to).
 */
public class PurchaseHistoryItem {
    String mJsonItemInfo;
    String mProductId;
    long mPurchaseTime;
    String mDeveloperPayload;
    String mPurchaseToken;

    public PurchaseHistoryItem(String jsonItemInfo) throws JSONException {
        mJsonItemInfo = jsonItemInfo;

        JSONObject o = new JSONObject(jsonItemInfo);

        mProductId = o.optString("productId");
        mPurchaseTime = o.optLong("purchaseTime");
        mDeveloperPayload = o.optString("developerPayload");
        mPurchaseToken = o.optString("purchaseToken");
    }

    public String getProductId() { return mProductId; }
    public long getPurchaseTime() { return mPurchaseTime; }
    public String getDeveloperPayload() { return mDeveloperPayload; }
    public String getPurchaseToken() { return mPurchaseToken; }
    public String getOriginalJson() { return mJsonItemInfo; }

    @Override
    public String toString() { return "PurchaseHistoryItem: " + mJsonItemInfo; }
}
