#import "HockeyApp.h"
#import <HockeySDK/HockeySDK.h>
#import <Cordova/CDVViewController.h>

@implementation HockeyApp

- (id)init
{
    self = [super init];
    initialized = NO;
    return self;
}

- (void) start:(CDVInvokedUrlCommand*)command
{
    NSArray* arguments = command.arguments;
    CDVPluginResult* pluginResult = nil;

    if (initialized == YES) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"hockeyapp cordova plugin: plugin is already started!"];
    } else if ([arguments count] > 1) {

        NSString* token = [arguments objectAtIndex:0];
        NSString* autoSend = [arguments objectAtIndex:1];

        if ([autoSend isEqual:@"true"]) {
            [[BITHockeyManager sharedHockeyManager].crashManager setCrashManagerStatus:BITCrashManagerStatusAutoSend];
        }

        [[BITHockeyManager sharedHockeyManager] configureWithIdentifier:token];
        [[BITHockeyManager sharedHockeyManager] startManager];
        [[BITHockeyManager sharedHockeyManager].authenticator authenticateInstallation];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        initialized = YES;

    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"hockeyapp cordova plugin: missing arguments!"];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) feedback:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    if(initialized == YES) {
        [[BITHockeyManager sharedHockeyManager].feedbackManager showFeedbackListView];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }
    else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"hockeyapp cordova plugin is not started, call hockeyapp.start(successcb, errorcb, appid) first!"];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) checkForUpdate:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    if(initialized == YES) {
        [[BITHockeyManager sharedHockeyManager].updateManager checkForUpdate];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }
    else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"hockeyapp cordova plugin is not started, call hockeyapp.start(successcb, errorcb, hockeyapp_id) first!"];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)forceCrash:(CDVInvokedUrlCommand *)command {
    [[BITHockeyManager sharedHockeyManager].crashManager generateTestCrash];
}

@end
