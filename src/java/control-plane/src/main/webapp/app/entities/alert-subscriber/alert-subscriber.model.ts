import { IAlert } from 'app/entities/alert/alert.model';
import { IClient } from 'app/entities/client/client.model';

export interface IAlertSubscriber {
  id: number;
  subscriberAddress?: string | null;
  subscriberPort?: number | null;
  subscriberUri?: string | null;
  subscriberStreamId?: number | null;
  alert?: IAlert | null;
  client?: IClient | null;
}

export type NewAlertSubscriber = Omit<IAlertSubscriber, 'id'> & { id: null };
