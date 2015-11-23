# cordova-plugin-hockeyapp

This plugin exposes the HockeyApp SDK for ios and android

Including:

* HockeyAppSDK-iOS 3.8.2
* HockeyAppSDK-Android 3.5.0

## Installation

    cordova plugin add cordova-plugin-hockeyapp

## Supported Platforms

- Android
- iOS

## Methods

- hockeyapp.start
- hockeyapp.checkForUpdate
- hockeyapp.feedback
- hockeyapp.forceCrash

### hockeyapp.start

Initialize HockeyApp SDK

`hockeyapp.start(success:function, error:function, hockeyapp_id:string, autosend:boolean):void`

### hockeyapp.checkForUpdate

Check for a new vesion

`hockeyapp.checkForUpdate(sucess:function, error:function):void`

Note: This [should not be called in production/release builds](http://support.hockeyapp.net/discussions/problems/46569-can-i-use-update-manager-with-google-play-store-apps#comment_38058429) intended for the Google Play Store.

### hockeyapp.feedback

Display tester feedback UI

`hockeyapp.feedback(sucess:function, error:function):void`

### hockeyapp.forceCrash

Force crash app

`hockeyapp.forceCrash():void`


## Warning

On iOS, you need to disable bitcode. add `ENABLE_BITCODE = false` in `platforms/ios/cordova/build-release.xcconfig`.
