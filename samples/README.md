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
cordova prepare
```

2) Connect iOS device

3) Open `platforms/ios/cordova-hockey-app.xcworkspace` in Xcode and select provisioning profile/change bundle id for project. In addition, you can set target for `cordova-hockey-app`.

4) Build and run project
```
cordova build ios --buildFlag='-UseModernBuildSystem=0'
cordova run ios
```

*Note:* project contains symbolic links to SDK. If you want to use production SDK version, remove and install plugin again:
```
cordova plugin remove cordova-plugin-hockeyapp --save
cordova plugin add cordova-plugin-hockeyapp@latest --save
```
