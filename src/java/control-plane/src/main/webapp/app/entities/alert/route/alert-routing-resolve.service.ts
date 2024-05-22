import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAlert } from '../alert.model';
import { AlertService } from '../service/alert.service';

const alertResolve = (route: ActivatedRouteSnapshot): Observable<null | IAlert> => {
  const id = route.params['id'];
  if (id) {
    return inject(AlertService)
      .find(id)
      .pipe(
        mergeMap((alert: HttpResponse<IAlert>) => {
          if (alert.body) {
            return of(alert.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default alertResolve;
