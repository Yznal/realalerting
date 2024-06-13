import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMetric, NewMetric } from '../metric.model';

export type PartialUpdateMetric = Partial<IMetric> & Pick<IMetric, 'id'>;

export type EntityResponseType = HttpResponse<IMetric>;
export type EntityArrayResponseType = HttpResponse<IMetric[]>;

@Injectable({ providedIn: 'root' })
export class MetricService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/metrics');

  create(metric: NewMetric): Observable<EntityResponseType> {
    return this.http.post<IMetric>(this.resourceUrl, metric, { observe: 'response' });
  }

  update(metric: IMetric): Observable<EntityResponseType> {
    return this.http.put<IMetric>(`${this.resourceUrl}/${this.getMetricIdentifier(metric)}`, metric, { observe: 'response' });
  }

  partialUpdate(metric: PartialUpdateMetric): Observable<EntityResponseType> {
    return this.http.patch<IMetric>(`${this.resourceUrl}/${this.getMetricIdentifier(metric)}`, metric, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMetric>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMetric[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMetricIdentifier(metric: Pick<IMetric, 'id'>): number {
    return metric.id;
  }

  compareMetric(o1: Pick<IMetric, 'id'> | null, o2: Pick<IMetric, 'id'> | null): boolean {
    return o1 && o2 ? this.getMetricIdentifier(o1) === this.getMetricIdentifier(o2) : o1 === o2;
  }

  addMetricToCollectionIfMissing<Type extends Pick<IMetric, 'id'>>(
    metricCollection: Type[],
    ...metricsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const metrics: Type[] = metricsToCheck.filter(isPresent);
    if (metrics.length > 0) {
      const metricCollectionIdentifiers = metricCollection.map(metricItem => this.getMetricIdentifier(metricItem));
      const metricsToAdd = metrics.filter(metricItem => {
        const metricIdentifier = this.getMetricIdentifier(metricItem);
        if (metricCollectionIdentifiers.includes(metricIdentifier)) {
          return false;
        }
        metricCollectionIdentifiers.push(metricIdentifier);
        return true;
      });
      return [...metricsToAdd, ...metricCollection];
    }
    return metricCollection;
  }
}
