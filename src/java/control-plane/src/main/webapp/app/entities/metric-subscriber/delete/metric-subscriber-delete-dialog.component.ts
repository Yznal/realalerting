import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMetricSubscriber } from '../metric-subscriber.model';
import { MetricSubscriberService } from '../service/metric-subscriber.service';

@Component({
  standalone: true,
  templateUrl: './metric-subscriber-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MetricSubscriberDeleteDialogComponent {
  metricSubscriber?: IMetricSubscriber;

  protected metricSubscriberService = inject(MetricSubscriberService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.metricSubscriberService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
