import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';
import { MetricService } from '../service/metric.service';
import { IMetric } from '../metric.model';
import { MetricFormService } from './metric-form.service';

import { MetricUpdateComponent } from './metric-update.component';

describe('Metric Management Update Component', () => {
  let comp: MetricUpdateComponent;
  let fixture: ComponentFixture<MetricUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let metricFormService: MetricFormService;
  let metricService: MetricService;
  let clientService: ClientService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, MetricUpdateComponent],
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
      .overrideTemplate(MetricUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MetricUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    metricFormService = TestBed.inject(MetricFormService);
    metricService = TestBed.inject(MetricService);
    clientService = TestBed.inject(ClientService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Client query and add missing value', () => {
      const metric: IMetric = { id: 456 };
      const client: IClient = { id: 19346 };
      metric.client = client;

      const clientCollection: IClient[] = [{ id: 28663 }];
      jest.spyOn(clientService, 'query').mockReturnValue(of(new HttpResponse({ body: clientCollection })));
      const additionalClients = [client];
      const expectedCollection: IClient[] = [...additionalClients, ...clientCollection];
      jest.spyOn(clientService, 'addClientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ metric });
      comp.ngOnInit();

      expect(clientService.query).toHaveBeenCalled();
      expect(clientService.addClientToCollectionIfMissing).toHaveBeenCalledWith(
        clientCollection,
        ...additionalClients.map(expect.objectContaining),
      );
      expect(comp.clientsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const metric: IMetric = { id: 456 };
      const client: IClient = { id: 20733 };
      metric.client = client;

      activatedRoute.data = of({ metric });
      comp.ngOnInit();

      expect(comp.clientsSharedCollection).toContain(client);
      expect(comp.metric).toEqual(metric);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetric>>();
      const metric = { id: 123 };
      jest.spyOn(metricFormService, 'getMetric').mockReturnValue(metric);
      jest.spyOn(metricService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metric });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: metric }));
      saveSubject.complete();

      // THEN
      expect(metricFormService.getMetric).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(metricService.update).toHaveBeenCalledWith(expect.objectContaining(metric));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetric>>();
      const metric = { id: 123 };
      jest.spyOn(metricFormService, 'getMetric').mockReturnValue({ id: null });
      jest.spyOn(metricService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metric: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: metric }));
      saveSubject.complete();

      // THEN
      expect(metricFormService.getMetric).toHaveBeenCalled();
      expect(metricService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetric>>();
      const metric = { id: 123 };
      jest.spyOn(metricService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metric });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(metricService.update).toHaveBeenCalled();
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
  });
});
