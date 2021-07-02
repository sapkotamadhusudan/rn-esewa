import { NativeModules } from 'react-native';

type RnEsewaType = {
  multiply(a: number, b: number): Promise<number>;
};

const { RnEsewa } = NativeModules;

export default RnEsewa as RnEsewaType;
