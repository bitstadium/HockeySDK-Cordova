# Cordova HockeySDK sample

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
cordova platform add ios
```

2) Open `platforms/ios/cordova-hockey-app.xcworkspace` in Xcode and select provisioning profile/change bundle id for project. In addition, you can set target for `cordova-hockey-app`.

3) Build and run project
```
cordova build ios --buildFlag='-UseModernBuildSystem=0'
cordova run ios
```
