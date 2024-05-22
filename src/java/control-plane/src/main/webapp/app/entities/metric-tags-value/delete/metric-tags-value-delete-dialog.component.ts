import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMetricTagsValue } from '../metric-tags-value.model';
import { MetricTagsValueService } from '../service/metric-tags-value.service';

@Component({
  standalone: true,
  templateUrl: './metric-tags-value-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MetricTagsValueDeleteDialogComponent {
  metricTagsValue?: IMetricTagsValue;

  protected metricTagsValueService = inject(MetricTagsValueService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.metricTagsValueService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
