import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';
import { IRealAlert } from 'app/entities/real-alert/real-alert.model';
import { RealAlertService } from 'app/entities/real-alert/service/real-alert.service';
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
  let clientService: ClientService;
  let realAlertService: RealAlertService;

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
    clientService = TestBed.inject(ClientService);
    realAlertService = TestBed.inject(RealAlertService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Client query and add missing value', () => {
      const alertSubscriber: IAlertSubscriber = { id: 456 };
      const client: IClient = { id: 23340 };
      alertSubscriber.client = client;

      const clientCollection: IClient[] = [{ id: 3073 }];
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

    it('Should call RealAlert query and add missing value', () => {
      const alertSubscriber: IAlertSubscriber = { id: 456 };
      const realAlert: IRealAlert = { id: 1117 };
      alertSubscriber.realAlert = realAlert;

      const realAlertCollection: IRealAlert[] = [{ id: 8042 }];
      jest.spyOn(realAlertService, 'query').mockReturnValue(of(new HttpResponse({ body: realAlertCollection })));
      const additionalRealAlerts = [realAlert];
      const expectedCollection: IRealAlert[] = [...additionalRealAlerts, ...realAlertCollection];
      jest.spyOn(realAlertService, 'addRealAlertToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ alertSubscriber });
      comp.ngOnInit();

      expect(realAlertService.query).toHaveBeenCalled();
      expect(realAlertService.addRealAlertToCollectionIfMissing).toHaveBeenCalledWith(
        realAlertCollection,
        ...additionalRealAlerts.map(expect.objectContaining),
      );
      expect(comp.realAlertsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const alertSubscriber: IAlertSubscriber = { id: 456 };
      const client: IClient = { id: 18600 };
      alertSubscriber.client = client;
      const realAlert: IRealAlert = { id: 18764 };
      alertSubscriber.realAlert = realAlert;

      activatedRoute.data = of({ alertSubscriber });
      comp.ngOnInit();

      expect(comp.clientsSharedCollection).toContain(client);
      expect(comp.realAlertsSharedCollection).toContain(realAlert);
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
    describe('compareClient', () => {
      it('Should forward to clientService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(clientService, 'compareClient');
        comp.compareClient(entity, entity2);
        expect(clientService.compareClient).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareRealAlert', () => {
      it('Should forward to realAlertService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(realAlertService, 'compareRealAlert');
        comp.compareRealAlert(entity, entity2);
        expect(realAlertService.compareRealAlert).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
