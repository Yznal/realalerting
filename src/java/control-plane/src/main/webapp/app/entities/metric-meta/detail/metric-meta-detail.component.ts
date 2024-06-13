import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IMetricMeta } from '../metric-meta.model';

@Component({
  standalone: true,
  selector: 'jhi-metric-meta-detail',
  templateUrl: './metric-meta-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class MetricMetaDetailComponent {
  metricMeta = input<IMetricMeta | null>(null);

  previousState(): void {
    window.history.back();
  }
}
