import { IClient, NewClient } from './client.model';

export const sampleWithRequiredData: IClient = {
  id: 13318,
};

export const sampleWithPartialData: IClient = {
  id: 13559,
  protocolProducerPort: 8709,
  protocolProducerStreamId: 16276,
  protocolSubscriberUri: 'cone host unsightly',
  metricProducerAddress: 'disengagement for what',
  metricProducerPort: 21697,
  metricProducerUri: 'outgoing not',
  metricProducerStreamId: 30153,
};

export const sampleWithFullData: IClient = {
  id: 30846,
  protocolProducerAddress: 'deliberately',
  protocolProducerPort: 20399,
  protocolProducerUri: 'slur woot',
  protocolProducerStreamId: 1700,
  protocolSubscriberAddress: 'disloyal',
  protocolSubscriberPort: 4954,
  protocolSubscriberUri: 'boohoo artifact',
  protocolSubscriberStreamId: 10303,
  metricProducerAddress: 'interconnect',
  metricProducerPort: 16166,
  metricProducerUri: 'worth',
  metricProducerStreamId: 23891,
};

export const sampleWithNewData: NewClient = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
