#import <Cordova/CDV.h>

@interface wswAlipay:CDVPlugin 

- (void)aliLogin:(CDVInvokedUrlCommand *)command;

- (void)aliPay:(CDVInvokedUrlCommand*)command;
- (BOOL)handleAlipayOpenURL:(NSURL *)url;
@property (nonatomic, strong) NSString *alipayId;

@end

