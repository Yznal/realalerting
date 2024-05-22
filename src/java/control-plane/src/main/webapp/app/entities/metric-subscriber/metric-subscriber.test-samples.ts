import { IMetricSubscriber, NewMetricSubscriber } from './metric-subscriber.model';

export const sampleWithRequiredData: IMetricSubscriber = {
  id: 10578,
};

export const sampleWithPartialData: IMetricSubscriber = {
  id: 21940,
  subscriberStreamId: 15947,
};

export const sampleWithFullData: IMetricSubscriber = {
  id: 8198,
  subscriberAddress: 'jubilantly profuse blah',
  subscriberPort: 845,
  subscriberUri: 'but phew snack',
  subscriberStreamId: 30931,
};

export const sampleWithNewData: NewMetricSubscriber = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
