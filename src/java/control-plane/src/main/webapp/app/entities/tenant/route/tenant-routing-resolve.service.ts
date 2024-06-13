import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITenant } from '../tenant.model';
import { TenantService } from '../service/tenant.service';

const tenantResolve = (route: ActivatedRouteSnapshot): Observable<null | ITenant> => {
  const id = route.params['id'];
  if (id) {
    return inject(TenantService)
      .find(id)
      .pipe(
        mergeMap((tenant: HttpResponse<ITenant>) => {
          if (tenant.body) {
            return of(tenant.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default tenantResolve;
