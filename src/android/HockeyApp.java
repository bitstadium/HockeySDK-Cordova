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
import net.hockeyapp.android.FeedbackManagerListener;
import net.hockeyapp.android.LoginManager;
import net.hockeyapp.android.LoginManagerListener;
import net.hockeyapp.android.metrics.MetricsManager;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.Tracking;
import net.hockeyapp.android.UpdateManager;

import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
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
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

public class HockeyApp extends CordovaPlugin {

    public static final long XWALK_SCREENSHOT_WAIT_MS = 5000;
    public static final String XWALK_SCREENSHOT_CAPTURE_MSG = "captureXWalkBitmap";
    public static final String XWALK_SCREENSHOT_BITMAP_MSG = "onGotXWalkBitmap";
    
    public static boolean initialized = false;
    public static String appId;
    public static Object monitor = new Object();
    public static volatile Bitmap bitmap;
    
    private ConfiguredCrashManagerListener crashListener;
    
    // Integration with Crosswalk requires:
    // - Crosswalk engine version 18 or later
    // - Latest cordova-plugin-crosswalk-webview plugin     
    private Bitmap getBitmap() {
        bitmap = null;
        boolean isCrosswalk = false;
        try {
            Class.forName("org.crosswalk.engine.XWalkWebViewEngine");
            isCrosswalk = true;
        } catch (Exception e) {
        }

        if (isCrosswalk) {
            webView.getPluginManager().postMessage(XWALK_SCREENSHOT_CAPTURE_MSG, this);
            try {
                synchronized (monitor) {
                    monitor.wait(XWALK_SCREENSHOT_WAIT_MS);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                Thread.currentThread().interrupt();
            }
        } else {
            View view = webView.getView();
            view.setDrawingCacheEnabled(true);
            bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
        }

        return bitmap;
    }

    @Override
    public Object onMessage(String id, Object data) {
        if (id.equals(XWALK_SCREENSHOT_BITMAP_MSG) && data != null) {
            bitmap = (Bitmap)data;
            synchronized (monitor) {
                monitor.notify();                
            }
        }
        return null;
    }
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("start")) {
            appId = args.optString(0);
            boolean autoSend = args.optBoolean(3);
            boolean ignoreDefaultHandler = args.optBoolean(4, false);
            boolean shouldCreateNewFeedbackThread = args.optBoolean(5, false);

            FeedbackManager.register(cordova.getActivity(), appId, shouldCreateNewFeedbackThread ? new SingleThreadFeedbackManagerListener() : null);
            this.crashListener = new ConfiguredCrashManagerListener(autoSend, ignoreDefaultHandler);
            
            MetricsManager.register(cordova.getActivity(), cordova.getActivity().getApplication(), appId);
            CrashManager.register(cordova.getActivity(), appId, this.crashListener);
            
            // Verify the user
            final CallbackContext loginCallbackContext = callbackContext;
            final int loginMode = args.optInt(1, LoginManager.LOGIN_MODE_ANONYMOUS);
            final String appSecret = args.optString(2, "");
            
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

        if (action.equals("setUserEmail")) {
            String userEmail = args.optString(0);
            FeedbackManager.setUserEmail(userEmail);
            callbackContext.success();
            return true;
        }

        if (action.equals("setUserName")) {
            String userName = args.optString(0);
            FeedbackManager.setUserName(userName);
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
        
        if (action.equals("composeFeedback")) {
            final ArrayList<Uri> attachments = new ArrayList<Uri>();
            final Activity context = cordova.getActivity();
            boolean attachScreenshot = args.optBoolean(0);
            if (attachScreenshot) {
                Bitmap screenshot = getBitmap();
                if (screenshot == null) {
                    callbackContext.error("failed to take screenshot");
                    return false;
                }
                try{
                    File imageFile = File.createTempFile("hockeyapp-scrn", ".jpg", context.getFilesDir());
                    imageFile.deleteOnExit();
                    FileOutputStream stream = new FileOutputStream(imageFile);
                    screenshot.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                    stream.close();
                    attachments.add(Uri.fromFile(imageFile));
                } catch (IOException e) {
                    callbackContext.error("failed to save screenshot");
                    return false;
                }
            }
            if (args.length() > 1) {
                String jsonData = args.optString(1);
                try {
                    File dataFile = File.createTempFile("hockeyapp-data", ".json", context.getFilesDir());
                    dataFile.deleteOnExit();
                    FileWriter writer = new FileWriter(dataFile);
                    writer.write(jsonData);
                    writer.close();
                    attachments.add(Uri.fromFile(dataFile));
                } catch (IOException e) {
                    callbackContext.error("failed to create json data file");
                    return false;
                }
            }
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Uri[] attachmentArray = new Uri[attachments.size()];
                    attachmentArray = attachments.toArray(attachmentArray);
                    FeedbackManager.showFeedbackActivity(context, attachmentArray);
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
        
        if (action.equals("addMetaData")) {
            try {
                String jsonArgs = args.optString(0);
                JSONObject rawMetaData = new JSONObject(jsonArgs);
                Iterator<String> keys = rawMetaData.keys();
                boolean success = true;
            
                while (keys.hasNext()) {
                    String key = keys.next();
                    success = success && this.crashListener.putMetaData(key, rawMetaData.getString(key));
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
        }
        
        if (action.equals("trackEvent")) {
            String eventName = args.optString(0);
            if (eventName.isEmpty()) {
                callbackContext.error("no event name provided. Ignoring....");
                return false;
            } else { 
                MetricsManager.trackEvent(eventName);
                callbackContext.success();
                return true;
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

class SingleThreadFeedbackManagerListener extends FeedbackManagerListener {
    @Override
    public boolean feedbackAnswered(FeedbackMessage latestMessage){
        return true;
    }

    @Override
    public boolean shouldCreateNewFeedbackThread(){
        return true;
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
    
    public boolean putMetaData(String key, String value) {
        try {
            this.crashMetaData.put(key, value);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}