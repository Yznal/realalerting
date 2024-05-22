import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMetricTagsValue } from '../metric-tags-value.model';
import { MetricTagsValueService } from '../service/metric-tags-value.service';

const metricTagsValueResolve = (route: ActivatedRouteSnapshot): Observable<null | IMetricTagsValue> => {
  const id = route.params['id'];
  if (id) {
    return inject(MetricTagsValueService)
      .find(id)
      .pipe(
        mergeMap((metricTagsValue: HttpResponse<IMetricTagsValue>) => {
          if (metricTagsValue.body) {
            return of(metricTagsValue.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default metricTagsValueResolve;
