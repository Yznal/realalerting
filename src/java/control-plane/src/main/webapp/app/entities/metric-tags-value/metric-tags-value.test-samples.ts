import { IMetricTagsValue, NewMetricTagsValue } from './metric-tags-value.model';

export const sampleWithRequiredData: IMetricTagsValue = {
  id: 16653,
};

export const sampleWithPartialData: IMetricTagsValue = {
  id: 12025,
  value1: 'strictly short',
};

export const sampleWithFullData: IMetricTagsValue = {
  id: 17699,
  value1: 'peer',
  value256: 'despite',
};

export const sampleWithNewData: NewMetricTagsValue = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
