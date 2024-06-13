import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';
import { IMetricMeta } from '../metric-meta.model';
import { MetricMetaService } from '../service/metric-meta.service';
import { MetricMetaFormService, MetricMetaFormGroup } from './metric-meta-form.service';

@Component({
  standalone: true,
  selector: 'jhi-metric-meta-update',
  templateUrl: './metric-meta-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MetricMetaUpdateComponent implements OnInit {
  isSaving = false;
  metricMeta: IMetricMeta | null = null;

  tenantsCollection: ITenant[] = [];

  protected metricMetaService = inject(MetricMetaService);
  protected metricMetaFormService = inject(MetricMetaFormService);
  protected tenantService = inject(TenantService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MetricMetaFormGroup = this.metricMetaFormService.createMetricMetaFormGroup();

  compareTenant = (o1: ITenant | null, o2: ITenant | null): boolean => this.tenantService.compareTenant(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ metricMeta }) => {
      this.metricMeta = metricMeta;
      if (metricMeta) {
        this.updateForm(metricMeta);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const metricMeta = this.metricMetaFormService.getMetricMeta(this.editForm);
    if (metricMeta.id !== null) {
      this.subscribeToSaveResponse(this.metricMetaService.update(metricMeta));
    } else {
      this.subscribeToSaveResponse(this.metricMetaService.create(metricMeta));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMetricMeta>>): void {
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

  protected updateForm(metricMeta: IMetricMeta): void {
    this.metricMeta = metricMeta;
    this.metricMetaFormService.resetForm(this.editForm, metricMeta);

    this.tenantsCollection = this.tenantService.addTenantToCollectionIfMissing<ITenant>(this.tenantsCollection, metricMeta.tenant);
  }

  protected loadRelationshipsOptions(): void {
    this.tenantService
      .query({ filter: 'metricmeta-is-null' })
      .pipe(map((res: HttpResponse<ITenant[]>) => res.body ?? []))
      .pipe(map((tenants: ITenant[]) => this.tenantService.addTenantToCollectionIfMissing<ITenant>(tenants, this.metricMeta?.tenant)))
      .subscribe((tenants: ITenant[]) => (this.tenantsCollection = tenants));
  }
}
