import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IRealAlert } from '../real-alert.model';
import { RealAlertService } from '../service/real-alert.service';

@Component({
  standalone: true,
  templateUrl: './real-alert-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class RealAlertDeleteDialogComponent {
  realAlert?: IRealAlert;

  protected realAlertService = inject(RealAlertService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.realAlertService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
