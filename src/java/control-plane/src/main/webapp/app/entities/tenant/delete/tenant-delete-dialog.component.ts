import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITenant } from '../tenant.model';
import { TenantService } from '../service/tenant.service';

@Component({
  standalone: true,
  templateUrl: './tenant-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TenantDeleteDialogComponent {
  tenant?: ITenant;

  protected tenantService = inject(TenantService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tenantService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
