import { NativeModules } from 'react-native';

type ProofOfPayment = {
  message: {
    successMessage: string;
    technicalSuccessMessage: string;
  };
  transactionDetails: {
    date: string;
    referenceId: string;
    status: string; // COMPLETED,
  };
  code: string;
  productId: string;
  productName: string;
  totalAmount: string;
  merchantName: string;
  environment: 'live' | 'test' | 'local';
};

type eSewaPaymentSuccessResponse = {
  completed: boolean;
  proofOfPayment: ProofOfPayment;
};

type eSewaPaymentErrorResponse = {
  hasError: boolean;
  errorMessage: string;
};

export type eSewaPaymentResponse = {
  didCancel?: boolean;
} & Partial<eSewaPaymentSuccessResponse> &
  Partial<eSewaPaymentErrorResponse>;

export type eSewaOptions = {
  isDevelopment: boolean;
  clientId: string;
  clientSecret: string;

  productPrice: string;
  productName: string;
  productId: string;
  callbackUrl: string;
};

type eSewaPaymentSDKType = {
  initiatePayment(
    options: eSewaOptions,
    callback: (response: eSewaPaymentResponse) => void
  ): void;
};

const eSewaPaymentSDK: eSewaPaymentSDKType | undefined =
  NativeModules.RNEsewaSDK;

// Produce an error if we don't have the native module
if (!eSewaPaymentSDK) {
  throw new Error(`rn-esewa: NativeModule.RNEsewaSDK is null. To fix this issue try these steps:
• Run \`react-native link rn-esewa\` in the project root.
• Rebuild and re-run the app.
• If you are using CocoaPods on iOS, run \`pod install\` in the \`ios\` directory and then rebuild and re-run the app. You may also need to re-open Xcode to get the new pods.
• Check that the library was linked correctly when you used the link command by running through the manual installation instructions in the README.
* If you are getting this error while unit testing you need to mock the native module. Follow the guide in the README.
If none of these fix the issue, please open an issue on the Github repository: https://git.healthathome.com.np/sapkotamadhusudan/rn-esewa`);
}

export default eSewaPaymentSDK as eSewaPaymentSDKType;
