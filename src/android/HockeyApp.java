package com.zengularity.cordova.hockeyapp;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.RuntimeException;
import java.lang.Runnable;
import java.lang.Thread;

public class HockeyApp extends CordovaPlugin {

    public static boolean initialized = false;
    public static String token;
    
    private ConfiguredCrashManagerListener crashListener;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("start")) {
            token = args.optString(0);
            boolean autoSend = args.optBoolean(1, false);
            boolean ignoreDefaultHandler = args.optBoolean(2, false);
            
            FeedbackManager.register(cordova.getActivity(), token, null);
            this.crashListener = new ConfiguredCrashManagerListener(autoSend, ignoreDefaultHandler);
            CrashManager.register(cordova.getActivity(), token, this.crashListener);

            initialized = true;
            callbackContext.success();
            return true;
        }
        else if(action.equals("checkForUpdate")) {
            UpdateManager.register(cordova.getActivity(), token);
            callbackContext.success();
            return true;
        }
        else if(action.equals("feedback")) {
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
        } else if(action.equals("forceCrash")) {
            if(initialized) {
                new Thread(new Runnable() {
                    public void run() {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        throw new RuntimeException("Test crash at " + df.format(c.getTime()));
                    }
                }).start();
                return true;
            } else {
                callbackContext.error("cordova hockeyapp plugin not initialized, call start() first");
                return false;
            }
        }
        else {
            return false;
        }
    }

}

private class ConfiguredCrashManagerListener extends CrashManagerListener {
    private boolean autoSend = false;
    private boolean ignoreDefaultHandler = false;
    
    public ConfiguredCrashManagerListener(boolean autoSend, boolean ignoreDefaultHandler) {
        this.autoSend = autoSend;
        this.ignoreDefaultHandler = ignoreDefaultHandler;
    }
    
    @Override
    public boolean shouldAutoUploadCrashes() {
        return this.autoSend;
    }
    
    @Override
    public boolean ignoreDefaultHandler() {
        return this.ignoreDefaultHandler;
    }
}