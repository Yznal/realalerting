import { IMetric } from 'app/entities/metric/metric.model';
import { AlertType } from 'app/entities/enumerations/alert-type.model';

export interface IAlert {
  id: number;
  type?: keyof typeof AlertType | null;
  name?: string | null;
  description?: string | null;
  metric?: IMetric | null;
}

export type NewAlert = Omit<IAlert, 'id'> & { id: null };
