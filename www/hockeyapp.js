var exec = require('cordova/exec');

var hockeyapp = {
    start: function(success, failure, appId, autoSend, loginMode, ignoreDefaultHandler) {
        autoSend = (autoSend === true || autoSend === "true");
        loginMode = loginMode || hockeyapp.loginMode.ANONYMOUS;
        ignoreDefaultHandler = ignoreDefaultHandler || false;
        exec(success, failure, "HockeyApp", "start", [appId, autoSend, loginMode]);
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
