import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAlertSubscriber } from '../alert-subscriber.model';
import { AlertSubscriberService } from '../service/alert-subscriber.service';

const alertSubscriberResolve = (route: ActivatedRouteSnapshot): Observable<null | IAlertSubscriber> => {
  const id = route.params['id'];
  if (id) {
    return inject(AlertSubscriberService)
      .find(id)
      .pipe(
        mergeMap((alertSubscriber: HttpResponse<IAlertSubscriber>) => {
          if (alertSubscriber.body) {
            return of(alertSubscriber.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default alertSubscriberResolve;
