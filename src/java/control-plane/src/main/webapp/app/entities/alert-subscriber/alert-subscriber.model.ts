import { IClient } from 'app/entities/client/client.model';
import { IRealAlert } from 'app/entities/real-alert/real-alert.model';

export interface IAlertSubscriber {
  id: number;
  subscriberAddress?: string | null;
  subscriberPort?: number | null;
  subscriberUri?: string | null;
  subscriberStreamId?: number | null;
  client?: IClient | null;
  realAlert?: IRealAlert | null;
}

export type NewAlertSubscriber = Omit<IAlertSubscriber, 'id'> & { id: null };
