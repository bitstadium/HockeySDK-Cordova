var exec = require('cordova/exec');

var hockeyapp = {
    start: function(success, failure, appId, autoSend, ignoreDefaultHandler, createNewFeedbackThread, loginMode, appSecret) {
        autoSend = (autoSend === true || autoSend === "true");
        ignoreDefaultHandler = (ignoreDefaultHandler === true || ignoreDefaultHandler === "true");
        loginMode = loginMode || hockeyapp.loginMode.ANONYMOUS;
        appSecret = appSecret || '';
        createNewFeedbackThread = (createNewFeedbackThread === true || createNewFeedbackThread === "true");

        // Requesting loginMode.EMAIL_ONLY without an appSecret is not permitted
        if (loginMode === hockeyapp.loginMode.EMAIL_ONLY && appSecret.trim() === '') {
            if (failure && typeof failure === 'function') {
                failure('You must specify your app secret when using email-only login mode');
            }
            return;
        }

        exec(success, failure, "HockeyApp", "start", [appId, loginMode, appSecret, autoSend, ignoreDefaultHandler,  createNewFeedbackThread]);
    },
    setUserEmail: function (success, failure, userEmail) {
      exec(success, failure, "HockeyApp", "setUserEmail", [userEmail]);
    },
    setUserName: function (success, failure, userName) {
      exec(success, failure, "HockeyApp", "setUserName", [userName]);
    },
    feedback: function (success, failure) {
        exec(success, failure, "HockeyApp", "feedback", []);
    },
    composeFeedback: function (success, failure, attachScreenshot, data) {
        var parameters = [attachScreenshot === true || attachScreenshot === "true"];
        if (data != undefined) {
            parameters.push(JSON.stringify(data));
        }
        exec(success, failure, "HockeyApp", "composeFeedback", parameters);
    },
    forceCrash: function (success, failure) {
        exec(success, failure, "HockeyApp", "forceCrash", []);
    },
    checkForUpdate: function (success, failure) {
        exec(success, failure, "HockeyApp", "checkForUpdate", []);
    },
    addMetaData: function (success, failure, data) {
        exec(success, failure, "HockeyApp", "addMetaData", [JSON.stringify(data)]);
    },
    trackEvent: function (success, failure, eventName) {
        exec(success, failure, "HockeyApp", "trackEvent", [eventName]);
    },
    
    // Valid loginMode values
    loginMode: {
        ANONYMOUS: 0,
        EMAIL_ONLY: 1,
        EMAIL_PASSWORD: 2,
        VALIDATE: 3
    }
};

module.exports = hockeyapp;
