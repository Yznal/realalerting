import { ITenant } from 'app/entities/tenant/tenant.model';

export interface IMetricMeta {
  id: number;
  label01?: string | null;
  label256?: string | null;
  tenant?: ITenant | null;
}

export type NewMetricMeta = Omit<IMetricMeta, 'id'> & { id: null };
