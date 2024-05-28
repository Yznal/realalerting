import { ITenant } from 'app/entities/tenant/tenant.model';

export interface IClient {
  id: number;
  protocolProducerAddress?: string | null;
  protocolProducerPort?: number | null;
  protocolProducerUri?: string | null;
  protocolProducerStreamId?: number | null;
  protocolSubscriberAddress?: string | null;
  protocolSubscriberPort?: number | null;
  protocolSubscriberUri?: string | null;
  protocolSubscriberStreamId?: number | null;
  metricProducerAddress?: string | null;
  metricProducerPort?: number | null;
  metricProducerUri?: string | null;
  metricProducerStreamId?: number | null;
  tenant?: ITenant | null;
}

export type NewClient = Omit<IClient, 'id'> & { id: null };
