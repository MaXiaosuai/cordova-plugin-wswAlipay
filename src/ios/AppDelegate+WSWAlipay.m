//
//  AppDelegate+Wechat.h
//  cordova-plugin-wechat
//
//  Created by Jason.z on 26/2/20.
//
//

#import "AppDelegate+WSWAlipay.h"
#import "wswAlipay.h"


#import <objc/runtime.h>

@implementation AppDelegate (WSWAlipay)


+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        Class obj = [self class];
        
        gx_swizzleSelector(obj, @selector(application:openURL:options:), @selector(setingServiceApplication:openURL:options:));
        
    });
}

- (BOOL)setingServiceApplication:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<NSString *,id> *)options{
    if ([url.host isEqualToString:@"safepay"]) {
        wswAlipay *cdvWechat = [self.viewController getCommandInstance:@"wswAlipay"];
        return [cdvWechat handleAlipayOpenURL:url];
    }else{
        return YES;
    }
}

static inline void gx_swizzleSelector(Class theClass, SEL originalSelector, SEL swizzledSelector) {
    Method originalMethod = class_getInstanceMethod(theClass, originalSelector);
    Method swizzledMethod = class_getInstanceMethod(theClass, swizzledSelector);
    
    BOOL didAddMethod =
    class_addMethod(theClass,
                    originalSelector,
                    method_getImplementation(swizzledMethod),
                    method_getTypeEncoding(swizzledMethod));
    
    if (didAddMethod) {
        class_replaceMethod(theClass,
                            swizzledSelector,
                            method_getImplementation(originalMethod),
                            method_getTypeEncoding(originalMethod));
    } else {
        method_exchangeImplementations(originalMethod, swizzledMethod);
    }
}


@end
