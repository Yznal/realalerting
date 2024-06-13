import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { MetricMetaComponent } from './list/metric-meta.component';
import { MetricMetaDetailComponent } from './detail/metric-meta-detail.component';
import { MetricMetaUpdateComponent } from './update/metric-meta-update.component';
import MetricMetaResolve from './route/metric-meta-routing-resolve.service';

const metricMetaRoute: Routes = [
  {
    path: '',
    component: MetricMetaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MetricMetaDetailComponent,
    resolve: {
      metricMeta: MetricMetaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MetricMetaUpdateComponent,
    resolve: {
      metricMeta: MetricMetaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MetricMetaUpdateComponent,
    resolve: {
      metricMeta: MetricMetaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default metricMetaRoute;
