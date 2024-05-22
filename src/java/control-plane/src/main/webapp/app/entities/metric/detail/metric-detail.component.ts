import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IMetric } from '../metric.model';

@Component({
  standalone: true,
  selector: 'jhi-metric-detail',
  templateUrl: './metric-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class MetricDetailComponent {
  metric = input<IMetric | null>(null);

  previousState(): void {
    window.history.back();
  }
}
