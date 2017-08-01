package com.alexdisler.inapppurchases;

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
