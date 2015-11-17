#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>

@interface HockeyApp : CDVPlugin {
    BOOL initialized;
    NSMutableDictionary crashMetaData;
}

- (void)start:(CDVInvokedUrlCommand*)command;
- (void)feedback:(CDVInvokedUrlCommand*)command;
- (void)checkForUpdate:(CDVInvokedUrlCommand*)command;
- (void)forceCrash:(CDVInvokedUrlCommand*)command;
- (void)addMetaData:(CDVInvokedUrlCommand*)command;

@end
