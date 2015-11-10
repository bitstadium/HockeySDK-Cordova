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

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("start")) {
            token = args.optString(0);
            String autoSend = args.optString(1);
            FeedbackManager.register(cordova.getActivity(), token, null);
            if (autoSend.equals("true")) {
                CrashManager.register(cordova.getActivity(), token, new CrashManagerListener() {
                    public boolean shouldAutoUploadCrashes() {
                        return true;
                    }
                });
            } else {
                CrashManager.register(cordova.getActivity(), token);
            }

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
