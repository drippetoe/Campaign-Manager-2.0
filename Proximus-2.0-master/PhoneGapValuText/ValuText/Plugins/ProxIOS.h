//
//  ProxIOS.h
//  ValuText
//
//  Created by David Shaw on 2/25/13.
//
//

#import <Cordova/CDV.h>

@interface ProxIOS : CDVPlugin
- (NSString *)getMacAddress;

- (void)macaddress:(CDVInvokedUrlCommand*)command;
- (void)mcc:(CDVInvokedUrlCommand*)command;
- (void)mnc:(CDVInvokedUrlCommand*)command;
- (void)carriername:(CDVInvokedUrlCommand*)command;
@end