import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { TenantComponent } from './list/tenant.component';
import { TenantDetailComponent } from './detail/tenant-detail.component';
import { TenantUpdateComponent } from './update/tenant-update.component';
import TenantResolve from './route/tenant-routing-resolve.service';

const tenantRoute: Routes = [
  {
    path: '',
    component: TenantComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TenantDetailComponent,
    resolve: {
      tenant: TenantResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TenantUpdateComponent,
    resolve: {
      tenant: TenantResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TenantUpdateComponent,
    resolve: {
      tenant: TenantResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default tenantRoute;
