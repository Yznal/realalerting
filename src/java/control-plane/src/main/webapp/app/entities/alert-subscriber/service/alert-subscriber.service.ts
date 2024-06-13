import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAlertSubscriber, NewAlertSubscriber } from '../alert-subscriber.model';

export type PartialUpdateAlertSubscriber = Partial<IAlertSubscriber> & Pick<IAlertSubscriber, 'id'>;

export type EntityResponseType = HttpResponse<IAlertSubscriber>;
export type EntityArrayResponseType = HttpResponse<IAlertSubscriber[]>;

@Injectable({ providedIn: 'root' })
export class AlertSubscriberService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/alert-subscribers');

  create(alertSubscriber: NewAlertSubscriber): Observable<EntityResponseType> {
    return this.http.post<IAlertSubscriber>(this.resourceUrl, alertSubscriber, { observe: 'response' });
  }

  update(alertSubscriber: IAlertSubscriber): Observable<EntityResponseType> {
    return this.http.put<IAlertSubscriber>(`${this.resourceUrl}/${this.getAlertSubscriberIdentifier(alertSubscriber)}`, alertSubscriber, {
      observe: 'response',
    });
  }

  partialUpdate(alertSubscriber: PartialUpdateAlertSubscriber): Observable<EntityResponseType> {
    return this.http.patch<IAlertSubscriber>(`${this.resourceUrl}/${this.getAlertSubscriberIdentifier(alertSubscriber)}`, alertSubscriber, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAlertSubscriber>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAlertSubscriber[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAlertSubscriberIdentifier(alertSubscriber: Pick<IAlertSubscriber, 'id'>): number {
    return alertSubscriber.id;
  }

  compareAlertSubscriber(o1: Pick<IAlertSubscriber, 'id'> | null, o2: Pick<IAlertSubscriber, 'id'> | null): boolean {
    return o1 && o2 ? this.getAlertSubscriberIdentifier(o1) === this.getAlertSubscriberIdentifier(o2) : o1 === o2;
  }

  addAlertSubscriberToCollectionIfMissing<Type extends Pick<IAlertSubscriber, 'id'>>(
    alertSubscriberCollection: Type[],
    ...alertSubscribersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const alertSubscribers: Type[] = alertSubscribersToCheck.filter(isPresent);
    if (alertSubscribers.length > 0) {
      const alertSubscriberCollectionIdentifiers = alertSubscriberCollection.map(alertSubscriberItem =>
        this.getAlertSubscriberIdentifier(alertSubscriberItem),
      );
      const alertSubscribersToAdd = alertSubscribers.filter(alertSubscriberItem => {
        const alertSubscriberIdentifier = this.getAlertSubscriberIdentifier(alertSubscriberItem);
        if (alertSubscriberCollectionIdentifiers.includes(alertSubscriberIdentifier)) {
          return false;
        }
        alertSubscriberCollectionIdentifiers.push(alertSubscriberIdentifier);
        return true;
      });
      return [...alertSubscribersToAdd, ...alertSubscriberCollection];
    }
    return alertSubscriberCollection;
  }
}
