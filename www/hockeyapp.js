var exec = require('cordova/exec');

var hockeyapp = {
    start:function(success, failure, token) {
        exec(success, failure, "HockeyApp", "start", [ token ]);
    },
    versionStart:function(success, failure) {
        exec(success, failure, "HockeyApp", "versionStart", []);
    },
    versionStop:function(success, failure) {
        exec(success, failure, "HockeyApp", "versionStop", []);
    },
    feedback:function(success, failure) {
        exec(success, failure, "HockeyApp", "feedback", []);
    }
};

module.exports = hockeyapp;
