import { IRealAlert, NewRealAlert } from './real-alert.model';

export const sampleWithRequiredData: IRealAlert = {
  id: 15244,
  type: 'REGULAR',
};

export const sampleWithPartialData: IRealAlert = {
  id: 86,
  type: 'CRITICAL',
  description: 'broker',
};

export const sampleWithFullData: IRealAlert = {
  id: 13280,
  type: 'CRITICAL',
  name: 'kookily whenever',
  description: 'boo instead interconnect',
  conf: 'facelift',
};

export const sampleWithNewData: NewRealAlert = {
  type: 'REGULAR',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
