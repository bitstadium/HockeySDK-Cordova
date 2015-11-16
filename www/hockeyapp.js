var exec = require('cordova/exec');

var hockeyapp = {
    start: function(success, failure, appId, autoSend, loginMode) {
        autoSend = (autoSend === true || autoSend === "true");
        loginMode = loginMode || hockeyapp.loginMode.ANONYMOUS;
        exec(success, failure, "HockeyApp", "start", [appId, autoSend, loginMode]);
    },
    feedback: function(success, failure) {
        exec(success, failure, "HockeyApp", "feedback", []);
    },
    forceCrash: function(success, failure) {
        exec(success, failure, "HockeyApp", "forceCrash", []);
    },
    checkForUpdate: function(success, failure) {
        exec(success, failure, "HockeyApp", "checkForUpdate", []);
    },
    verifyLogin: function(success, failure, appSecret) {
        exec(success, failure, "HockeyApp", "verifyLogin", [appSecret]);
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
