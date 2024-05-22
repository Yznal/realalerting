import { IMetric, NewMetric } from './metric.model';

export const sampleWithRequiredData: IMetric = {
  id: 10457,
  type: 'DOUBLE',
};

export const sampleWithPartialData: IMetric = {
  id: 14158,
  type: 'DOUBLE',
  name: 'telex underneath solidify',
};

export const sampleWithFullData: IMetric = {
  id: 13381,
  type: 'DOUBLE',
  name: 'underperform sleepy',
  description: 'sorrowful utterly defenseless',
};

export const sampleWithNewData: NewMetric = {
  type: 'DOUBLE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
