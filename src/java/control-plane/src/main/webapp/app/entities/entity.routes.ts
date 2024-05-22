import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'controlPlaneApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'metric-meta',
    data: { pageTitle: 'controlPlaneApp.metricMeta.home.title' },
    loadChildren: () => import('./metric-meta/metric-meta.routes'),
  },
  {
    path: 'client',
    data: { pageTitle: 'controlPlaneApp.client.home.title' },
    loadChildren: () => import('./client/client.routes'),
  },
  {
    path: 'metric-tags-value',
    data: { pageTitle: 'controlPlaneApp.metricTagsValue.home.title' },
    loadChildren: () => import('./metric-tags-value/metric-tags-value.routes'),
  },
  {
    path: 'tenant',
    data: { pageTitle: 'controlPlaneApp.tenant.home.title' },
    loadChildren: () => import('./tenant/tenant.routes'),
  },
  {
    path: 'metric',
    data: { pageTitle: 'controlPlaneApp.metric.home.title' },
    loadChildren: () => import('./metric/metric.routes'),
  },
  {
    path: 'alert',
    data: { pageTitle: 'controlPlaneApp.alert.home.title' },
    loadChildren: () => import('./alert/alert.routes'),
  },
  {
    path: 'alert-subscriber',
    data: { pageTitle: 'controlPlaneApp.alertSubscriber.home.title' },
    loadChildren: () => import('./alert-subscriber/alert-subscriber.routes'),
  },
  {
    path: 'metric-subscriber',
    data: { pageTitle: 'controlPlaneApp.metricSubscriber.home.title' },
    loadChildren: () => import('./metric-subscriber/metric-subscriber.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
