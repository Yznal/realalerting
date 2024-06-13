import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMetricSubscriber } from '../metric-subscriber.model';
import { MetricSubscriberService } from '../service/metric-subscriber.service';

const metricSubscriberResolve = (route: ActivatedRouteSnapshot): Observable<null | IMetricSubscriber> => {
  const id = route.params['id'];
  if (id) {
    return inject(MetricSubscriberService)
      .find(id)
      .pipe(
        mergeMap((metricSubscriber: HttpResponse<IMetricSubscriber>) => {
          if (metricSubscriber.body) {
            return of(metricSubscriber.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default metricSubscriberResolve;
