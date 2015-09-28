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

- hockeyapp.start(success:function, error:function, hockeyapp_id:string):void

Initialize HockeyApp SDK

- hockeyapp.feedback(sucess:function, error:function):void

Display tester feedback UI

- hockeyapp.checkForUpdate(sucess:function, error:function):void

Check for a new vesion

- hockeyapp.forceCrash():void

Force crash app

## Warning

On iOS, you need to disable bitcode. add `ENABLE_BITCODE = false` in `platforms/ios/cordova/build-release.xcconfig`.
