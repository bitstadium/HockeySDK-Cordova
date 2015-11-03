var exec = require('cordova/exec');

var hockeyapp = {
    start: function(success, failure, autoSend, token) {
        exec(success, failure, "HockeyApp", "start", [ autoSend, token ]);
    },
    feedback: function(success, failure) {
        exec(success, failure, "HockeyApp", "feedback", []);
    },
    forceCrash: function(success, failure) {
        exec(success, failure, "HockeyApp", "forceCrash", []);
    },
    checkForUpdate: function(success, failure) {
        exec(success, failure, "HockeyApp", "checkForUpdate", []);
    }
};

module.exports = hockeyapp;
