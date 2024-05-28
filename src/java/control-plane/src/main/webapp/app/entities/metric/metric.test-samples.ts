import { IMetric, NewMetric } from './metric.model';

export const sampleWithRequiredData: IMetric = {
  id: 14158,
  type: 'DOUBLE',
};

export const sampleWithPartialData: IMetric = {
  id: 19099,
  type: 'DOUBLE',
  criticalAlertProducerAddress: 'ah',
  criticalAlertProducerPort: 8188,
  criticalAlertProducerStreamId: 4566,
};

export const sampleWithFullData: IMetric = {
  id: 27435,
  type: 'INT',
  name: 'moist provided',
  description: 'rotating before furthermore',
  criticalAlertProducerAddress: 'worm standard cofactor',
  criticalAlertProducerPort: 3477,
  criticalAlertProducerUri: 'among green crowd',
  criticalAlertProducerStreamId: 4926,
};

export const sampleWithNewData: NewMetric = {
  type: 'INT',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
