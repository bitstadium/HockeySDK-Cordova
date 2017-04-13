#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>
#import "HockeyApp.h"
#import <HockeySDK/HockeySDK.h>

@interface HockeyApp : CDVPlugin <BITHockeyManagerDelegate> {
    BOOL initialized;
    BOOL shouldCreateNewFeedbackThread;
    NSString *userEmail;
    NSString *userName;
    NSMutableDictionary *crashMetaData;
}

- (void)start:(CDVInvokedUrlCommand*)command;
- (void)setUserEmail:(CDVInvokedUrlCommand*)command;
- (void)setUserName:(CDVInvokedUrlCommand*)command;
- (void)feedback:(CDVInvokedUrlCommand*)command;
- (void)composeFeedback:(CDVInvokedUrlCommand*)command;
- (void)checkForUpdate:(CDVInvokedUrlCommand*)command;
- (void)forceCrash:(CDVInvokedUrlCommand*)command;
- (void)addMetaData:(CDVInvokedUrlCommand*)command;
- (void)trackEvent:(CDVInvokedUrlCommand*)command;

@end