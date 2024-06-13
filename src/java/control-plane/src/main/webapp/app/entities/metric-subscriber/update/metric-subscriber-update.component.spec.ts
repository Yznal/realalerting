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
import { IMetricSubscriber } from '../metric-subscriber.model';
import { MetricSubscriberService } from '../service/metric-subscriber.service';
import { MetricSubscriberFormService } from './metric-subscriber-form.service';

import { MetricSubscriberUpdateComponent } from './metric-subscriber-update.component';

describe('MetricSubscriber Management Update Component', () => {
  let comp: MetricSubscriberUpdateComponent;
  let fixture: ComponentFixture<MetricSubscriberUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let metricSubscriberFormService: MetricSubscriberFormService;
  let metricSubscriberService: MetricSubscriberService;
  let clientService: ClientService;
  let metricService: MetricService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, MetricSubscriberUpdateComponent],
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
      .overrideTemplate(MetricSubscriberUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MetricSubscriberUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    metricSubscriberFormService = TestBed.inject(MetricSubscriberFormService);
    metricSubscriberService = TestBed.inject(MetricSubscriberService);
    clientService = TestBed.inject(ClientService);
    metricService = TestBed.inject(MetricService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Client query and add missing value', () => {
      const metricSubscriber: IMetricSubscriber = { id: 456 };
      const client: IClient = { id: 30935 };
      metricSubscriber.client = client;

      const clientCollection: IClient[] = [{ id: 14818 }];
      jest.spyOn(clientService, 'query').mockReturnValue(of(new HttpResponse({ body: clientCollection })));
      const additionalClients = [client];
      const expectedCollection: IClient[] = [...additionalClients, ...clientCollection];
      jest.spyOn(clientService, 'addClientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ metricSubscriber });
      comp.ngOnInit();

      expect(clientService.query).toHaveBeenCalled();
      expect(clientService.addClientToCollectionIfMissing).toHaveBeenCalledWith(
        clientCollection,
        ...additionalClients.map(expect.objectContaining),
      );
      expect(comp.clientsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Metric query and add missing value', () => {
      const metricSubscriber: IMetricSubscriber = { id: 456 };
      const metric: IMetric = { id: 4774 };
      metricSubscriber.metric = metric;

      const metricCollection: IMetric[] = [{ id: 4831 }];
      jest.spyOn(metricService, 'query').mockReturnValue(of(new HttpResponse({ body: metricCollection })));
      const additionalMetrics = [metric];
      const expectedCollection: IMetric[] = [...additionalMetrics, ...metricCollection];
      jest.spyOn(metricService, 'addMetricToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ metricSubscriber });
      comp.ngOnInit();

      expect(metricService.query).toHaveBeenCalled();
      expect(metricService.addMetricToCollectionIfMissing).toHaveBeenCalledWith(
        metricCollection,
        ...additionalMetrics.map(expect.objectContaining),
      );
      expect(comp.metricsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const metricSubscriber: IMetricSubscriber = { id: 456 };
      const client: IClient = { id: 27064 };
      metricSubscriber.client = client;
      const metric: IMetric = { id: 3014 };
      metricSubscriber.metric = metric;

      activatedRoute.data = of({ metricSubscriber });
      comp.ngOnInit();

      expect(comp.clientsSharedCollection).toContain(client);
      expect(comp.metricsSharedCollection).toContain(metric);
      expect(comp.metricSubscriber).toEqual(metricSubscriber);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetricSubscriber>>();
      const metricSubscriber = { id: 123 };
      jest.spyOn(metricSubscriberFormService, 'getMetricSubscriber').mockReturnValue(metricSubscriber);
      jest.spyOn(metricSubscriberService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metricSubscriber });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: metricSubscriber }));
      saveSubject.complete();

      // THEN
      expect(metricSubscriberFormService.getMetricSubscriber).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(metricSubscriberService.update).toHaveBeenCalledWith(expect.objectContaining(metricSubscriber));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetricSubscriber>>();
      const metricSubscriber = { id: 123 };
      jest.spyOn(metricSubscriberFormService, 'getMetricSubscriber').mockReturnValue({ id: null });
      jest.spyOn(metricSubscriberService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metricSubscriber: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: metricSubscriber }));
      saveSubject.complete();

      // THEN
      expect(metricSubscriberFormService.getMetricSubscriber).toHaveBeenCalled();
      expect(metricSubscriberService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetricSubscriber>>();
      const metricSubscriber = { id: 123 };
      jest.spyOn(metricSubscriberService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metricSubscriber });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(metricSubscriberService.update).toHaveBeenCalled();
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
