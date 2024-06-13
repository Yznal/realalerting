import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMetric } from '../metric.model';
import { MetricService } from '../service/metric.service';

const metricResolve = (route: ActivatedRouteSnapshot): Observable<null | IMetric> => {
  const id = route.params['id'];
  if (id) {
    return inject(MetricService)
      .find(id)
      .pipe(
        mergeMap((metric: HttpResponse<IMetric>) => {
          if (metric.body) {
            return of(metric.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default metricResolve;
