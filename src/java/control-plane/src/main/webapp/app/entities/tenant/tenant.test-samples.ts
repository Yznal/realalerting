import { ITenant, NewTenant } from './tenant.model';

export const sampleWithRequiredData: ITenant = {
  id: 22070,
};

export const sampleWithPartialData: ITenant = {
  id: 18676,
  description: 'pish',
};

export const sampleWithFullData: ITenant = {
  id: 18774,
  name: 'ack mmm rubbery',
  description: 'geez sea jumbo',
};

export const sampleWithNewData: NewTenant = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
