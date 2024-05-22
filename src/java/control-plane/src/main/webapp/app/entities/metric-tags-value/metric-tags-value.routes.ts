import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { MetricTagsValueComponent } from './list/metric-tags-value.component';
import { MetricTagsValueDetailComponent } from './detail/metric-tags-value-detail.component';
import { MetricTagsValueUpdateComponent } from './update/metric-tags-value-update.component';
import MetricTagsValueResolve from './route/metric-tags-value-routing-resolve.service';

const metricTagsValueRoute: Routes = [
  {
    path: '',
    component: MetricTagsValueComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MetricTagsValueDetailComponent,
    resolve: {
      metricTagsValue: MetricTagsValueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MetricTagsValueUpdateComponent,
    resolve: {
      metricTagsValue: MetricTagsValueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MetricTagsValueUpdateComponent,
    resolve: {
      metricTagsValue: MetricTagsValueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default metricTagsValueRoute;
