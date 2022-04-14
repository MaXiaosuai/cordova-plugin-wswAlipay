#import <Cordova/CDV.h>

@interface wswAlipay:CDVPlugin 

- (void)aliLogin:(CDVInvokedUrlCommand *)command;

- (void)aliPay:(CDVInvokedUrlCommand*)command;

@end

