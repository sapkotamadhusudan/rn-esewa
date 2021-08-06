import Foundation
import UIKit
import EsewaSDK


@objc(RNEsewaSDK)
class RNEsewaSDK: NSObject, EsewaSDKPaymentDelegate {
  
  var lastCallback : RCTResponseSenderBlock?
  
  override init() {
    super.init()
  }

  @objc static func requiresMainQueueSetup() -> Bool {
    return true
  }

  @objc(initiatePayment:callback:)
  func initiatePayment(_ options: NSDictionary, callback: @escaping RCTResponseSenderBlock) -> Void {
    let response = NSMutableDictionary()
    
    if lastCallback != nil {
      response.setValue(true, forKey: "hasError")
      response.setValue("Previous payment is in progress", forKey: "errorMessage")
      callback([response])
      
      return
    }

    let clientId = options["clientId"] as? String ?? ""
    let clientSecret = options["clientSecret"] as? String ?? ""
    let productId = options["productId"] as? String ?? ""
    let productName = options["productName"] as? String ?? ""
    let productPrice = options["productPrice"] as? String ?? ""
    let callbackUrl = options["callbackUrl"] as? String ?? ""
    let isDevelopment = options["isDevelopment"] as? Bool ?? true

    if clientId.isEmpty {
      response.setValue(true, forKey: "hasError")
      response.setValue("ClientId is required", forKey: "errorMessage")
      callback([response])
      
      return
    }
    
    if clientSecret.isEmpty {
      response.setValue(true, forKey: "hasError")
      response.setValue("ClientSecretId is required", forKey: "errorMessage")
      callback([response])
      
      return
    }
    
    if productPrice.isEmpty {
      response.setValue(true, forKey: "hasError")
      response.setValue("Product Price is required", forKey: "errorMessage")
      callback([response])
      
      return
    }
    
    if productName.isEmpty {
      response.setValue(true, forKey: "hasError")
      response.setValue("Product Name is required", forKey: "errorMessage")
      callback([response])
      
      return
    }
    
    if productId.isEmpty {
      response.setValue(true, forKey: "hasError")
      response.setValue("ProductId is required", forKey: "errorMessage")
      callback([response])
      
      return
    }
    
    if callbackUrl.isEmpty {
      response.setValue(true, forKey: "hasError")
      response.setValue("callbackUrl is required", forKey: "errorMessage")
      callback([response])
      
      return
    }
    
    lastCallback = callback
    DispatchQueue.main.async {
      self.run(merchantId: clientId,
               merchantSecret: clientSecret,
               productName: productName,
               productAmount: productPrice,
               productId: productId,
               callbackUrl: callbackUrl,
               isDevelopment: isDevelopment
      )
    }
  }
  
  func run(
    merchantId: String,
    merchantSecret: String,
    productName: String,
    productAmount: String,
    productId: String,
    callbackUrl: String,
    isDevelopment: Bool
  ){
    let rvc = UIApplication.shared.windows.first?.rootViewController
    let sdk = EsewaSDK(inViewController: rvc!,
                   environment: isDevelopment ? .development : .production, delegate: self)
    sdk.initiatePayment(merchantId: merchantId, merchantSecret: merchantSecret, productName: "Test Product Name", productAmount: productAmount, productId: productId, callbackUrl: callbackUrl)
  }
  
  func onEsewaSDKPaymentSuccess(info: [String : Any]) {
    if lastCallback != nil {
      let response = NSMutableDictionary()
      response.setValue(true, forKey: "completed")
      response.setValue(info, forKey: "proofOfPayment")
      lastCallback!([response])
      lastCallback = nil
    }
  }
  
  func onEsewaSDKPaymentError(errorDescription: String) {
    if lastCallback != nil {
      let response = NSMutableDictionary()
      response.setValue(true, forKey: "hasError")
      response.setValue(errorDescription, forKey: "errorMessage")
      lastCallback!([response])
      lastCallback = nil
    }
  }
}
