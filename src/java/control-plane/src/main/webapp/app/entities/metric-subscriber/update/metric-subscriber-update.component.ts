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
import { MetricSubscriberService } from '../service/metric-subscriber.service';
import { IMetricSubscriber } from '../metric-subscriber.model';
import { MetricSubscriberFormService, MetricSubscriberFormGroup } from './metric-subscriber-form.service';

@Component({
  standalone: true,
  selector: 'jhi-metric-subscriber-update',
  templateUrl: './metric-subscriber-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MetricSubscriberUpdateComponent implements OnInit {
  isSaving = false;
  metricSubscriber: IMetricSubscriber | null = null;

  clientsSharedCollection: IClient[] = [];
  metricsSharedCollection: IMetric[] = [];

  protected metricSubscriberService = inject(MetricSubscriberService);
  protected metricSubscriberFormService = inject(MetricSubscriberFormService);
  protected clientService = inject(ClientService);
  protected metricService = inject(MetricService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MetricSubscriberFormGroup = this.metricSubscriberFormService.createMetricSubscriberFormGroup();

  compareClient = (o1: IClient | null, o2: IClient | null): boolean => this.clientService.compareClient(o1, o2);

  compareMetric = (o1: IMetric | null, o2: IMetric | null): boolean => this.metricService.compareMetric(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ metricSubscriber }) => {
      this.metricSubscriber = metricSubscriber;
      if (metricSubscriber) {
        this.updateForm(metricSubscriber);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const metricSubscriber = this.metricSubscriberFormService.getMetricSubscriber(this.editForm);
    if (metricSubscriber.id !== null) {
      this.subscribeToSaveResponse(this.metricSubscriberService.update(metricSubscriber));
    } else {
      this.subscribeToSaveResponse(this.metricSubscriberService.create(metricSubscriber));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMetricSubscriber>>): void {
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

  protected updateForm(metricSubscriber: IMetricSubscriber): void {
    this.metricSubscriber = metricSubscriber;
    this.metricSubscriberFormService.resetForm(this.editForm, metricSubscriber);

    this.clientsSharedCollection = this.clientService.addClientToCollectionIfMissing<IClient>(
      this.clientsSharedCollection,
      metricSubscriber.client,
    );
    this.metricsSharedCollection = this.metricService.addMetricToCollectionIfMissing<IMetric>(
      this.metricsSharedCollection,
      metricSubscriber.metric,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.clientService
      .query()
      .pipe(map((res: HttpResponse<IClient[]>) => res.body ?? []))
      .pipe(map((clients: IClient[]) => this.clientService.addClientToCollectionIfMissing<IClient>(clients, this.metricSubscriber?.client)))
      .subscribe((clients: IClient[]) => (this.clientsSharedCollection = clients));

    this.metricService
      .query()
      .pipe(map((res: HttpResponse<IMetric[]>) => res.body ?? []))
      .pipe(map((metrics: IMetric[]) => this.metricService.addMetricToCollectionIfMissing<IMetric>(metrics, this.metricSubscriber?.metric)))
      .subscribe((metrics: IMetric[]) => (this.metricsSharedCollection = metrics));
  }
}
