import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMetric } from '../metric.model';
import { MetricService } from '../service/metric.service';

@Component({
  standalone: true,
  templateUrl: './metric-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MetricDeleteDialogComponent {
  metric?: IMetric;

  protected metricService = inject(MetricService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.metricService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
