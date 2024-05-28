import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRealAlert } from '../real-alert.model';
import { RealAlertService } from '../service/real-alert.service';

const realAlertResolve = (route: ActivatedRouteSnapshot): Observable<null | IRealAlert> => {
  const id = route.params['id'];
  if (id) {
    return inject(RealAlertService)
      .find(id)
      .pipe(
        mergeMap((realAlert: HttpResponse<IRealAlert>) => {
          if (realAlert.body) {
            return of(realAlert.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default realAlertResolve;
