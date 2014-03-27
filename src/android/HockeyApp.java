package com.zengularity.cordova.hockeyapp;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

public class HockeyApp extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("init")) {
            this.init();
            callbackContext.success();
            return true;
        }
        else {
            return false;
        }
    }

    public void init() {
        this.webView.postMessage("hockeyapp", "init");
    }

}
