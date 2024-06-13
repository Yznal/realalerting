import { IMetricMeta, NewMetricMeta } from './metric-meta.model';

export const sampleWithRequiredData: IMetricMeta = {
  id: 4042,
};

export const sampleWithPartialData: IMetricMeta = {
  id: 17951,
  label1: 'thistle worth expurgate',
};

export const sampleWithFullData: IMetricMeta = {
  id: 28123,
  label1: 'young regularly',
  label256: 'dark germinate along',
};

export const sampleWithNewData: NewMetricMeta = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
