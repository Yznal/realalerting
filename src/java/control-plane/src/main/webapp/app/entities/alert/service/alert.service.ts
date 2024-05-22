import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAlert, NewAlert } from '../alert.model';

export type PartialUpdateAlert = Partial<IAlert> & Pick<IAlert, 'id'>;

export type EntityResponseType = HttpResponse<IAlert>;
export type EntityArrayResponseType = HttpResponse<IAlert[]>;

@Injectable({ providedIn: 'root' })
export class AlertService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/alerts');

  create(alert: NewAlert): Observable<EntityResponseType> {
    return this.http.post<IAlert>(this.resourceUrl, alert, { observe: 'response' });
  }

  update(alert: IAlert): Observable<EntityResponseType> {
    return this.http.put<IAlert>(`${this.resourceUrl}/${this.getAlertIdentifier(alert)}`, alert, { observe: 'response' });
  }

  partialUpdate(alert: PartialUpdateAlert): Observable<EntityResponseType> {
    return this.http.patch<IAlert>(`${this.resourceUrl}/${this.getAlertIdentifier(alert)}`, alert, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAlert>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAlert[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAlertIdentifier(alert: Pick<IAlert, 'id'>): number {
    return alert.id;
  }

  compareAlert(o1: Pick<IAlert, 'id'> | null, o2: Pick<IAlert, 'id'> | null): boolean {
    return o1 && o2 ? this.getAlertIdentifier(o1) === this.getAlertIdentifier(o2) : o1 === o2;
  }

  addAlertToCollectionIfMissing<Type extends Pick<IAlert, 'id'>>(
    alertCollection: Type[],
    ...alertsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const alerts: Type[] = alertsToCheck.filter(isPresent);
    if (alerts.length > 0) {
      const alertCollectionIdentifiers = alertCollection.map(alertItem => this.getAlertIdentifier(alertItem));
      const alertsToAdd = alerts.filter(alertItem => {
        const alertIdentifier = this.getAlertIdentifier(alertItem);
        if (alertCollectionIdentifiers.includes(alertIdentifier)) {
          return false;
        }
        alertCollectionIdentifiers.push(alertIdentifier);
        return true;
      });
      return [...alertsToAdd, ...alertCollection];
    }
    return alertCollection;
  }
}
