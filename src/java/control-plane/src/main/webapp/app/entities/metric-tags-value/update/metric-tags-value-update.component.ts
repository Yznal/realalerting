import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMetric } from 'app/entities/metric/metric.model';
import { MetricService } from 'app/entities/metric/service/metric.service';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';
import { MetricTagsValueService } from '../service/metric-tags-value.service';
import { IMetricTagsValue } from '../metric-tags-value.model';
import { MetricTagsValueFormService, MetricTagsValueFormGroup } from './metric-tags-value-form.service';

@Component({
  standalone: true,
  selector: 'jhi-metric-tags-value-update',
  templateUrl: './metric-tags-value-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MetricTagsValueUpdateComponent implements OnInit {
  isSaving = false;
  metricTagsValue: IMetricTagsValue | null = null;

  metricsCollection: IMetric[] = [];
  tenantsSharedCollection: ITenant[] = [];

  protected metricTagsValueService = inject(MetricTagsValueService);
  protected metricTagsValueFormService = inject(MetricTagsValueFormService);
  protected metricService = inject(MetricService);
  protected tenantService = inject(TenantService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MetricTagsValueFormGroup = this.metricTagsValueFormService.createMetricTagsValueFormGroup();

  compareMetric = (o1: IMetric | null, o2: IMetric | null): boolean => this.metricService.compareMetric(o1, o2);

  compareTenant = (o1: ITenant | null, o2: ITenant | null): boolean => this.tenantService.compareTenant(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ metricTagsValue }) => {
      this.metricTagsValue = metricTagsValue;
      if (metricTagsValue) {
        this.updateForm(metricTagsValue);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const metricTagsValue = this.metricTagsValueFormService.getMetricTagsValue(this.editForm);
    if (metricTagsValue.id !== null) {
      this.subscribeToSaveResponse(this.metricTagsValueService.update(metricTagsValue));
    } else {
      this.subscribeToSaveResponse(this.metricTagsValueService.create(metricTagsValue));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMetricTagsValue>>): void {
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

  protected updateForm(metricTagsValue: IMetricTagsValue): void {
    this.metricTagsValue = metricTagsValue;
    this.metricTagsValueFormService.resetForm(this.editForm, metricTagsValue);

    this.metricsCollection = this.metricService.addMetricToCollectionIfMissing<IMetric>(this.metricsCollection, metricTagsValue.metric);
    this.tenantsSharedCollection = this.tenantService.addTenantToCollectionIfMissing<ITenant>(
      this.tenantsSharedCollection,
      metricTagsValue.tenant,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.metricService
      .query({ filter: 'metrictagsvalue-is-null' })
      .pipe(map((res: HttpResponse<IMetric[]>) => res.body ?? []))
      .pipe(map((metrics: IMetric[]) => this.metricService.addMetricToCollectionIfMissing<IMetric>(metrics, this.metricTagsValue?.metric)))
      .subscribe((metrics: IMetric[]) => (this.metricsCollection = metrics));

    this.tenantService
      .query()
      .pipe(map((res: HttpResponse<ITenant[]>) => res.body ?? []))
      .pipe(map((tenants: ITenant[]) => this.tenantService.addTenantToCollectionIfMissing<ITenant>(tenants, this.metricTagsValue?.tenant)))
      .subscribe((tenants: ITenant[]) => (this.tenantsSharedCollection = tenants));
  }
}
