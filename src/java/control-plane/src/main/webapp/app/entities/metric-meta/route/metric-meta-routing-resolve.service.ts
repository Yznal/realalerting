import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMetricMeta } from '../metric-meta.model';
import { MetricMetaService } from '../service/metric-meta.service';

const metricMetaResolve = (route: ActivatedRouteSnapshot): Observable<null | IMetricMeta> => {
  const id = route.params['id'];
  if (id) {
    return inject(MetricMetaService)
      .find(id)
      .pipe(
        mergeMap((metricMeta: HttpResponse<IMetricMeta>) => {
          if (metricMeta.body) {
            return of(metricMeta.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default metricMetaResolve;
