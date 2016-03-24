# cordova-plugin-hockeyapp

This plugin exposes the HockeyApp SDK for iOS and Android

Including:

* HockeyAppSDK-iOS 4.1.0
* HockeyAppSDK-Android 4.1.0

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

#### Track custom events
```
hockeyapp.trackEvent(success:function, error:function, eventName:string):void
```
Can be used to record the occurrence of a custom app event in HockeyApp.