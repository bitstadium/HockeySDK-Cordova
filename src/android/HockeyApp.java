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

import java.lang.Runnable;
import java.lang.RuntimeException;
import java.lang.Thread;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HockeyApp extends CordovaPlugin {

    public static boolean initialized = false;
    public static String appId;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("start")) {
            appId = args.optString(0);
            boolean autoSend = args.optBoolean(1);
            
            FeedbackManager.register(cordova.getActivity(), appId);
            if (autoSend) {
                CrashManager.register(cordova.getActivity(), appId, new CrashManagerListener() {
                    public boolean shouldAutoUploadCrashes() {
                        return true;
                    }
                });
            } else {
                CrashManager.register(cordova.getActivity(), appId);
            }
            
            // Verify the user
            final CallbackContext loginCallbackContext = callbackContext;
            final int loginMode = args.optInt(2, LoginManager.LOGIN_MODE_ANONYMOUS);
            final String appSecret = args.optString(3, "");
            
            if (loginMode == LoginManager.LOGIN_MODE_ANONYMOUS) {
                // LOGIN_MODE_ANONYMOUS does not raise the onSuccess method
                // of the LoginManagerListener, so just return immediately.
                initialized = true;
                callbackContext.success();
                return true;
            } else if (loginMode == LoginManager.LOGIN_MODE_VALIDATE) {
                // LOGIN_MODE_VALIDATE does not currently work on Android, so fail immediately
                callbackContext.error("The requested login mode is not available on the Android platform");
                return false;
            }

            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoginManager.register(cordova.getActivity(), appId, appSecret, loginMode, new LoginManagerListener() {
                        @Override
                        public void onBack() {
                            loginCallbackContext.error("Login failed");
                        }
                        
                        @Override
                        public void onSuccess() {
                            initialized = true;
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
