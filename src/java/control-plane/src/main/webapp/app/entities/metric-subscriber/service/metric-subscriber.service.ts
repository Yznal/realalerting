import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMetricSubscriber, NewMetricSubscriber } from '../metric-subscriber.model';

export type PartialUpdateMetricSubscriber = Partial<IMetricSubscriber> & Pick<IMetricSubscriber, 'id'>;

export type EntityResponseType = HttpResponse<IMetricSubscriber>;
export type EntityArrayResponseType = HttpResponse<IMetricSubscriber[]>;

@Injectable({ providedIn: 'root' })
export class MetricSubscriberService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/metric-subscribers');

  create(metricSubscriber: NewMetricSubscriber): Observable<EntityResponseType> {
    return this.http.post<IMetricSubscriber>(this.resourceUrl, metricSubscriber, { observe: 'response' });
  }

  update(metricSubscriber: IMetricSubscriber): Observable<EntityResponseType> {
    return this.http.put<IMetricSubscriber>(
      `${this.resourceUrl}/${this.getMetricSubscriberIdentifier(metricSubscriber)}`,
      metricSubscriber,
      { observe: 'response' },
    );
  }

  partialUpdate(metricSubscriber: PartialUpdateMetricSubscriber): Observable<EntityResponseType> {
    return this.http.patch<IMetricSubscriber>(
      `${this.resourceUrl}/${this.getMetricSubscriberIdentifier(metricSubscriber)}`,
      metricSubscriber,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMetricSubscriber>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMetricSubscriber[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMetricSubscriberIdentifier(metricSubscriber: Pick<IMetricSubscriber, 'id'>): number {
    return metricSubscriber.id;
  }

  compareMetricSubscriber(o1: Pick<IMetricSubscriber, 'id'> | null, o2: Pick<IMetricSubscriber, 'id'> | null): boolean {
    return o1 && o2 ? this.getMetricSubscriberIdentifier(o1) === this.getMetricSubscriberIdentifier(o2) : o1 === o2;
  }

  addMetricSubscriberToCollectionIfMissing<Type extends Pick<IMetricSubscriber, 'id'>>(
    metricSubscriberCollection: Type[],
    ...metricSubscribersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const metricSubscribers: Type[] = metricSubscribersToCheck.filter(isPresent);
    if (metricSubscribers.length > 0) {
      const metricSubscriberCollectionIdentifiers = metricSubscriberCollection.map(metricSubscriberItem =>
        this.getMetricSubscriberIdentifier(metricSubscriberItem),
      );
      const metricSubscribersToAdd = metricSubscribers.filter(metricSubscriberItem => {
        const metricSubscriberIdentifier = this.getMetricSubscriberIdentifier(metricSubscriberItem);
        if (metricSubscriberCollectionIdentifiers.includes(metricSubscriberIdentifier)) {
          return false;
        }
        metricSubscriberCollectionIdentifiers.push(metricSubscriberIdentifier);
        return true;
      });
      return [...metricSubscribersToAdd, ...metricSubscriberCollection];
    }
    return metricSubscriberCollection;
  }
}
