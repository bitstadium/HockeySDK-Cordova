# com.zengularity.cordova.hockeyapp [![experimental](http://hughsk.github.io/stability-badges/dist/experimental.svg)](http://github.com/hughsk/stability-badges)

This plugin exposes the HockeyApp SDK for ios and android

Including:

* HockeyAppSDK-iOS 3.5.4
* HockeyAppSDK-Android 3.0.1

## Installation

    cordova plugin add https://github.com/peutetre/cordova-plugin-hockeyapp.git

## Supported Platforms

- Android
- iOS

## Methods

- hockeyapp.start(success:function, error:function, appid:string):void

Initialize HockeyApp SDK

- hockeyapp.feedback(sucess:function, error:function):void

Display tester feedback UI

- hockeyapp.versionChcek(sucess:function, error:function):void

Check for a new vesion and notify the user if a newer version is available

## Android tips

Listen to the `backbutton` event to fix the backbutton on the feedback UI like:

```javascript
document.addEventListener("backbutton", function () { }, false);
```
