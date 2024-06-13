import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IRealAlert } from '../real-alert.model';

@Component({
  standalone: true,
  selector: 'jhi-real-alert-detail',
  templateUrl: './real-alert-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class RealAlertDetailComponent {
  realAlert = input<IRealAlert | null>(null);

  previousState(): void {
    window.history.back();
  }
}
