#import <Cordova/CDV.h>

@interface wswAlipay:CDVPlugin 

- (void)aliLogin:(CDVInvokedUrlCommand *)command;

- (void)aliPay:(CDVInvokedUrlCommand*)command;

- (void)jumpPay:(CDVInvokedUrlCommand *)command;

- (BOOL)handleAlipayOpenURL:(NSURL *)url;
@property (nonatomic, strong) NSString *alipayId;
@property (nonatomic, strong) NSString *currentCallbackId;

@end

