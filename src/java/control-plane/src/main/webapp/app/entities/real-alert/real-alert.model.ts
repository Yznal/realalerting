import { IClient } from 'app/entities/client/client.model';
import { IMetric } from 'app/entities/metric/metric.model';
import { AlertType } from 'app/entities/enumerations/alert-type.model';

export interface IRealAlert {
  id: number;
  type?: keyof typeof AlertType | null;
  name?: string | null;
  description?: string | null;
  conf?: string | null;
  client?: IClient | null;
  metric?: IMetric | null;
}

export type NewRealAlert = Omit<IRealAlert, 'id'> & { id: null };
