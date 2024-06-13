import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IMetricTagsValue } from '../metric-tags-value.model';

@Component({
  standalone: true,
  selector: 'jhi-metric-tags-value-detail',
  templateUrl: './metric-tags-value-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class MetricTagsValueDetailComponent {
  metricTagsValue = input<IMetricTagsValue | null>(null);

  previousState(): void {
    window.history.back();
  }
}
