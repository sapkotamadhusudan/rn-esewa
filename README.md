# rn-esewa

A library providing esewa payment integration to react native applications

> :warning: **For IOS development**
> Please add following postinstall script to package.json
>
> This verifies and select the suitable esewa sdk of IOS for your environment.
>
> ```"postinstall": "sh node_modules/rn-esewa/scripts/select_esewa_ios_build.sh"```
>


## Installation

```sh
npm install rn-esewa

or

yarn add rn-esewa
```

## Usage

```js

    import eSewaPaymentSDK, { eSewaOptions, eSewaPaymentResponse } from 'rn-esewa';

    const options = {
        isDevelopment: true,
        clientId: <eSewa merchantId>,
        clientSecret: <eSewa merchantSecrect>,
        productId: new Date().getTime().toString(),
        productName: 'Payment for RN-Esewa Module',
        productPrice: '1',
        callbackUrl: 'https://your-backend-api.com',
    };

    const paymentCallback = (response: eSewaPaymentResponse) => {
      const { completed, proofOfPayment, didCancel, errorMessage } = response;

      if (completed) {
        console.debug('ProofOfPayment', proofOfPayment);
      } else if (didCancel) {
        console.info('Payment is canceled by user');
      } else {
        console.error(
          `Could not complete the payment due to ${
            errorMessage || 'an unknown error'
          }`
        );
      }
    };

    eSewaPaymentSDK.initiatePayment(options, paymentCallback);

```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
