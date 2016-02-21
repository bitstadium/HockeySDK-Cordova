# cordova-plugin-hockeyapp

This plugin exposes the HockeyApp SDK for iOS and Android

Including:

* HockeyAppSDK-iOS 3.8.5
* HockeyAppSDK-Android 3.7.0

## Installation

    cordova plugin add cordova-plugin-hockeyapp

## Supported Platforms

- Android
- iOS

## Methods

#### Initialize HockeyApp SDK
```
hockeyapp.start(success:function, error:function, appId:string, autoSend:boolean, ignoreDefaultHandler:boolean, loginMode:int, appSecret:string):void
```

When specifying the `loginMode`, it is recommended that you use the `hockeyapp.loginMode` enumeration. Available values are:

- `ANONYMOUS`: The user will not be prompted to authenticate.
- `EMAIL_ONLY`: The user will be prompted to specify a valid email address. You must also pass your `appSecret` when using this mode.
- `EMAIL_PASSWORD`: The user will be prompted for a valid username/password combination.
- `VALIDATE`: The user will not be prompted to authenticate, but the app will try to validate with the HockeyApp service.

Important: For iOS devices only `ANONYMOUS` is available, other modes will produce an error.

#### Display tester feedback user interface
```
hockeyapp.feedback(success:function, error:function):void
```

#### Display modal tester feedback user interface with attachments
```
hockeyapp.feedbackModal(sucess:function, error:function, takeScreenshot: boolean, data:any):void
```

Display tester modal feedback UI including a screenshot and/or text attachment.  If `takeScreenshot` is true, an image of the screen at the moment that `feedbackModal` is called and included as a JPEG attachment. The object in `data`, if present, will be serialized as text and included in a text attachment.

See also: [CrossWalk considerations](#crosswalk-considerations)   

#### Check for a new version
```
hockeyapp.checkForUpdate(success:function, error:function):void
```

#### Force an app crash
```
hockeyapp.forceCrash():void
```
Can be used to test the crash reporting feature of HockeyApp.

#### Add arbitrary metadata to a crash
```
hockeyapp.addMetaData(success:function, error:function, data:object):void
```
Will be displayed in crash reports in HockeyApp.

## CrossWalk considerations

When calling ```feedbackModal``` from an application that is hosted in a CrossWalk WebView, a blank screenshot will be attached to the report unless the following requirements are met:

- The application references the cordova-plugin-crosswalk-engine plugin version 1.6.0 or above
- The application links against the xwalk engine version 18 or above


