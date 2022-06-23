#import "wswAlipay.h"
#import <AlipaySDK/AlipaySDK.h>


@implementation wswAlipay

- (void)pluginInitialize {
    NSString* appId = [[self.commandDelegate settings] objectForKey:@"alipayid"];
    
    if (appId) {
        self.alipayId = appId;
    }else
    {
        self.alipayId = @"wswalipay";
    }
}


- (void)aliLogin:(CDVInvokedUrlCommand *)command
{
    NSLog(@"登录");
    
    NSDictionary *params = [command.arguments objectAtIndex:0];
    if (![params objectForKey:@"sign"]) {
        [self failWithCallbackID:command.callbackId withMessage:@"参数格式错误"];
        return ;
    }
    
    [[AlipaySDK defaultService] auth_V2WithInfo:params[@"sign"] fromScheme:self.alipayId callback:^(NSDictionary *resultDic) {
        NSLog(@"result=========== %@",resultDic);
        // 解析 auth code
        NSString *result = resultDic[@"result"];
        NSString *authCode = nil;
        if (result.length>0) {
            NSArray *resultArr = [result componentsSeparatedByString:@"&"];
            for (NSString *subResult in resultArr) {
                if (subResult.length > 10 && [subResult hasPrefix:@"auth_code="]) {
                    authCode = [subResult substringFromIndex:10];
                    [self successWithCallbackID:command.callbackId withMessage:authCode];
                    break;
                }
            }
        }else{
            [self failWithCallbackID:command.callbackId withMessage:@"登录失败"];
        }
                    
    }];
}

- (void)successWithCallbackID:(NSString *)callbackID withMessage:(NSString *)message
{
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:message];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callbackID];
}

- (void)successWithCallbackID:(NSString *)callbackID withMessageDic:(NSDictionary *)message
{
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:message];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callbackID];
}


- (void)failWithCallbackID:(NSString *)callbackID withMessage:(NSString *)message
{
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:message];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callbackID];
}

- (void)jumpPay:(CDVInvokedUrlCommand *)command
{
    NSDictionary *params = [command.arguments objectAtIndex:0];

    if (![params objectForKey:@"url"]) {
        [self failWithCallbackID:command.callbackId withMessage:@"参数格式错误"];
        return ;
    }
    NSString *pamurl = @"";
    if ([params objectForKey:@"pamurl"]) {
        pamurl =  [params[@"pamurl"] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet characterSetWithCharactersInString:@"!$'()*+,-./:;?@_~%#[]"]];
    }
    
    NSString *jumpAlipayStr = [NSString stringWithFormat:@"%@?%@",params[@"url"],pamurl];

    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:jumpAlipayStr] options:@{} completionHandler:^(BOOL success) {
         if (success) {
             [self successWithCallbackID:command.callbackId withMessage:@"跳转支付宝小程序"];
         }else{
             [self failWithCallbackID:command.callbackId withMessage:@"支付失败"];
         }
     }];
}

- (void)aliPay:(CDVInvokedUrlCommand *)command
{
    NSLog(@"支付");
    NSDictionary *params = [command.arguments objectAtIndex:0];
    if (![params objectForKey:@"sign"]) {
        [self failWithCallbackID:command.callbackId withMessage:@"参数格式错误"];
        return ;
    }
     self.currentCallbackId = command.callbackId;
    [[AlipaySDK defaultService] payOrder:params[@"sign"] fromScheme:self.alipayId callback:^(NSDictionary *resultDic) {
        NSString *resultStatus =[resultDic objectForKey:@"resultStatus"];
        
        if([resultStatus isEqualToString:@"9000"])
        {
            [self successWithCallbackID:command.callbackId withMessageDic:resultDic];
        }else{
            [self failWithCallbackID:command.callbackId withMessage:@"支付失败"];
        }
    
    }];
}

- (BOOL)handleAlipayOpenURL:(NSURL *)url
{

    [[AlipaySDK defaultService] processOrderWithPaymentResult:url standbyCallback:^(NSDictionary *resultDic) {
         NSString *resultStatus =[resultDic objectForKey:@"resultStatus"];
        
        if([resultStatus isEqualToString:@"9000"])
        {
            [self successWithCallbackID:self.currentCallbackId withMessageDic:resultDic];
        }else{
            [self failWithCallbackID:self.currentCallbackId withMessage:@"支付失败"];
        }
            
    }];
    [[AlipaySDK defaultService] processAuth_V2Result:url standbyCallback:^(NSDictionary *resultDic) {
        
    }];
    return YES;
}
@end
