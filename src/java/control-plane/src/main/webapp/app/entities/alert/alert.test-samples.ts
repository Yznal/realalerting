import { IAlert, NewAlert } from './alert.model';

export const sampleWithRequiredData: IAlert = {
  id: 17169,
  type: 'REGULAR',
};

export const sampleWithPartialData: IAlert = {
  id: 7059,
  type: 'CRITICAL',
  description: 'but',
};

export const sampleWithFullData: IAlert = {
  id: 32020,
  type: 'REGULAR',
  name: 'um',
  description: 'worst memorialise',
};

export const sampleWithNewData: NewAlert = {
  type: 'REGULAR',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
