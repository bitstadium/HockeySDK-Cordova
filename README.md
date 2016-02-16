# cordova-plugin-hockeyapp

This plugin exposes the HockeyApp SDK for ios and android

Including:

* HockeyAppSDK-iOS 3.8.2
* HockeyAppSDK-Android 3.7.0

## Installation

    cordova plugin add cordova-plugin-hockeyapp

## Supported Platforms

- Android
- iOS

## Methods

- **hockeyapp.start(success:function, error:function, appId:string, autoSend:boolean, ignoreDefaultHandler:boolean, loginMode:int, appSecret:string):void**

Initialize HockeyApp SDK

When specifying the loginMode, it is recommended that you use the hockeyapp.loginMode enumeration. Available values are:

- ANONYMOUS: The user will not be prompted to authenticate.
- EMAIL_ONLY: The user will be prompted to specify a valid email address. You must also pass your appSecret when using this mode.
- EMAIL_PASSWORD: The user will be prompted for a valid username/password combination.
- VALIDATE: The user will not be prompted to authenticate, but the app will try to validate with the HockeyApp service.

Important: Only ANONYMOUS is available for iOS devices, other modes will produce an error.

- **hockeyapp.feedback(sucess:function, error:function):void**

Display tester feedback UI

- **hockeyapp.feedbackModal(sucess:function, error:function, takeScreenshot: boolean, data:any):void**

Display tester modal feedback UI including a screenshot and/or text attachment.  If `takeScreenshot` is true, an image of the screen at the moment that `feedbackModal` is called and included as a JPEG attachment. The object in `data`, if present, will be serialized as text and included in a text attachment.

See also: [CrossWalk considerations](#crosswalk-considerations)   

- **hockeyapp.checkForUpdate(sucess:function, error:function):void**

Check for a new version

- **hockeyapp.forceCrash():void**

Force crash app

- **hockeyapp.addMetaData(success:function, error:function, data:object):void**

Adds arbitrary metadata to a crash, which will be displayed in crash reports.

## CrossWalk considerations

An application that is hosted in a CrossWalk View will show a blank screenshot unless the following requirements are met:

- The application references the cordova-plugin-crosswalk-engine plugin version 1.6.0 or above
- The application links against the xwalk engine version 18 or above

## Warning

On iOS, you need to disable bitcode. add `ENABLE_BITCODE = false` in `platforms/ios/cordova/build-release.xcconfig .
