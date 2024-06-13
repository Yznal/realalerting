import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MetricComponent } from './list/metric.component';
import { MetricDetailComponent } from './detail/metric-detail.component';
import { MetricUpdateComponent } from './update/metric-update.component';
import MetricResolve from './route/metric-routing-resolve.service';

const metricRoute: Routes = [
  {
    path: '',
    component: MetricComponent,
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MetricDetailComponent,
    resolve: {
      metric: MetricResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MetricUpdateComponent,
    resolve: {
      metric: MetricResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MetricUpdateComponent,
    resolve: {
      metric: MetricResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default metricRoute;
