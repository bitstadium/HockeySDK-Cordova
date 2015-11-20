package com.zengularity.cordova.hockeyapp;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.LoginManager;
import net.hockeyapp.android.LoginManagerListener;
import net.hockeyapp.android.Tracking;
import net.hockeyapp.android.UpdateManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.lang.RuntimeException;
import java.lang.Runnable;
import java.lang.StringBuilder;
import java.lang.Thread;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HockeyApp extends CordovaPlugin {

    public static boolean initialized = false;
    public static String token;
    
    private ConfiguredCrashManagerListener crashListener;
    public static String appId;
    public static int loginMode;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("start")) {
            token = args.optString(0);
            boolean autoSend = args.optBoolean(1, false);
            loginMode = args.optInt(2, LoginManager.LOGIN_MODE_EMAIL_PASSWORD);
            boolean ignoreDefaultHandler = args.optBoolean(3, false);
            
            FeedbackManager.register(cordova.getActivity(), token, null);
            this.crashListener = new ConfiguredCrashManagerListener(autoSend, ignoreDefaultHandler);
            CrashManager.register(cordova.getActivity(), token, this.crashListener);

            initialized = true;
            callbackContext.success();
            return true;
        }
        
        // All other operations require that start() have been called, so check that now
        if (!initialized) {
            callbackContext.error("cordova hockeyapp plugin not initialized, call start() first");
            return false;
        } 
        
        if (action.equals("checkForUpdate")) {
            UpdateManager.register(cordova.getActivity(), appId);
            callbackContext.success();
            return true;
        }
        
        if (action.equals("feedback")) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FeedbackManager.showFeedbackActivity(cordova.getActivity());
                }
            });

            callbackContext.success();
            return true;
        }
        
        if (action.equals("forceCrash")) {
            new Thread(new Runnable() {
                public void run() {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    throw new RuntimeException("Test crash at " + df.format(c.getTime()));
                }
            }).start();
            return true;
        }
        
        if (action.equals("verifyLogin")) {
            // We're going to need to call back into the JS side once the login is complete
            final CallbackContext loginCallbackContext = callbackContext;
            final String appSecret = args.optString(0);

            cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoginManager.register(cordova.getActivity(), appId, appSecret, loginMode, new LoginManagerListener() {
                            @Override
                            public void onBack() {
                                loginCallbackContext.error("");
                            }
                            
                            @Override
                            public void onSuccess() {
                                loginCallbackContext.success();
                            }
                        });
                        
                        LoginManager.verifyLogin(cordova.getActivity(), cordova.getActivity().getIntent());
                    }
                });
                
            PluginResult pluginResult = new PluginResult(Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }
        
        if (action.equals("addMetaData")) {
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

        // Unrecognized command     
        return false;
    }
    
    @Override
    public void onPause(boolean multitasking) {
        Tracking.stopUsage(cordova.getActivity());            
    }
    
    @Override
    public void onResume(boolean multitasking) {
        Tracking.startUsage(cordova.getActivity());            
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