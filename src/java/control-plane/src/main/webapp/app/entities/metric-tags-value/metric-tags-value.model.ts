import { IMetric } from 'app/entities/metric/metric.model';
import { ITenant } from 'app/entities/tenant/tenant.model';

export interface IMetricTagsValue {
  id: number;
  value01?: string | null;
  value256?: string | null;
  metric?: IMetric | null;
  tenant?: ITenant | null;
}

export type NewMetricTagsValue = Omit<IMetricTagsValue, 'id'> & { id: null };
