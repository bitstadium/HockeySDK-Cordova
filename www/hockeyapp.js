var exec = require('cordova/exec');

var hockeyapp = {
    start: function (success, failure, token, crashManagerConfig) {
        var args = [token];
        // TODO: add more options
        //args order:
        // [autoSend: boolean, ignoreDefaultHandler: boolean]
        if (typeof crashManagerConfig === "object") {
            args.push(crashManagerConfig.autoSend ? crashManagerConfig.autoSend : false);
            args.push(crashManagerConfig.ignoreDefaultHandler ? crashManagerConfig.ignoreDefaultHandler : false);
        } else {
            // if crashManagerConfig is a boolean or string true, treat it as just autosend to maintain backwards compatibility
            var autoSend = (crashManagerConfig === true || crashManagerConfig === "true") ? true : false;
            args.push(autoSend.toString());
        }
        
        exec(success, failure, "HockeyApp", "start", args);
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
    }
};

module.exports = hockeyapp;
