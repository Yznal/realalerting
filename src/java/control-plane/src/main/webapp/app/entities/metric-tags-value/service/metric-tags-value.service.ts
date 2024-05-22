import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMetricTagsValue, NewMetricTagsValue } from '../metric-tags-value.model';

export type PartialUpdateMetricTagsValue = Partial<IMetricTagsValue> & Pick<IMetricTagsValue, 'id'>;

export type EntityResponseType = HttpResponse<IMetricTagsValue>;
export type EntityArrayResponseType = HttpResponse<IMetricTagsValue[]>;

@Injectable({ providedIn: 'root' })
export class MetricTagsValueService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/metric-tags-values');

  create(metricTagsValue: NewMetricTagsValue): Observable<EntityResponseType> {
    return this.http.post<IMetricTagsValue>(this.resourceUrl, metricTagsValue, { observe: 'response' });
  }

  update(metricTagsValue: IMetricTagsValue): Observable<EntityResponseType> {
    return this.http.put<IMetricTagsValue>(`${this.resourceUrl}/${this.getMetricTagsValueIdentifier(metricTagsValue)}`, metricTagsValue, {
      observe: 'response',
    });
  }

  partialUpdate(metricTagsValue: PartialUpdateMetricTagsValue): Observable<EntityResponseType> {
    return this.http.patch<IMetricTagsValue>(`${this.resourceUrl}/${this.getMetricTagsValueIdentifier(metricTagsValue)}`, metricTagsValue, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMetricTagsValue>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMetricTagsValue[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMetricTagsValueIdentifier(metricTagsValue: Pick<IMetricTagsValue, 'id'>): number {
    return metricTagsValue.id;
  }

  compareMetricTagsValue(o1: Pick<IMetricTagsValue, 'id'> | null, o2: Pick<IMetricTagsValue, 'id'> | null): boolean {
    return o1 && o2 ? this.getMetricTagsValueIdentifier(o1) === this.getMetricTagsValueIdentifier(o2) : o1 === o2;
  }

  addMetricTagsValueToCollectionIfMissing<Type extends Pick<IMetricTagsValue, 'id'>>(
    metricTagsValueCollection: Type[],
    ...metricTagsValuesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const metricTagsValues: Type[] = metricTagsValuesToCheck.filter(isPresent);
    if (metricTagsValues.length > 0) {
      const metricTagsValueCollectionIdentifiers = metricTagsValueCollection.map(metricTagsValueItem =>
        this.getMetricTagsValueIdentifier(metricTagsValueItem),
      );
      const metricTagsValuesToAdd = metricTagsValues.filter(metricTagsValueItem => {
        const metricTagsValueIdentifier = this.getMetricTagsValueIdentifier(metricTagsValueItem);
        if (metricTagsValueCollectionIdentifiers.includes(metricTagsValueIdentifier)) {
          return false;
        }
        metricTagsValueCollectionIdentifiers.push(metricTagsValueIdentifier);
        return true;
      });
      return [...metricTagsValuesToAdd, ...metricTagsValueCollection];
    }
    return metricTagsValueCollection;
  }
}
