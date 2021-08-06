#import <React/RCTBridgeModule.h>
#import <Foundation/Foundation.h>

@interface RCT_EXTERN_MODULE(RNEsewaSDK, NSObject)

RCT_EXTERN_METHOD(initiatePayment:(NSDictionary *)options callback:(RCTResponseSenderBlock)callback)

@end
