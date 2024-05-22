import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';
import { MetricType } from 'app/entities/enumerations/metric-type.model';
import { MetricService } from '../service/metric.service';
import { IMetric } from '../metric.model';
import { MetricFormService, MetricFormGroup } from './metric-form.service';

@Component({
  standalone: true,
  selector: 'jhi-metric-update',
  templateUrl: './metric-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MetricUpdateComponent implements OnInit {
  isSaving = false;
  metric: IMetric | null = null;
  metricTypeValues = Object.keys(MetricType);

  clientsSharedCollection: IClient[] = [];

  protected metricService = inject(MetricService);
  protected metricFormService = inject(MetricFormService);
  protected clientService = inject(ClientService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MetricFormGroup = this.metricFormService.createMetricFormGroup();

  compareClient = (o1: IClient | null, o2: IClient | null): boolean => this.clientService.compareClient(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ metric }) => {
      this.metric = metric;
      if (metric) {
        this.updateForm(metric);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const metric = this.metricFormService.getMetric(this.editForm);
    if (metric.id !== null) {
      this.subscribeToSaveResponse(this.metricService.update(metric));
    } else {
      this.subscribeToSaveResponse(this.metricService.create(metric));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMetric>>): void {
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

  protected updateForm(metric: IMetric): void {
    this.metric = metric;
    this.metricFormService.resetForm(this.editForm, metric);

    this.clientsSharedCollection = this.clientService.addClientToCollectionIfMissing<IClient>(this.clientsSharedCollection, metric.client);
  }

  protected loadRelationshipsOptions(): void {
    this.clientService
      .query()
      .pipe(map((res: HttpResponse<IClient[]>) => res.body ?? []))
      .pipe(map((clients: IClient[]) => this.clientService.addClientToCollectionIfMissing<IClient>(clients, this.metric?.client)))
      .subscribe((clients: IClient[]) => (this.clientsSharedCollection = clients));
  }
}
