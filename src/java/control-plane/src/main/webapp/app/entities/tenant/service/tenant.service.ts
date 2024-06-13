import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITenant, NewTenant } from '../tenant.model';

export type PartialUpdateTenant = Partial<ITenant> & Pick<ITenant, 'id'>;

export type EntityResponseType = HttpResponse<ITenant>;
export type EntityArrayResponseType = HttpResponse<ITenant[]>;

@Injectable({ providedIn: 'root' })
export class TenantService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tenants');

  create(tenant: NewTenant): Observable<EntityResponseType> {
    return this.http.post<ITenant>(this.resourceUrl, tenant, { observe: 'response' });
  }

  update(tenant: ITenant): Observable<EntityResponseType> {
    return this.http.put<ITenant>(`${this.resourceUrl}/${this.getTenantIdentifier(tenant)}`, tenant, { observe: 'response' });
  }

  partialUpdate(tenant: PartialUpdateTenant): Observable<EntityResponseType> {
    return this.http.patch<ITenant>(`${this.resourceUrl}/${this.getTenantIdentifier(tenant)}`, tenant, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITenant>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITenant[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTenantIdentifier(tenant: Pick<ITenant, 'id'>): number {
    return tenant.id;
  }

  compareTenant(o1: Pick<ITenant, 'id'> | null, o2: Pick<ITenant, 'id'> | null): boolean {
    return o1 && o2 ? this.getTenantIdentifier(o1) === this.getTenantIdentifier(o2) : o1 === o2;
  }

  addTenantToCollectionIfMissing<Type extends Pick<ITenant, 'id'>>(
    tenantCollection: Type[],
    ...tenantsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tenants: Type[] = tenantsToCheck.filter(isPresent);
    if (tenants.length > 0) {
      const tenantCollectionIdentifiers = tenantCollection.map(tenantItem => this.getTenantIdentifier(tenantItem));
      const tenantsToAdd = tenants.filter(tenantItem => {
        const tenantIdentifier = this.getTenantIdentifier(tenantItem);
        if (tenantCollectionIdentifiers.includes(tenantIdentifier)) {
          return false;
        }
        tenantCollectionIdentifiers.push(tenantIdentifier);
        return true;
      });
      return [...tenantsToAdd, ...tenantCollection];
    }
    return tenantCollection;
  }
}
