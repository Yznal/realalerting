import { IClient, NewClient } from './client.model';

export const sampleWithRequiredData: IClient = {
  id: 9211,
};

export const sampleWithPartialData: IClient = {
  id: 31401,
  protocolAddress: 'except criticize',
  protocolUri: 'before',
  protocolStreamId: 6879,
  metricProducerPort: 14281,
  metricProducerStreamId: 12242,
};

export const sampleWithFullData: IClient = {
  id: 18974,
  protocolAddress: 'voluntarily even',
  protocolPort: 6392,
  protocolUri: 'disarmament enormous',
  protocolStreamId: 16473,
  metricProducerAddress: 'beech site',
  metricProducerPort: 20812,
  metricProducerUri: 'unless drafty beautifully',
  metricProducerStreamId: 8770,
};

export const sampleWithNewData: NewClient = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
