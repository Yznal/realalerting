import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMetricMeta, NewMetricMeta } from '../metric-meta.model';

export type PartialUpdateMetricMeta = Partial<IMetricMeta> & Pick<IMetricMeta, 'id'>;

export type EntityResponseType = HttpResponse<IMetricMeta>;
export type EntityArrayResponseType = HttpResponse<IMetricMeta[]>;

@Injectable({ providedIn: 'root' })
export class MetricMetaService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/metric-metas');

  create(metricMeta: NewMetricMeta): Observable<EntityResponseType> {
    return this.http.post<IMetricMeta>(this.resourceUrl, metricMeta, { observe: 'response' });
  }

  update(metricMeta: IMetricMeta): Observable<EntityResponseType> {
    return this.http.put<IMetricMeta>(`${this.resourceUrl}/${this.getMetricMetaIdentifier(metricMeta)}`, metricMeta, {
      observe: 'response',
    });
  }

  partialUpdate(metricMeta: PartialUpdateMetricMeta): Observable<EntityResponseType> {
    return this.http.patch<IMetricMeta>(`${this.resourceUrl}/${this.getMetricMetaIdentifier(metricMeta)}`, metricMeta, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMetricMeta>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMetricMeta[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMetricMetaIdentifier(metricMeta: Pick<IMetricMeta, 'id'>): number {
    return metricMeta.id;
  }

  compareMetricMeta(o1: Pick<IMetricMeta, 'id'> | null, o2: Pick<IMetricMeta, 'id'> | null): boolean {
    return o1 && o2 ? this.getMetricMetaIdentifier(o1) === this.getMetricMetaIdentifier(o2) : o1 === o2;
  }

  addMetricMetaToCollectionIfMissing<Type extends Pick<IMetricMeta, 'id'>>(
    metricMetaCollection: Type[],
    ...metricMetasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const metricMetas: Type[] = metricMetasToCheck.filter(isPresent);
    if (metricMetas.length > 0) {
      const metricMetaCollectionIdentifiers = metricMetaCollection.map(metricMetaItem => this.getMetricMetaIdentifier(metricMetaItem));
      const metricMetasToAdd = metricMetas.filter(metricMetaItem => {
        const metricMetaIdentifier = this.getMetricMetaIdentifier(metricMetaItem);
        if (metricMetaCollectionIdentifiers.includes(metricMetaIdentifier)) {
          return false;
        }
        metricMetaCollectionIdentifiers.push(metricMetaIdentifier);
        return true;
      });
      return [...metricMetasToAdd, ...metricMetaCollection];
    }
    return metricMetaCollection;
  }
}
