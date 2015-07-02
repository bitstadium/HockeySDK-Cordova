var exec = require('cordova/exec');

var hockeyapp = {
    start:function(success, failure, token) {
        exec(success, failure, "HockeyApp", "start", [ token ]);
    },
    versionCheck:function(success, failure) {
        exec(success, failure, "HockeyApp", "versionCheck", []);
    },
    feedback:function(success, failure) {
        exec(success, failure, "HockeyApp", "feedback", []);
    }
};

module.exports = hockeyapp;
