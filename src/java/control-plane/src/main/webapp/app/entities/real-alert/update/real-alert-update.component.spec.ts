import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';
import { IMetric } from 'app/entities/metric/metric.model';
import { MetricService } from 'app/entities/metric/service/metric.service';
import { IRealAlert } from '../real-alert.model';
import { RealAlertService } from '../service/real-alert.service';
import { RealAlertFormService } from './real-alert-form.service';

import { RealAlertUpdateComponent } from './real-alert-update.component';

describe('RealAlert Management Update Component', () => {
  let comp: RealAlertUpdateComponent;
  let fixture: ComponentFixture<RealAlertUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let realAlertFormService: RealAlertFormService;
  let realAlertService: RealAlertService;
  let clientService: ClientService;
  let metricService: MetricService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RealAlertUpdateComponent],
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
      .overrideTemplate(RealAlertUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RealAlertUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    realAlertFormService = TestBed.inject(RealAlertFormService);
    realAlertService = TestBed.inject(RealAlertService);
    clientService = TestBed.inject(ClientService);
    metricService = TestBed.inject(MetricService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Client query and add missing value', () => {
      const realAlert: IRealAlert = { id: 456 };
      const client: IClient = { id: 12825 };
      realAlert.client = client;

      const clientCollection: IClient[] = [{ id: 25552 }];
      jest.spyOn(clientService, 'query').mockReturnValue(of(new HttpResponse({ body: clientCollection })));
      const additionalClients = [client];
      const expectedCollection: IClient[] = [...additionalClients, ...clientCollection];
      jest.spyOn(clientService, 'addClientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ realAlert });
      comp.ngOnInit();

      expect(clientService.query).toHaveBeenCalled();
      expect(clientService.addClientToCollectionIfMissing).toHaveBeenCalledWith(
        clientCollection,
        ...additionalClients.map(expect.objectContaining),
      );
      expect(comp.clientsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Metric query and add missing value', () => {
      const realAlert: IRealAlert = { id: 456 };
      const metric: IMetric = { id: 18617 };
      realAlert.metric = metric;

      const metricCollection: IMetric[] = [{ id: 13272 }];
      jest.spyOn(metricService, 'query').mockReturnValue(of(new HttpResponse({ body: metricCollection })));
      const additionalMetrics = [metric];
      const expectedCollection: IMetric[] = [...additionalMetrics, ...metricCollection];
      jest.spyOn(metricService, 'addMetricToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ realAlert });
      comp.ngOnInit();

      expect(metricService.query).toHaveBeenCalled();
      expect(metricService.addMetricToCollectionIfMissing).toHaveBeenCalledWith(
        metricCollection,
        ...additionalMetrics.map(expect.objectContaining),
      );
      expect(comp.metricsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const realAlert: IRealAlert = { id: 456 };
      const client: IClient = { id: 7101 };
      realAlert.client = client;
      const metric: IMetric = { id: 14216 };
      realAlert.metric = metric;

      activatedRoute.data = of({ realAlert });
      comp.ngOnInit();

      expect(comp.clientsSharedCollection).toContain(client);
      expect(comp.metricsSharedCollection).toContain(metric);
      expect(comp.realAlert).toEqual(realAlert);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRealAlert>>();
      const realAlert = { id: 123 };
      jest.spyOn(realAlertFormService, 'getRealAlert').mockReturnValue(realAlert);
      jest.spyOn(realAlertService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ realAlert });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: realAlert }));
      saveSubject.complete();

      // THEN
      expect(realAlertFormService.getRealAlert).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(realAlertService.update).toHaveBeenCalledWith(expect.objectContaining(realAlert));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRealAlert>>();
      const realAlert = { id: 123 };
      jest.spyOn(realAlertFormService, 'getRealAlert').mockReturnValue({ id: null });
      jest.spyOn(realAlertService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ realAlert: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: realAlert }));
      saveSubject.complete();

      // THEN
      expect(realAlertFormService.getRealAlert).toHaveBeenCalled();
      expect(realAlertService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRealAlert>>();
      const realAlert = { id: 123 };
      jest.spyOn(realAlertService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ realAlert });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(realAlertService.update).toHaveBeenCalled();
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

    describe('compareMetric', () => {
      it('Should forward to metricService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(metricService, 'compareMetric');
        comp.compareMetric(entity, entity2);
        expect(metricService.compareMetric).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
