#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>
#import "HockeyApp.h"
#import <HockeySDK/HockeySDK.h>

@interface HockeyApp : CDVPlugin <BITCrashManagerDelegate> {
    BOOL initialized;
    NSMutableDictionary *crashMetaData;
}

- (void)start:(CDVInvokedUrlCommand*)command;
- (void)feedback:(CDVInvokedUrlCommand*)command;
- (void)checkForUpdate:(CDVInvokedUrlCommand*)command;
- (void)forceCrash:(CDVInvokedUrlCommand*)command;
- (void)addMetaData:(CDVInvokedUrlCommand*)command;
- (void)trackEvent:(CDVInvokedUrlCommand*)command;

@end