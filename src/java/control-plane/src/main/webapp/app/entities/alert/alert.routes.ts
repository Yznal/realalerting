import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { AlertComponent } from './list/alert.component';
import { AlertDetailComponent } from './detail/alert-detail.component';
import { AlertUpdateComponent } from './update/alert-update.component';
import AlertResolve from './route/alert-routing-resolve.service';

const alertRoute: Routes = [
  {
    path: '',
    component: AlertComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AlertDetailComponent,
    resolve: {
      alert: AlertResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AlertUpdateComponent,
    resolve: {
      alert: AlertResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AlertUpdateComponent,
    resolve: {
      alert: AlertResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default alertRoute;
