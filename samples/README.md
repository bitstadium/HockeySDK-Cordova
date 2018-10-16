# Cordova HockeySDK sample
This repository contains a sample app that allows for easy testing of the SDK and its functionality.

### Prerequisites
1) Condova should be installed locally on your machine.
2) Hockey application should be created on portal before testing.
3) Minimum iOS version is 9.0. Minimum Android version is 4.4.

### Configuration
To use this app, replace `<appid>` with real appId of your Hockey application here:

`www/js/index.js:31`
```
onDeviceReady: function() {
   console.log("Device Ready!");
   hockeyapp.start(null, null, "<appid>");
```

### How to build and run app

1) Install packages and initialize platform:
```
npm install
cordova prepare
```

2) Connect device

3) *For iOS only:* Open `platforms/ios/cordova-hockey-app.xcworkspace` in Xcode and select provisioning profile/change bundle id for project. In addition, you can set target for `cordova-hockey-app`.

4) Build and run project
```
# iOS:
cordova build ios --buildFlag='-UseModernBuildSystem=0'
cordova run ios

# Android:
cordova build android
cordova run android
```

This project contains symbolic links to the local SDK. If you want to use the app with the release version of the SDK version, remove and install plugin the plugin again:
```
cordova plugin remove cordova-plugin-hockeyapp --save
cordova plugin add cordova-plugin-hockeyapp@latest --save
```
