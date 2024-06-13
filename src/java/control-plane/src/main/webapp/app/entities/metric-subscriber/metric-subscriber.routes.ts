import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MetricSubscriberComponent } from './list/metric-subscriber.component';
import { MetricSubscriberDetailComponent } from './detail/metric-subscriber-detail.component';
import { MetricSubscriberUpdateComponent } from './update/metric-subscriber-update.component';
import MetricSubscriberResolve from './route/metric-subscriber-routing-resolve.service';

const metricSubscriberRoute: Routes = [
  {
    path: '',
    component: MetricSubscriberComponent,
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MetricSubscriberDetailComponent,
    resolve: {
      metricSubscriber: MetricSubscriberResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MetricSubscriberUpdateComponent,
    resolve: {
      metricSubscriber: MetricSubscriberResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MetricSubscriberUpdateComponent,
    resolve: {
      metricSubscriber: MetricSubscriberResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default metricSubscriberRoute;
