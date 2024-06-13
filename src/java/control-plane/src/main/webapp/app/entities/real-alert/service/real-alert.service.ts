import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRealAlert, NewRealAlert } from '../real-alert.model';

export type PartialUpdateRealAlert = Partial<IRealAlert> & Pick<IRealAlert, 'id'>;

export type EntityResponseType = HttpResponse<IRealAlert>;
export type EntityArrayResponseType = HttpResponse<IRealAlert[]>;

@Injectable({ providedIn: 'root' })
export class RealAlertService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/real-alerts');

  create(realAlert: NewRealAlert): Observable<EntityResponseType> {
    return this.http.post<IRealAlert>(this.resourceUrl, realAlert, { observe: 'response' });
  }

  update(realAlert: IRealAlert): Observable<EntityResponseType> {
    return this.http.put<IRealAlert>(`${this.resourceUrl}/${this.getRealAlertIdentifier(realAlert)}`, realAlert, { observe: 'response' });
  }

  partialUpdate(realAlert: PartialUpdateRealAlert): Observable<EntityResponseType> {
    return this.http.patch<IRealAlert>(`${this.resourceUrl}/${this.getRealAlertIdentifier(realAlert)}`, realAlert, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRealAlert>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRealAlert[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRealAlertIdentifier(realAlert: Pick<IRealAlert, 'id'>): number {
    return realAlert.id;
  }

  compareRealAlert(o1: Pick<IRealAlert, 'id'> | null, o2: Pick<IRealAlert, 'id'> | null): boolean {
    return o1 && o2 ? this.getRealAlertIdentifier(o1) === this.getRealAlertIdentifier(o2) : o1 === o2;
  }

  addRealAlertToCollectionIfMissing<Type extends Pick<IRealAlert, 'id'>>(
    realAlertCollection: Type[],
    ...realAlertsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const realAlerts: Type[] = realAlertsToCheck.filter(isPresent);
    if (realAlerts.length > 0) {
      const realAlertCollectionIdentifiers = realAlertCollection.map(realAlertItem => this.getRealAlertIdentifier(realAlertItem));
      const realAlertsToAdd = realAlerts.filter(realAlertItem => {
        const realAlertIdentifier = this.getRealAlertIdentifier(realAlertItem);
        if (realAlertCollectionIdentifiers.includes(realAlertIdentifier)) {
          return false;
        }
        realAlertCollectionIdentifiers.push(realAlertIdentifier);
        return true;
      });
      return [...realAlertsToAdd, ...realAlertCollection];
    }
    return realAlertCollection;
  }
}
