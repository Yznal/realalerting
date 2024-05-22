import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IClient } from '../client.model';

@Component({
  standalone: true,
  selector: 'jhi-client-detail',
  templateUrl: './client-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ClientDetailComponent {
  client = input<IClient | null>(null);

  previousState(): void {
    window.history.back();
  }
}
