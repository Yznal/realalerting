import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AlertSubscriberComponent } from './list/alert-subscriber.component';
import { AlertSubscriberDetailComponent } from './detail/alert-subscriber-detail.component';
import { AlertSubscriberUpdateComponent } from './update/alert-subscriber-update.component';
import AlertSubscriberResolve from './route/alert-subscriber-routing-resolve.service';

const alertSubscriberRoute: Routes = [
  {
    path: '',
    component: AlertSubscriberComponent,
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AlertSubscriberDetailComponent,
    resolve: {
      alertSubscriber: AlertSubscriberResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AlertSubscriberUpdateComponent,
    resolve: {
      alertSubscriber: AlertSubscriberResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AlertSubscriberUpdateComponent,
    resolve: {
      alertSubscriber: AlertSubscriberResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default alertSubscriberRoute;
