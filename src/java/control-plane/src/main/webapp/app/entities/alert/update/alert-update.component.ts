import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMetric } from 'app/entities/metric/metric.model';
import { MetricService } from 'app/entities/metric/service/metric.service';
import { AlertType } from 'app/entities/enumerations/alert-type.model';
import { AlertService } from '../service/alert.service';
import { IAlert } from '../alert.model';
import { AlertFormService, AlertFormGroup } from './alert-form.service';

@Component({
  standalone: true,
  selector: 'jhi-alert-update',
  templateUrl: './alert-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AlertUpdateComponent implements OnInit {
  isSaving = false;
  alert: IAlert | null = null;
  alertTypeValues = Object.keys(AlertType);

  metricsSharedCollection: IMetric[] = [];

  protected alertService = inject(AlertService);
  protected alertFormService = inject(AlertFormService);
  protected metricService = inject(MetricService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AlertFormGroup = this.alertFormService.createAlertFormGroup();

  compareMetric = (o1: IMetric | null, o2: IMetric | null): boolean => this.metricService.compareMetric(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ alert }) => {
      this.alert = alert;
      if (alert) {
        this.updateForm(alert);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const alert = this.alertFormService.getAlert(this.editForm);
    if (alert.id !== null) {
      this.subscribeToSaveResponse(this.alertService.update(alert));
    } else {
      this.subscribeToSaveResponse(this.alertService.create(alert));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAlert>>): void {
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

  protected updateForm(alert: IAlert): void {
    this.alert = alert;
    this.alertFormService.resetForm(this.editForm, alert);

    this.metricsSharedCollection = this.metricService.addMetricToCollectionIfMissing<IMetric>(this.metricsSharedCollection, alert.metric);
  }

  protected loadRelationshipsOptions(): void {
    this.metricService
      .query()
      .pipe(map((res: HttpResponse<IMetric[]>) => res.body ?? []))
      .pipe(map((metrics: IMetric[]) => this.metricService.addMetricToCollectionIfMissing<IMetric>(metrics, this.alert?.metric)))
      .subscribe((metrics: IMetric[]) => (this.metricsSharedCollection = metrics));
  }
}
