import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IAlert } from 'app/entities/alert/alert.model';
import { AlertService } from 'app/entities/alert/service/alert.service';
import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';
import { IAlertSubscriber } from '../alert-subscriber.model';
import { AlertSubscriberService } from '../service/alert-subscriber.service';
import { AlertSubscriberFormService } from './alert-subscriber-form.service';

import { AlertSubscriberUpdateComponent } from './alert-subscriber-update.component';

describe('AlertSubscriber Management Update Component', () => {
  let comp: AlertSubscriberUpdateComponent;
  let fixture: ComponentFixture<AlertSubscriberUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let alertSubscriberFormService: AlertSubscriberFormService;
  let alertSubscriberService: AlertSubscriberService;
  let alertService: AlertService;
  let clientService: ClientService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, AlertSubscriberUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(AlertSubscriberUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AlertSubscriberUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    alertSubscriberFormService = TestBed.inject(AlertSubscriberFormService);
    alertSubscriberService = TestBed.inject(AlertSubscriberService);
    alertService = TestBed.inject(AlertService);
    clientService = TestBed.inject(ClientService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Alert query and add missing value', () => {
      const alertSubscriber: IAlertSubscriber = { id: 456 };
      const alert: IAlert = { id: 8640 };
      alertSubscriber.alert = alert;

      const alertCollection: IAlert[] = [{ id: 28835 }];
      jest.spyOn(alertService, 'query').mockReturnValue(of(new HttpResponse({ body: alertCollection })));
      const additionalAlerts = [alert];
      const expectedCollection: IAlert[] = [...additionalAlerts, ...alertCollection];
      jest.spyOn(alertService, 'addAlertToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ alertSubscriber });
      comp.ngOnInit();

      expect(alertService.query).toHaveBeenCalled();
      expect(alertService.addAlertToCollectionIfMissing).toHaveBeenCalledWith(
        alertCollection,
        ...additionalAlerts.map(expect.objectContaining),
      );
      expect(comp.alertsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Client query and add missing value', () => {
      const alertSubscriber: IAlertSubscriber = { id: 456 };
      const client: IClient = { id: 12106 };
      alertSubscriber.client = client;

      const clientCollection: IClient[] = [{ id: 19986 }];
      jest.spyOn(clientService, 'query').mockReturnValue(of(new HttpResponse({ body: clientCollection })));
      const additionalClients = [client];
      const expectedCollection: IClient[] = [...additionalClients, ...clientCollection];
      jest.spyOn(clientService, 'addClientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ alertSubscriber });
      comp.ngOnInit();

      expect(clientService.query).toHaveBeenCalled();
      expect(clientService.addClientToCollectionIfMissing).toHaveBeenCalledWith(
        clientCollection,
        ...additionalClients.map(expect.objectContaining),
      );
      expect(comp.clientsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const alertSubscriber: IAlertSubscriber = { id: 456 };
      const alert: IAlert = { id: 6637 };
      alertSubscriber.alert = alert;
      const client: IClient = { id: 27029 };
      alertSubscriber.client = client;

      activatedRoute.data = of({ alertSubscriber });
      comp.ngOnInit();

      expect(comp.alertsSharedCollection).toContain(alert);
      expect(comp.clientsSharedCollection).toContain(client);
      expect(comp.alertSubscriber).toEqual(alertSubscriber);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAlertSubscriber>>();
      const alertSubscriber = { id: 123 };
      jest.spyOn(alertSubscriberFormService, 'getAlertSubscriber').mockReturnValue(alertSubscriber);
      jest.spyOn(alertSubscriberService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ alertSubscriber });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: alertSubscriber }));
      saveSubject.complete();

      // THEN
      expect(alertSubscriberFormService.getAlertSubscriber).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(alertSubscriberService.update).toHaveBeenCalledWith(expect.objectContaining(alertSubscriber));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAlertSubscriber>>();
      const alertSubscriber = { id: 123 };
      jest.spyOn(alertSubscriberFormService, 'getAlertSubscriber').mockReturnValue({ id: null });
      jest.spyOn(alertSubscriberService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ alertSubscriber: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: alertSubscriber }));
      saveSubject.complete();

      // THEN
      expect(alertSubscriberFormService.getAlertSubscriber).toHaveBeenCalled();
      expect(alertSubscriberService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAlertSubscriber>>();
      const alertSubscriber = { id: 123 };
      jest.spyOn(alertSubscriberService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ alertSubscriber });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(alertSubscriberService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareAlert', () => {
      it('Should forward to alertService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(alertService, 'compareAlert');
        comp.compareAlert(entity, entity2);
        expect(alertService.compareAlert).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareClient', () => {
      it('Should forward to clientService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(clientService, 'compareClient');
        comp.compareClient(entity, entity2);
        expect(clientService.compareClient).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
