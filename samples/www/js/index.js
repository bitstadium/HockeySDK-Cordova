var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);

        var crashButton = document.getElementById("crashMe");
        crashButton.addEventListener('click', this.crashMe, false);

        var crashWithDataButton = document.getElementById("crashWithData");
        crashWithDataButton.addEventListener('click', this.crashWithData, false);

        var feedbackButton = document.getElementById("feedback");
        feedbackButton.addEventListener('click', this.feedback, false);

        var composeFeedbackButton = document.getElementById("composeFeedback");
        composeFeedbackButton.addEventListener('click', this.composeFeedback, false);

        var updateButton = document.getElementById("update");
        updateButton.addEventListener('click', this.checkUpdates, false);

        var eventButton = document.getElementById("event");
        eventButton.addEventListener('click', this.trackEvent, false);
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        console.log("Device Ready!");
        hockeyapp.start(null, null, "<appid>");

        var status = document.getElementById("status");
        status.innerText = "Ready";

        var buttons = document.getElementById("buttons");
        buttons.style.visibility = "visible";

        cordova.getAppVersion.getVersionNumber().then((versionNumber) => {
            cordova.getAppVersion.getVersionCode().then((version) => {
                var versionElement = document.getElementById("version");
                versionElement.innerText = `${versionNumber} (${version})`;
            });
        });
    },

    crashMe: function() {
        console.log("Manual crash requested");

        var status = document.getElementById("status");
        status.innerText = "Crashed";

        hockeyapp.forceCrash();
    },

    crashWithData: function() {
        console.log("Manual crash with data requested");

        var status = document.getElementById("status");
        status.innerText = "Crash with data";

        hockeyapp.addMetaData(null, null, { someCustomProp: 23, anotherProp: "TestValue" });
        hockeyapp.forceCrash();
    },

    feedback: function() {
        console.log("Feedback requested");

        var status = document.getElementById("status");
        status.innerText = "Feedback requested";

        hockeyapp.feedback();
    },

    composeFeedback: function() {
        console.log("Compose feedback requested");

        var status = document.getElementById("status");
        status.innerText = "Compose feedback requested";

        hockeyapp.composeFeedback(null, null, true);
    },

    checkUpdates: function() {
        console.log("Update check requested");

        var status = document.getElementById("status");
        status.innerText = "Update check requested";

        hockeyapp.checkForUpdate();
    },

    trackEvent: function() {
        console.log("Custom event");

        var status = document.getElementById("status");
        var eventText = prompt("Event:", "Custom event");

        if (eventText) {
            hockeyapp.trackEvent(null, null, eventText);
            status.innerText = "Custom event: '" + eventText + "'";
        }
    }
};

app.initialize();
