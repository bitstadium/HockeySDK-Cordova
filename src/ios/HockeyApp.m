#import "HockeyApp.h"
#import <HockeySDK/HockeySDK.h>
#import <Cordova/CDVViewController.h>

@implementation HockeyApp

- (id)init
{
    self = [super init];
    initialized = NO;
    userEmail = nil;
    userName = nil;
    crashMetaData = [NSMutableDictionary new];
    shouldCreateNewFeedbackThread = NO;
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
        NSString* autoSend = [arguments objectAtIndex:3];
        NSString* createNewFeedbackThread = [arguments objectAtIndex:5];
        // no-op this for now. Appears to do nothing on ios side?
        // NSString* ignoreDefaultHandler = [arguments objectAtIndex:4];

        if ([autoSend boolValue] == YES) {
            [[BITHockeyManager sharedHockeyManager].crashManager setCrashManagerStatus:BITCrashManagerStatusAutoSend];
            [[NSUserDefaults standardUserDefaults] setInteger:BITCrashManagerStatusAutoSend forKey:@"BITCrashManagerStatus"];
        }

        if ([createNewFeedbackThread boolValue] == YES) {
            shouldCreateNewFeedbackThread = YES;
        }

        [[BITHockeyManager sharedHockeyManager] configureWithIdentifier:token
                                                               delegate:self];
        [[BITHockeyManager sharedHockeyManager] startManager];

        // Set authentication mode prior to verifying the user
        NSInteger authType = BITAuthenticatorIdentificationTypeAnonymous;
        [[BITHockeyManager sharedHockeyManager].authenticator setIdentificationType:BITAuthenticatorIdentificationTypeHockeyAppUser];
        if ([arguments count] >= 3) {
            NSString *authTypeString = [arguments objectAtIndex:1];
            authType = [authTypeString intValue];
            NSString *appSecret = [arguments objectAtIndex:2];

            [[BITHockeyManager sharedHockeyManager].authenticator setAuthenticationSecret:appSecret];
            [[BITHockeyManager sharedHockeyManager].authenticator setIdentificationType:authType];
        }

        if (authType == BITAuthenticatorIdentificationTypeAnonymous) {
            [[BITHockeyManager sharedHockeyManager].authenticator authenticateInstallation];
            initialized = YES;
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        } else {
            // Non-anonymous validation will crash the app, so return an error to indicate what is actually happening
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"hockeyapp cordova plugin: non-anonymous app validation not currently supported"];
        }
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"hockeyapp cordova plugin: missing arguments!"];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)setUserEmail:(CDVInvokedUrlCommand*)command {
    NSString* emailArgumentValue = [command argumentAtIndex:0];
    userEmail = emailArgumentValue;

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

- (void)setUserName:(CDVInvokedUrlCommand*)command {
    NSString* nameArgumentValue = [command argumentAtIndex:0];
    userName = nameArgumentValue;

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
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

- (void) composeFeedback:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;

    if(initialized == YES) {
        NSMutableArray* items = [NSMutableArray array];

        BOOL attachScreenshot = [[command argumentAtIndex:0] boolValue];
        if (attachScreenshot) {
            UIImage* screenshot = [[BITHockeyManager sharedHockeyManager].feedbackManager screenshot];
            [items addObject:screenshot];
        }

        if ([command.arguments count] > 1) {
            NSString* jsonData = [command argumentAtIndex:1];
            NSData* data = [jsonData dataUsingEncoding:NSUTF8StringEncoding];
            [items addObject:data];
        }

        [[BITHockeyManager sharedHockeyManager].feedbackManager showFeedbackComposeViewWithPreparedItems:items];
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

- (void) forceCrash:(CDVInvokedUrlCommand *)command {
    [[BITHockeyManager sharedHockeyManager].crashManager generateTestCrash];
}

- (void) addMetaData:(CDVInvokedUrlCommand *)command {
    NSData* arguments = [[command.arguments objectAtIndex:0] dataUsingEncoding:NSUTF8StringEncoding];
    CDVPluginResult* pluginResult = nil;

    if(initialized == YES) {
        NSString *documentsDirectory = [NSSearchPathForDirectoriesInDomains (NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
        NSString *fileName = [documentsDirectory stringByAppendingPathComponent:@"crashMetaData.txt"];
        if([[NSFileManager defaultManager] fileExistsAtPath:fileName]) {
            [[NSFileManager defaultManager] removeItemAtPath:fileName error:nil];
        }

        [[NSFileManager defaultManager] createFileAtPath:fileName contents:nil attributes:nil];

        if (crashMetaData == nil) {
            crashMetaData = [NSMutableDictionary new];
        }

        NSError *error;
        NSDictionary* newMetaData = [NSJSONSerialization JSONObjectWithData:arguments options:0 error:&error];
        [crashMetaData addEntriesFromDictionary:newMetaData];

        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:crashMetaData options:0 error:&error];
        if (jsonData) {
            NSString *crashLogString = [[NSString alloc] initWithBytes:[jsonData bytes] length:[jsonData length] encoding:NSUTF8StringEncoding];
            NSFileHandle *file = [NSFileHandle fileHandleForUpdatingAtPath:fileName];
            [file seekToEndOfFile];
            [file writeData:[crashLogString dataUsingEncoding:NSUTF8StringEncoding]];
            [file closeFile];
        }

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }
    else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"hockeyapp cordova plugin is not started, call hockeyapp.start(successcb, errorcb, hockeyapp_id) first!"];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) trackEvent:(CDVInvokedUrlCommand *)command {
    CDVPluginResult* pluginResult = nil;
    BITMetricsManager *metricsManager = [[BITHockeyManager sharedHockeyManager] metricsManager];
    NSString *eventName = [command argumentAtIndex:0 withDefault:nil andClass:[NSString class]];
    if (initialized == YES) {
        if (eventName) {
            [metricsManager trackEventWithName:eventName];
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                             messageAsString:@"hockeyapp cordova plugin: an event name must be provided."];
        }
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"hockeyapp cordova plugin is not started, call hockeyapp.start(successcb, errorcb, hockeyapp_id) first!"];
    }

    [self.commandDelegate sendPluginResult:pluginResult
                                callbackId:command.callbackId];
}

#pragma mark - BITHockeyManagerDelegate

- (NSString *)userEmailForHockeyManager:(BITHockeyManager *)hockeyManager componentManager:(BITHockeyBaseManager *)componentManager {
    return userEmail;
}

- (NSString *)userNameForHockeyManager:(BITHockeyManager *)hockeyManager componentManager:(BITHockeyBaseManager *)componentManager {
    return userName;
}

#pragma mark - BITCrashManagerDelegate

- (NSString *)applicationLogForCrashManager:(BITCrashManager *)crashManager {
    NSString *documentsDirectory = [NSSearchPathForDirectoriesInDomains (NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *fileName = [documentsDirectory stringByAppendingPathComponent:@"crashMetaData.txt"];

    NSString *logData = [NSString stringWithContentsOfFile:fileName encoding:NSUTF8StringEncoding error:nil];

    return logData;
}

#pragma mark - BITFeedbackManagerDelegate

- (BOOL)forceNewFeedbackThreadForFeedbackManager:(BITFeedbackManager *)feedbackManager {
    return shouldCreateNewFeedbackThread;
}

@end
