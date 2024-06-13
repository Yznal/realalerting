import { IAlertSubscriber, NewAlertSubscriber } from './alert-subscriber.model';

export const sampleWithRequiredData: IAlertSubscriber = {
  id: 24467,
};

export const sampleWithPartialData: IAlertSubscriber = {
  id: 13476,
  subscriberAddress: 'whoa reporting',
  subscriberPort: 32339,
  subscriberUri: 'round nocturnal',
};

export const sampleWithFullData: IAlertSubscriber = {
  id: 23287,
  subscriberAddress: 'now',
  subscriberPort: 13676,
  subscriberUri: 'besides',
  subscriberStreamId: 14330,
};

export const sampleWithNewData: NewAlertSubscriber = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
