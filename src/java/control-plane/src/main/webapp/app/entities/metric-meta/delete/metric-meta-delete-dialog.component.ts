import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMetricMeta } from '../metric-meta.model';
import { MetricMetaService } from '../service/metric-meta.service';

@Component({
  standalone: true,
  templateUrl: './metric-meta-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MetricMetaDeleteDialogComponent {
  metricMeta?: IMetricMeta;

  protected metricMetaService = inject(MetricMetaService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.metricMetaService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
