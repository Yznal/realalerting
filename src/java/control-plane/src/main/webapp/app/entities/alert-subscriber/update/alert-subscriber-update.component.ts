import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IAlert } from 'app/entities/alert/alert.model';
import { AlertService } from 'app/entities/alert/service/alert.service';
import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';
import { AlertSubscriberService } from '../service/alert-subscriber.service';
import { IAlertSubscriber } from '../alert-subscriber.model';
import { AlertSubscriberFormService, AlertSubscriberFormGroup } from './alert-subscriber-form.service';

@Component({
  standalone: true,
  selector: 'jhi-alert-subscriber-update',
  templateUrl: './alert-subscriber-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AlertSubscriberUpdateComponent implements OnInit {
  isSaving = false;
  alertSubscriber: IAlertSubscriber | null = null;

  alertsSharedCollection: IAlert[] = [];
  clientsSharedCollection: IClient[] = [];

  protected alertSubscriberService = inject(AlertSubscriberService);
  protected alertSubscriberFormService = inject(AlertSubscriberFormService);
  protected alertService = inject(AlertService);
  protected clientService = inject(ClientService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AlertSubscriberFormGroup = this.alertSubscriberFormService.createAlertSubscriberFormGroup();

  compareAlert = (o1: IAlert | null, o2: IAlert | null): boolean => this.alertService.compareAlert(o1, o2);

  compareClient = (o1: IClient | null, o2: IClient | null): boolean => this.clientService.compareClient(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ alertSubscriber }) => {
      this.alertSubscriber = alertSubscriber;
      if (alertSubscriber) {
        this.updateForm(alertSubscriber);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const alertSubscriber = this.alertSubscriberFormService.getAlertSubscriber(this.editForm);
    if (alertSubscriber.id !== null) {
      this.subscribeToSaveResponse(this.alertSubscriberService.update(alertSubscriber));
    } else {
      this.subscribeToSaveResponse(this.alertSubscriberService.create(alertSubscriber));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAlertSubscriber>>): void {
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

  protected updateForm(alertSubscriber: IAlertSubscriber): void {
    this.alertSubscriber = alertSubscriber;
    this.alertSubscriberFormService.resetForm(this.editForm, alertSubscriber);

    this.alertsSharedCollection = this.alertService.addAlertToCollectionIfMissing<IAlert>(
      this.alertsSharedCollection,
      alertSubscriber.alert,
    );
    this.clientsSharedCollection = this.clientService.addClientToCollectionIfMissing<IClient>(
      this.clientsSharedCollection,
      alertSubscriber.client,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.alertService
      .query()
      .pipe(map((res: HttpResponse<IAlert[]>) => res.body ?? []))
      .pipe(map((alerts: IAlert[]) => this.alertService.addAlertToCollectionIfMissing<IAlert>(alerts, this.alertSubscriber?.alert)))
      .subscribe((alerts: IAlert[]) => (this.alertsSharedCollection = alerts));

    this.clientService
      .query()
      .pipe(map((res: HttpResponse<IClient[]>) => res.body ?? []))
      .pipe(map((clients: IClient[]) => this.clientService.addClientToCollectionIfMissing<IClient>(clients, this.alertSubscriber?.client)))
      .subscribe((clients: IClient[]) => (this.clientsSharedCollection = clients));
  }
}
