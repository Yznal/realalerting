import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IMetricSubscriber } from '../metric-subscriber.model';

@Component({
  standalone: true,
  selector: 'jhi-metric-subscriber-detail',
  templateUrl: './metric-subscriber-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class MetricSubscriberDetailComponent {
  metricSubscriber = input<IMetricSubscriber | null>(null);

  previousState(): void {
    window.history.back();
  }
}
