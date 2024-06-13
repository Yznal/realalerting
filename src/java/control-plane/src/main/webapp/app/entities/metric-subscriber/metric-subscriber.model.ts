import { IClient } from 'app/entities/client/client.model';
import { IMetric } from 'app/entities/metric/metric.model';

export interface IMetricSubscriber {
  id: number;
  subscriberAddress?: string | null;
  subscriberPort?: number | null;
  subscriberUri?: string | null;
  subscriberStreamId?: number | null;
  client?: IClient | null;
  metric?: IMetric | null;
}

export type NewMetricSubscriber = Omit<IMetricSubscriber, 'id'> & { id: null };
