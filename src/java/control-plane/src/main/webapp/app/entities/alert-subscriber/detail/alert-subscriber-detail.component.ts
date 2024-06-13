import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IAlertSubscriber } from '../alert-subscriber.model';

@Component({
  standalone: true,
  selector: 'jhi-alert-subscriber-detail',
  templateUrl: './alert-subscriber-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class AlertSubscriberDetailComponent {
  alertSubscriber = input<IAlertSubscriber | null>(null);

  previousState(): void {
    window.history.back();
  }
}
