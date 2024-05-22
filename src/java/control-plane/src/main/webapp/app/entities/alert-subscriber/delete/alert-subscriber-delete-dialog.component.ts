import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IAlertSubscriber } from '../alert-subscriber.model';
import { AlertSubscriberService } from '../service/alert-subscriber.service';

@Component({
  standalone: true,
  templateUrl: './alert-subscriber-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class AlertSubscriberDeleteDialogComponent {
  alertSubscriber?: IAlertSubscriber;

  protected alertSubscriberService = inject(AlertSubscriberService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.alertSubscriberService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
