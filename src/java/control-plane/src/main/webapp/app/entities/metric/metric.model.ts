import { IClient } from 'app/entities/client/client.model';
import { MetricType } from 'app/entities/enumerations/metric-type.model';

export interface IMetric {
  id: number;
  type?: keyof typeof MetricType | null;
  name?: string | null;
  description?: string | null;
  criticalAlertProducerAddress?: string | null;
  criticalAlertProducerPort?: number | null;
  criticalAlertProducerUri?: string | null;
  criticalAlertProducerStreamId?: number | null;
  client?: IClient | null;
}

export type NewMetric = Omit<IMetric, 'id'> & { id: null };
