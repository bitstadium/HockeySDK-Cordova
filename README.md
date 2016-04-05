# Cordova Plugin for HockeyApp

This plugin provides client-side integration for the [HockeyApp](http://hockeyapp.net) service, allowing you to easily add crash reporting, beta distribution and user metrics to your Cordova app(s).

* [Supported Cordova Platforms](#supported-cordova-platforms)
* [Getting Started](#getting-started)
    * [Collecting User Feedback](#collecting-user-feedback)
    * [Checking For Updates](#checking-for-updates)
    * [Tracking Custom Events](#tracking-custom-events)
    * [Enhancing Crash Reports](#enhancing-crash-reports)
* [API Reference](#api-reference)

## Supported Cordova Platforms

Cordova 5.0.0+ is fully supported, along with the following associated platforms:

- Android ([cordova-android](https://github.com/apache/cordova-android) 4.0.0+) - *Including [CrossWalk](https://github.com/crosswalk-project/cordova-plugin-crosswalk-webview)*
- iOS ([cordova-ios](https://github.com/apache/cordova-ios) 3.9.0+) - *Including [WKWebView](https://github.com/apache/cordova-plugin-wkwebview-engine)*

To check which versions of each Cordova platform you are currently using, you can run the following command and inspect the `Installed platforms` list:

```shell
cordova platform ls
```

If you're running an older Android and/or iOS platform than is mentioned above, and would be open to upgrading, you can easily do so by running the following commands (omitting a platform if it isn't neccessary):

```shell
cordova platform update android
cordova platform update ios
```

## Getting Started

After you've created your HockeyApp account, and registered your app with the service, you can begin integrating the Cordova plugin into your app by running the following command:

```shell
cordova plugin add cordova-plugin-hockeyapp@latest
```

With the CodePush plugin installed, configure your app to initialize the HockeyApp client (using your app ID) by calling the following function within your `deviceready` handler (or the equivalent location):

```javascript
hockeyapp.start(null, null, "APP_ID");
```

As a reminder, the app ID can be retrieved from the details page of your app in the HockeyApp portal (see the `App ID` field below). 

<img width="300" src="https://cloud.githubusercontent.com/assets/116461/14294392/b5d4dcea-fb25-11e5-8d36-9bcc76368f86.png" />

If you would like to test out the crash reporting feature and don't already have a crash in your app (nice job!), you can add the following method call anywhere in your app:

```javascript
hockeyapp.forceCrash();
```

And that's it! Your app will now send crash reports and user metrics (e.g. daily/monthly unique users, # of sessions per day) to the server without doing any additional work. Make sure to remove the call to `forceCrash` once you're satisfied with your test and are ready to begin sending your app to testers and on into production.

If you would like to add additional capabilities to your app (e.g. [detecting updates](#checking-for-updates), [capturing user feedback](#collecting-user-feedback), [adding custom instrmentation to view app-specific usage data](#tracking-custom-events)), then check out the following sections, or view the [API reference](#api-reference) for more details.

![User metrics](https://cloud.githubusercontent.com/assets/116461/14294691/2f2ad5e4-fb27-11e5-8e9d-611c8a1dd549.png)

### Collecting User Feedback

<img width="200" src="https://cloud.githubusercontent.com/assets/116461/14295150/48a0be74-fb29-11e5-981f-0f8f60e9f74b.png" align="right" />

If you're using HockeyApp to distribute beta builds to your testers, then you'll likely also want to collect feedback from them in addition to simply viewing crash reports and usage metrics. To do this, simply call the following method in order to display a UI to your users that allows them to send feedback for your app directly to you, and allows you to respond from the HockeyApp portal:

```javascript
hockeyapp.feedback();
```

You can call this method in response to a "Give Feedback" button, in a shake gesture handler, or wherever is appropriate for your app.

<br style="clear: right" />

### Checking For Updates

If you would like your beta testers to be notified whenever a new version of your app is available, you can call the following method to check for an update and display an update dialog when available:

```javascript
hockeyapp.checkForUpdate();
```

You can call this method in your `deviceready` handler, in a "Check for update" button, or wherever is appropriate for your app.

*NOTE: This [should not be called in production/release builds](http://support.hockeyapp.net/discussions/problems/46569-can-i-use-update-manager-with-google-play-store-apps#comment_38058429) intended for the Google Play Store.*
 
### Tracking Custom Events

*NOTE: Using this feature currently requires that you be registered with the HockeyApp [preseason program](http://hockeyapp.net/preseason/).*

By default, once you've called the `hockeyapp.start()` method, your app will begin automatically collecting basic metrics for daily and monthly users and sessions. However, it is very likely that you will want to track custom app events (e.g. "View_Cart", "Added_Item") in order to understand how your users are engaging with your app at a more granular/actionable level. In order to instrument your app to capture this data for both beta and production collection, simply call the following method where appropriate in your app:

```javascript
hockeapp.trackEvent(null, null, "EVENT_NAME");
```

### Enhancing Crash Reports

By default, any time your app crashes, the report sent to HockeyApp will include the call stack, as well as various other pieces of information (e.g. OS, device manufacturer) to help narrow down and/or repro the cause of the issue. However, it can be very helpful to understand the context of the users app when inspecting a crash report, and therefore, if you need to "attach" additional metadata to a crash report, you can simply add the following line of code:

```javascript
hockeyapp.addMetaData(null, null { someCustomProp: 23, anotherProp: "Value" });
```
 
The metadata property accepts an arbitrary JavaScript object, and therefore, can be used to log any strings, booleans, numbers, etc. Subsequent calls to the `addMetaData` method will "merge" the objects together such that any future crash reports will include a union of all object properties that were specified by any call to `addMetaData`.

When you view a crash report in the HockeyApp portal, you can see the metadata that you attached to it by selecting the **Description** button underneath the **Data** section within the **Crash Logs** tab of the respective crash report.

<img width="400" src="https://cloud.githubusercontent.com/assets/116461/14298576/8a80661c-fb3a-11e5-828a-c469b88c1a07.png" />

## API Reference

The HockeyApp API is exposed to your app via the global `hockeyapp` object, which is available after the `deviceready` event has fired. This API exposes the following top-level methods:

* [**addMetaData**](#hockeyappaddMetadata) - Attaches arbitrary metadata to the next crash report in order to provide more context about the user's state.

* [**feedback**](#hockeyappfeedback) - Displays the feedback UI so that testers can send and receive feedback about the app.

* [**forceCrash**](#hockeyappforcecrash) - Immediately crashes the app. This is used strictly for testing the HockeyApp crash reporting capabilities.

* [**start**](#hockeyappstart) - Initializes the HockeyApp plugin, and configures it with the approrpiate app ID and user settings (e.g. should crash reports be automatically submitted).

* [**trackEvent**](#hockeyapptrackevent) - Logs an app-specific event for analytic purposes.

### hockeyapp.addMetadata

```javascript
hockeyapp.addMetaData(successCallback: function, errorCallback: function, metadata: Object): void;
```

Attaches arbitrary metadata to the next crash report in order to provide more context about the user's state. Subsequent calls to this method will "merge" the metadata together into a single JavaScript object that will be sent along with the next crash report.

__Parameters__:

* **successCallback** - Function that will be triggered when the metadata has been successfully added.

* **errorCallback** - Function that will be triggered when adding the metadata failed for some reason.

* **metaData** - A JavaScript object that describes the metadata (i.e. properties and values) that you wuold like to attach to the next crash report.

### hockeyapp.feedback

```javascript
hockeyapp.feedback(successCallback?: function, errorCallback?: function): void;
```

Displays the feedback UI so that testers can send and receive feedback about the app.

__Parameters__:

* **successCallback** - Function that will be triggered when the feedback UI is successfully displayed.

* **errorCallback** - Function that will be triggered when an error occurs displaying the feedback UI.

### hockeyapp.forceCrash

```javascript
hockeyapp.forceCrash(): void;
```

Immediately crashes the app. This is used strictly for testing the HockeyApp crash reporting capabilities.

### hockeyapp.start

```javascript
hockeyapp.start(successCallback: function, errorCallback: function, appId: string, autoSend?: boolean, ignoreDefaultHandler?: boolean, loginMode?: hockeyapp.loginMode, appSecret?: string): void;
```

Initializes the HockeyApp plugin, and configures it with the appropriate app ID and user settings (e.g. should crash reports be automatically submitted).

__Parameters__:

* **successCallback** - Function that will be triggered when the initialization is successful.

* **errorCallback** - Function that will be triggered when an error occurs trying to initialize the plugin.

* **appID** - The ID of the app as provided by the HockeyApp portal.

* **autoSend** - Specifies whether you would like crash reports to be automatically sent to the HockeyApp server when the end user restarts the app. Defaults to `false`.

* **ignoreDefaultHandler** - Specifies whether you would like to display the standard dialog when the app is about to crash. This parameter is only relevant on Android, and therefore, you can set it to anything on iOS. Defaults to `false`.

* **loginMode** - The mechanism to use in order to authenticate users. Defaults to `hockeyapp.loginMode.ANONYMOUS`. The `hockeyapp.loginMode` enum provides the following available options:

    - `ANONYMOUS` - The end user isn't authenticated at all.
    
    - `EMAIL_ONLY` - The end user will be prompted to specify a valid email address. If this mode is selected, the `appSecret` parameter must also be specified. 
    
    - `EMAIL_PASSWORD`: The end user will be prompted for a valid email/password combination.
    
    - `VALIDATE`: The end user will not be prompted to authenticate, but the app will try to validate with the HockeyApp service.

    *NOTE: Only the `ANONYMOUS` login mode is supported on iOS, and therefore, you can only use the other modes within Android apps.*

* **appSecret** - The app secret as provided by the HockeyApp portal. This parameter only needs to be set if you're setting the `loginMode` parameter to `EMAIL_ONLY`.

### hockeyapp.trackEvent

```javascript
hockeyapp.trackEvent(successCallback: function, errorCallback: function, eventName: string): void;
```

Logs an app-specific event for analytic purposes.

__Parameters__:

* **successCallback** - Function that will be triggered when the event has been successfully tracked.

* **errorCallback** - Function that will be triggered when tracking the event failed for some reason.

* **eventName** - The name (e.g. "ITEM_ADDED") of the custom event that should be logged.