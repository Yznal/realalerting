import NavbarItem from 'app/layouts/navbar/navbar-item.model';

export const EntityNavbarItems: NavbarItem[] = [
  {
    name: 'MetricMeta',
    route: '/metric-meta',
    translationKey: 'global.menu.entities.metricMeta',
  },
  {
    name: 'Client',
    route: '/client',
    translationKey: 'global.menu.entities.client',
  },
  {
    name: 'MetricTagsValue',
    route: '/metric-tags-value',
    translationKey: 'global.menu.entities.metricTagsValue',
  },
  {
    name: 'Tenant',
    route: '/tenant',
    translationKey: 'global.menu.entities.tenant',
  },
  {
    name: 'Metric',
    route: '/metric',
    translationKey: 'global.menu.entities.metric',
  },
  {
    name: 'RealAlert',
    route: '/real-alert',
    translationKey: 'global.menu.entities.realAlert',
  },
  {
    name: 'AlertSubscriber',
    route: '/alert-subscriber',
    translationKey: 'global.menu.entities.alertSubscriber',
  },
  {
    name: 'MetricSubscriber',
    route: '/metric-subscriber',
    translationKey: 'global.menu.entities.metricSubscriber',
  },
];
