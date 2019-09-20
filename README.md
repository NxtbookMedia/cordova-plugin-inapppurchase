# cordova-plugin-inapppurchase 📱💰

[![Build Status](https://travis-ci.org/AlexDisler/cordova-plugin-inapppurchase.svg?branch=master)](https://travis-ci.org/AlexDisler/cordova-plugin-inapppurchase)
[![Coverage Status](https://coveralls.io/repos/github/AlexDisler/cordova-plugin-inapppurchase/badge.svg?branch=master&a=1)](https://coveralls.io/github/AlexDisler/cordova-plugin-inapppurchase?branch=master)

A lightweight Cordova plugin for in app purchases on iOS/Android. See [demo app](https://github.com/AlexDisler/cordova-inapppurchases-app) and [blog post](https://alexdisler.com/2016/02/29/in-app-purchases-ionic-cordova/).

## Looking for maintainers

If you would like to maintain this project, get in touch.

## Features

- Simple, promise-based API
- Support for consumable/non-consumable products and paid/free subscriptions
- Support for restoring purchases
- Uses well tested native libraries internally - [RMStore](https://github.com/robotmedia/RMStore) for iOS and an adjusted  [com.google.payments](https://github.com/MobileChromeApps/cordova-plugin-google-payments/tree/master/src/android) for Android

## Install

    $ cordova plugin add cordova-plugin-inapppurchase

## Configuration

### iOS

No configuration is necessary.

### Android

You must create a ```manifest.json``` in your project's ```www``` folder with your Android Billing Key:

    { "play_store_key": "<Base64-encoded public key from the Google Play Store>" }

You can get this key from the Google Play Store (under "Services & APIs") after uploading your app.

## Setting up and testing purchases

- [Configuring Cordova in app purchases on iOS and Android](https://alexdisler.com/2016/04/30/configuring-in-app-purchases-cordova-ionic-ios-android/)
- [Testing in app purchases](https://alexdisler.com/2016/04/04/testing-cordova-in-app-purchases-on-ios-android/)
- [Receipt validation (with nodejs)](https://alexdisler.com/2016/03/20/validating-cordova-in-app-purchases-on-ios-and-android-using-nodejs/)
- [Tips for signing and running Cordova apps on Android to test in app purchases locally](https://alexdisler.com/2016/04/01/tips-for-signing-installing-cordova-apps-on-android/)

## API

All functions return a Promise.

Be aware that on Android the `getProducts()`, `buy()`, `subscribe()`,
`consume()`, and `getPurchaseHistory()` methods cannot run simultaneously with
any of the others. Run them in series, not in parallel (unless you like reading
error messages).

### Get Products

#### inAppPurchase.getProducts(productIds)

- ___productIds___ - an array of product ids

Retrieves a list of full product data from Apple/Google. **This function must be called before making purchases.**

If successful, the promise resolves to an array of objects. Each object has the following attributes:

- ```productId``` - SKU / product bundle id (such as 'com.yourapp.prod1')
- ```title``` - short localized title
- ```description``` - long localized description
- ```price``` - localized price

___Example:___

```js
inAppPurchase
  .getProducts(['com.yourapp.prod1', 'com.yourapp.prod2', ...])
  .then(function (products) {
    console.log(products);
    /*
       [{ productId: 'com.yourapp.prod1', 'title': '...', description: '...', price: '...' }, ...]
    */
  })
  .catch(function (err) {
    console.log(err);
  });
```

### Buy

#### inAppPurchase.buy(productId)

- ___productId___ - a string of the productId

If successful, the promise resolves to an object with the following attributes that you will need for the receipt validation:

- ```transactionId``` - The transaction/order id
- ```receipt``` - On ***iOS*** it will be the base64 string of the receipt, on ***Android*** it will be a string of a json with all the transaction details required for validation such as ```{"orderId":"...","packageName:"...","productId":"...","purchaseTime":"...", "purchaseState":"...","purchaseToken":"..."}```
- ```signature``` - On Android it can be used to [consume](https://github.com/AlexDisler/cordova-plugin-inapppurchase#inapppurchaseconsumeproducttype-receipt-signature) a purchase. On iOS it will be an empty string.
- ```productType``` - On Android it can be used to [consume](https://github.com/AlexDisler/cordova-plugin-inapppurchase#inapppurchaseconsumeproducttype-receipt-signature) a purchase. On iOS it will be an empty string.

***Receipt validation:*** - To [validate your receipt](https://alexdisler.com/2016/03/20/validating-cordova-in-app-purchases-on-ios-and-android-using-nodejs/), you will need the ```receipt``` and ```signature``` on Android and the ```receipt``` and ```transactionId``` on iOS.

___Example:___

```js
inAppPurchase
  .buy('com.yourapp.prod1')
  .then(function (data) {
    console.log(data);
    /*
      {
        transactionId: ...
        receipt: ...
        signature: ...
      }
    */
  })
  .catch(function (err) {
    console.log(err);
  });
```

### Subscribe

#### inAppPurchase.subscribe(productId)

- ___productId___ - a string of the productId

This function behaves the same as [buy()](https://github.com/AlexDisler/cordova-plugin-inapppurchase#buy) but with subscriptions.

### Consume

#### inAppPurchase.consume(productType, receipt, signature)

- ___productType___ - string
- ___receipt___ - string (containing a json)
- ___signature___ - string

All 3 parameters are returned by the [buy()](https://github.com/AlexDisler/cordova-plugin-inapppurchase#buy) or [restorePurchases()](https://github.com/AlexDisler/cordova-plugin-inapppurchase#inapppurchaserestorepurchases) functions.

Call this function after purchasing a "consumable" product to mark it as consumed.

___NOTE: This function is only relevant to Android purchases.___

On ***Android***, you must consume products that you want to let the user purchase multiple times. If you will not consume the product after a purchase, the next time you will attempt to purchase it you will get the error message:
```Unable to buy item / Item already owned```.

On ***iOS*** there is no need to "consume" a product. However, in order to make your code cross platform, it is recommended to call it for iOS consumable purchases as well.

___Example:___

```js
// first buy the product...
inAppPurchase
  .buy('com.yourapp.consumable_prod1')
  .then(function (data) {
    // ...then mark it as consumed:
    return inAppPurchase.consume(data.productType, data.receipt, data.signature);
  })
  .then(function () {
    console.log('product was successfully consumed!');
  })
  .catch(function (err) {
    console.log(err);
  });
```

### Restore Purchases

#### inAppPurchase.restorePurchases()

If successful, the promise resolves to an array of objects with the following attributes:

- ```productId```
- ```state``` - the state of the product. On ***Android*** the statuses are: ```0 - ACTIVE, 1 - CANCELLED,  2 - REFUNDED```
- ```transactionId```
- ```date``` - timestamp of the purchase
- ```productType``` - On Android it can be used to [consume](https://github.com/AlexDisler/cordova-plugin-inapppurchase#inapppurchaseconsumeproducttype-receipt-signature) a purchase. On iOS it will be an empty string.
- ```receipt``` - On Android it can be used to [consume](https://github.com/AlexDisler/cordova-plugin-inapppurchase#inapppurchaseconsumeproducttype-receipt-signature) a purchase. On iOS it will be an empty string.
- ```signature``` - On Android it can be used to [consume](https://github.com/AlexDisler/cordova-plugin-inapppurchase#inapppurchaseconsumeproducttype-receipt-signature) a purchase. On iOS it will be an empty string.

___Example:___

```js
inAppPurchase
  .restorePurchases()
  .then(function (data) {
    console.log(data);
    /*
      [{
        transactionId: ...
        productId: ...
        state: ...
        date: ...
      }]
    */
  })
  .catch(function (err) {
    console.log(err);
  });
```

See [Differences Between Product Types](https://developer.apple.com/library/ios/documentation/NetworkingInternet/Conceptual/StoreKitGuide/Chapters/Products.html)

Note that [on iOS most apps should use `loadReceipt()` rather than
`restorePurchases()`](https://developer.apple.com/library/archive/documentation/NetworkingInternet/Conceptual/StoreKitGuide/Chapters/Restoring.html#//apple_ref/doc/uid/TP40008267-CH8-SW9)
in order to restore purchases.

### Get Purchase History

#### inAppPurchases.getPurchaseHistory()

Use this to get the most recent Play Store purchase for every SKU the user has
ever purchased, regardless of whether they still own it.

There is no equivalent method for iOS, since the purchase receipt contains all
past purchases including expired subscriptions.

On success, the promise resolves to an array of objects with the following
attributes:

- ```productId``` The purchased product's app-specific ID.
- ```purchaseTime``` The time the purchase was made, in milliseconds since the
  Unix epoch.
- ```developerPayload``` The optional developer-defined payload submitted with
  the original purchase.
- ```purchaseToken``` The token that ties this purchase to a specific
  product/user pair.

### Load And Refresh Receipt (iOS)

On ***iOS***, the purchase receipt is [the standard data structure for
verifying and restoring user
purchases](https://developer.apple.com/library/archive/documentation/NetworkingInternet/Conceptual/StoreKitGuide/Chapters/Restoring.html#//apple_ref/doc/uid/TP40008267-CH8-SW9).

This plugin offers a few methods for working with it.

#### inAppPurchase.loadReceipt()

Returns a promise to load the local copy of the purchase receipt.

The promise resolves with an encoded string representing the receipt, suitable
for use in [validating user
purchases](https://developer.apple.com/library/archive/releasenotes/General/ValidateAppStoreReceipt/Chapters/ValidateRemotely.html#//apple_ref/doc/uid/TP40010573-CH104-SW1).
If no receipt exists it resolves with an empty string.

This is the [standard way to get a user's purchases on
iOS](https://developer.apple.com/library/archive/releasenotes/General/ValidateAppStoreReceipt/Chapters/ValidateRemotely.html#//apple_ref/doc/uid/TP40010573-CH104-SW2)
and is what you should use by default for loading a user's purchases.


#### inAppPurchase.refreshReceipt()

Returns a promise to refresh the purchase receipt from the iTunes Store.

This forces users to authenticate to the App Store, so per Apple's docs it
should be done [only when users restore
purchases](https://developer.apple.com/library/archive/documentation/NetworkingInternet/Conceptual/StoreKitGuide/Chapters/Restoring.html#//apple_ref/doc/uid/TP40008267-CH8-SW9).

The promise resolves with an encoded string representing the receipt, suitable
for use in [validating user
purchases](https://developer.apple.com/library/archive/releasenotes/General/ValidateAppStoreReceipt/Chapters/ValidateRemotely.html#//apple_ref/doc/uid/TP40010573-CH104-SW1).
If no receipt exists it resolves with an empty string.

For backwards compatibility, `inAppPurchase.getReceipt()` is aliased to this
method, but that alias is deprecated due to its ambiguity.


On ***Android*** this function will always return an empty string since it's not needed for Android purchases.

___Example:___

```js
inAppPurchase
  .getReceipt()
  .then(function (receipt) {
    console.log(receipt);
  })
  .catch(function (err) {
    console.log(err);
  });
```

## Developing

### Build:

    $ npm install
    $ gulp watch

(Does not work with node 10)

### Run tests:

    $ npm test

Or, if you would like to watch and re-run tests:

    $ npm run watch

Coverage report:

    $ nyc npm test

## Debugging

- Have you enabled In-App Purchases for your App ID?
- Have you checked Cleared for Sale for your product?
- Does your project’s .plist Bundle ID match your App ID?
- Have you generated and installed a new provisioning profile for the new App ID?
- Have you configured your project to code sign using this new provisioning profile?
- Have you waited several hours since adding your product to iTunes Connect?
- Have you tried deleting the app from your device and reinstalling?
- Have you accepted contracts for IAPs in iTunes connect?
- Is your device jailbroken? If so, you need to revert the jailbreak for IAP to work.

## Thanks / Credits

- [Adar Porat](https://github.com/aporat)
- [Robot Media](https://github.com/robotmedia)
- [MobileChromeApps](https://github.com/MobileChromeApps)

## More

- [cordova-icon](https://github.com/AlexDisler/cordova-icon) - automatic icon resizing for cordova
- [ng-special-offer](https://github.com/AlexDisler/ng-special-offer) - prompt users to rate your cordova app in the app store
- [ionic-lock-screen](https://github.com/AlexDisler/ionic-lock-screen) - passcode lock screen for ionic (with touch id support for iOS)
- [ionic-zoom-view](https://github.com/AlexDisler/ionic-zoom-view) - an easy way to add a zoom view to images using an ionic modal
- [ng-persist](https://github.com/AlexDisler/ng-persist) - store data on mobile devices (using cordova) that persists even if the user reinstalls the app

## License

The MIT License

Copyright (c) 2016, Alex Disler

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
