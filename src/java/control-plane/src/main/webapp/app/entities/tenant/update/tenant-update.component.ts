import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITenant } from '../tenant.model';
import { TenantService } from '../service/tenant.service';
import { TenantFormService, TenantFormGroup } from './tenant-form.service';

@Component({
  standalone: true,
  selector: 'jhi-tenant-update',
  templateUrl: './tenant-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TenantUpdateComponent implements OnInit {
  isSaving = false;
  tenant: ITenant | null = null;

  protected tenantService = inject(TenantService);
  protected tenantFormService = inject(TenantFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TenantFormGroup = this.tenantFormService.createTenantFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tenant }) => {
      this.tenant = tenant;
      if (tenant) {
        this.updateForm(tenant);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tenant = this.tenantFormService.getTenant(this.editForm);
    if (tenant.id !== null) {
      this.subscribeToSaveResponse(this.tenantService.update(tenant));
    } else {
      this.subscribeToSaveResponse(this.tenantService.create(tenant));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITenant>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(tenant: ITenant): void {
    this.tenant = tenant;
    this.tenantFormService.resetForm(this.editForm, tenant);
  }
}
