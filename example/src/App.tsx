import React, { useState } from 'react';
import { View, Text, Button, StyleSheet } from 'react-native';

import eSewaPaymentSDK, { eSewaOptions, eSewaPaymentResponse } from 'rn-esewa';

const options: eSewaOptions = {
  isDevelopment: true,
  clientId: 'JB0BBQ4aD0UqIThFJwAKBgAXEUkEGQUBBAwdOgABHD4DChwUAB0R ',
  clientSecret: 'BhwIWQQADhIYSxILExMcAgFXFhcOBwAKBgAXEQ==',
  productId: new Date().getTime().toString(),
  productName: 'Payment for RN-Esewa Module',
  productPrice: '1',
  callbackUrl: 'https://your-backend-api.com',
};

const App = () => {
  return <EsewaPaymentComponent />;
};

const EsewaPaymentComponent = () => {
  const [result, setResult] = useState<any>('');

  const payWithEsewa = () => {
    const paymentCallback = (response: eSewaPaymentResponse) => {
      const { completed, proofOfPayment, didCancel, errorMessage } = response;

      if (completed) {
        setResult(proofOfPayment);
      } else if (didCancel) {
        setResult('Payment is canceled by user');
      } else {
        setResult(errorMessage + ' ' + proofOfPayment + ' ');
      }
    };

    eSewaPaymentSDK.initiatePayment(options, paymentCallback);
  };

  return (
    <View style={styles.container}>
      <Text>Payment Result: ${result}</Text>
      <Button title="Pay with Esewa" onPress={payWithEsewa} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center' },
});

export default App;
