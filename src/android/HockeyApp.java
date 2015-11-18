package com.zengularity.cordova.hockeyapp;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.lang.RuntimeException;
import java.lang.Runnable;
import java.lang.StringBuilder;
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
        } else if (action.equals("addMetaData")) {
            if(initialized) {
                try {
                    String jsonArgs = args.optString(0);
                    JSONObject rawMetaData = new JSONObject(jsonArgs);
                    Iterator<?> keys = rawMetaData.keys();
                    boolean success = true;
                
                    while (keys.hasNext()) {
                        String key = (String)keys.next();
                        success = success && this.crashListener.addSetMetaData(key, rawMetaData.getString(key));
                    }
                    
                    if (success) {
                        callbackContext.success();
                    } else {
                        callbackContext.error("failed to parse metadata. Ignoring....");
                    }
                    
                    return success;
                } catch (JSONException e) {
                    callbackContext.error("failed to parse metadata. Ignoring....");
                    return false;
                }
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

class ConfiguredCrashManagerListener extends CrashManagerListener {
    private boolean autoSend = false;
    private boolean ignoreDefaultHandler = false;
    private JSONObject crashMetaData;
    
    public ConfiguredCrashManagerListener(boolean autoSend, boolean ignoreDefaultHandler) {
        this.autoSend = autoSend;
        this.ignoreDefaultHandler = ignoreDefaultHandler;
        this.crashMetaData = new JSONObject();
    }
    
    @Override
    public boolean shouldAutoUploadCrashes() {
        return this.autoSend;
    }
    
    @Override
    public boolean ignoreDefaultHandler() {
        return this.ignoreDefaultHandler;
    }
    
    @Override
    public String getDescription() {
        return crashMetaData.toString();
    }
    
    public boolean addSetMetaData(String key, String value) {
        try {
            this.crashMetaData.put(key, value);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}