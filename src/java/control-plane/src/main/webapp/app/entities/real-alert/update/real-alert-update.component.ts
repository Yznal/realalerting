import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';
import { IMetric } from 'app/entities/metric/metric.model';
import { MetricService } from 'app/entities/metric/service/metric.service';
import { AlertType } from 'app/entities/enumerations/alert-type.model';
import { RealAlertService } from '../service/real-alert.service';
import { IRealAlert } from '../real-alert.model';
import { RealAlertFormService, RealAlertFormGroup } from './real-alert-form.service';

@Component({
  standalone: true,
  selector: 'jhi-real-alert-update',
  templateUrl: './real-alert-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class RealAlertUpdateComponent implements OnInit {
  isSaving = false;
  realAlert: IRealAlert | null = null;
  alertTypeValues = Object.keys(AlertType);

  clientsSharedCollection: IClient[] = [];
  metricsSharedCollection: IMetric[] = [];

  protected realAlertService = inject(RealAlertService);
  protected realAlertFormService = inject(RealAlertFormService);
  protected clientService = inject(ClientService);
  protected metricService = inject(MetricService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RealAlertFormGroup = this.realAlertFormService.createRealAlertFormGroup();

  compareClient = (o1: IClient | null, o2: IClient | null): boolean => this.clientService.compareClient(o1, o2);

  compareMetric = (o1: IMetric | null, o2: IMetric | null): boolean => this.metricService.compareMetric(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ realAlert }) => {
      this.realAlert = realAlert;
      if (realAlert) {
        this.updateForm(realAlert);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const realAlert = this.realAlertFormService.getRealAlert(this.editForm);
    if (realAlert.id !== null) {
      this.subscribeToSaveResponse(this.realAlertService.update(realAlert));
    } else {
      this.subscribeToSaveResponse(this.realAlertService.create(realAlert));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRealAlert>>): void {
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

  protected updateForm(realAlert: IRealAlert): void {
    this.realAlert = realAlert;
    this.realAlertFormService.resetForm(this.editForm, realAlert);

    this.clientsSharedCollection = this.clientService.addClientToCollectionIfMissing<IClient>(
      this.clientsSharedCollection,
      realAlert.client,
    );
    this.metricsSharedCollection = this.metricService.addMetricToCollectionIfMissing<IMetric>(
      this.metricsSharedCollection,
      realAlert.metric,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.clientService
      .query()
      .pipe(map((res: HttpResponse<IClient[]>) => res.body ?? []))
      .pipe(map((clients: IClient[]) => this.clientService.addClientToCollectionIfMissing<IClient>(clients, this.realAlert?.client)))
      .subscribe((clients: IClient[]) => (this.clientsSharedCollection = clients));

    this.metricService
      .query()
      .pipe(map((res: HttpResponse<IMetric[]>) => res.body ?? []))
      .pipe(map((metrics: IMetric[]) => this.metricService.addMetricToCollectionIfMissing<IMetric>(metrics, this.realAlert?.metric)))
      .subscribe((metrics: IMetric[]) => (this.metricsSharedCollection = metrics));
  }
}
