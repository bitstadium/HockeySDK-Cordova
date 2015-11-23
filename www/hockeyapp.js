var exec = require('cordova/exec');

var hockeyapp = {
    start: function(success, failure, appId, autoSend, ignoreDefaultHandler, loginMode, appSecret) {
        autoSend = (autoSend === true || autoSend === "true");
        ignoreDefaultHandler = (ignoreDefaultHandler === true || ignoreDefaultHandler === "true");
        loginMode = loginMode || hockeyapp.loginMode.ANONYMOUS;
        appSecret = appSecret || '';
        
        // Requesting loginMode.EMAIL_ONLY without an appSecret is not permitted
        if (loginMode === hockeyapp.loginMode.EMAIL_ONLY && appSecret.trim() === '') {
            if (failure && typeof failure === 'function') {
                failure('You must specify your app secret when using email-only login mode');
            }
            return;
        }

        exec(success, failure, "HockeyApp", "start", [appId, loginMode, appSecret, autoSend, ignoreDefaultHandler]);
    },
    feedback: function (success, failure) {
        exec(success, failure, "HockeyApp", "feedback", []);
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
    
    // Valid loginMode values
    loginMode: {
        ANONYMOUS: 0,
        EMAIL_ONLY: 1,
        EMAIL_PASSWORD: 2,
        VALIDATE: 3
    }
};

module.exports = hockeyapp;
