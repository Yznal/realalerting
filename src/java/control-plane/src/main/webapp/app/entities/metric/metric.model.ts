import { IClient } from 'app/entities/client/client.model';
import { MetricType } from 'app/entities/enumerations/metric-type.model';

export interface IMetric {
  id: number;
  type?: keyof typeof MetricType | null;
  name?: string | null;
  description?: string | null;
  client?: IClient | null;
}

export type NewMetric = Omit<IMetric, 'id'> & { id: null };
