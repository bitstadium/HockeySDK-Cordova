#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>

@interface HockeyApp : CDVPlugin {
    BOOL initialized;
}

- (void)start:(CDVInvokedUrlCommand*)command;
- (void)feedback:(CDVInvokedUrlCommand*)command;

@end
