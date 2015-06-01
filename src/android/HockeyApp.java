package com.zengularity.cordova.hockeyapp;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.UpdateManager;

public class HockeyApp extends CordovaPlugin {

    public static boolean initialized = false;
    public static String token;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("start")) {
            token = args.optString(0);
            FeedbackManager.register(cordova.getActivity(), token, null);
            initialized = true;
            callbackContext.success();
            return true;
        } else if(action.equals("feedback")) {
            if(initialized) {
                cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FeedbackManager.showFeedbackActivity(cordova.getActivity());
                    }
                });
                callbackContext.success();
                return true;
            }
            else {
                callbackContext.error("cordova hockeyapp plugin not initialized, call start() first");
                return false;
            }
        } else if(action.equals("versionStart")) {
            if (initialized) {
                UpdateManager.register(cordova.getActivity(), token);
                return true;
            }
            else {
                callbackContext.error("cordova hockeyapp plugin not initialized, call start() first");
                return false;
            }
        } else if (action.equals("versionStop")) {
            if (initialized) {
                UpdateManager.unregister();
                return true;
            }
            else {
                callbackContext.error("cordova hockeyapp plugin not initialized, call start() first");
                return false;
            }
        }
        else {
            return false;
        }
    }

}
