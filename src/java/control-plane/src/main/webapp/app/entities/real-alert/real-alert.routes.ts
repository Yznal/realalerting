import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RealAlertComponent } from './list/real-alert.component';
import { RealAlertDetailComponent } from './detail/real-alert-detail.component';
import { RealAlertUpdateComponent } from './update/real-alert-update.component';
import RealAlertResolve from './route/real-alert-routing-resolve.service';

const realAlertRoute: Routes = [
  {
    path: '',
    component: RealAlertComponent,
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RealAlertDetailComponent,
    resolve: {
      realAlert: RealAlertResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RealAlertUpdateComponent,
    resolve: {
      realAlert: RealAlertResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RealAlertUpdateComponent,
    resolve: {
      realAlert: RealAlertResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default realAlertRoute;
