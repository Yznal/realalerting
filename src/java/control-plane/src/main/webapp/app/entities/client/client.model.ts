import { ITenant } from 'app/entities/tenant/tenant.model';

export interface IClient {
  id: number;
  protocolAddress?: string | null;
  protocolPort?: number | null;
  protocolUri?: string | null;
  protocolStreamId?: number | null;
  metricProducerAddress?: string | null;
  metricProducerPort?: number | null;
  metricProducerUri?: string | null;
  metricProducerStreamId?: number | null;
  tenant?: ITenant | null;
}

export type NewClient = Omit<IClient, 'id'> & { id: null };
