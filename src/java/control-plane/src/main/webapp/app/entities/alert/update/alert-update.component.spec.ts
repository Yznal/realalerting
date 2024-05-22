import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IMetric } from 'app/entities/metric/metric.model';
import { MetricService } from 'app/entities/metric/service/metric.service';
import { AlertService } from '../service/alert.service';
import { IAlert } from '../alert.model';
import { AlertFormService } from './alert-form.service';

import { AlertUpdateComponent } from './alert-update.component';

describe('Alert Management Update Component', () => {
  let comp: AlertUpdateComponent;
  let fixture: ComponentFixture<AlertUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let alertFormService: AlertFormService;
  let alertService: AlertService;
  let metricService: MetricService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, AlertUpdateComponent],
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
      .overrideTemplate(AlertUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AlertUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    alertFormService = TestBed.inject(AlertFormService);
    alertService = TestBed.inject(AlertService);
    metricService = TestBed.inject(MetricService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Metric query and add missing value', () => {
      const alert: IAlert = { id: 456 };
      const metric: IMetric = { id: 22162 };
      alert.metric = metric;

      const metricCollection: IMetric[] = [{ id: 29839 }];
      jest.spyOn(metricService, 'query').mockReturnValue(of(new HttpResponse({ body: metricCollection })));
      const additionalMetrics = [metric];
      const expectedCollection: IMetric[] = [...additionalMetrics, ...metricCollection];
      jest.spyOn(metricService, 'addMetricToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ alert });
      comp.ngOnInit();

      expect(metricService.query).toHaveBeenCalled();
      expect(metricService.addMetricToCollectionIfMissing).toHaveBeenCalledWith(
        metricCollection,
        ...additionalMetrics.map(expect.objectContaining),
      );
      expect(comp.metricsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const alert: IAlert = { id: 456 };
      const metric: IMetric = { id: 32457 };
      alert.metric = metric;

      activatedRoute.data = of({ alert });
      comp.ngOnInit();

      expect(comp.metricsSharedCollection).toContain(metric);
      expect(comp.alert).toEqual(alert);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAlert>>();
      const alert = { id: 123 };
      jest.spyOn(alertFormService, 'getAlert').mockReturnValue(alert);
      jest.spyOn(alertService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ alert });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: alert }));
      saveSubject.complete();

      // THEN
      expect(alertFormService.getAlert).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(alertService.update).toHaveBeenCalledWith(expect.objectContaining(alert));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAlert>>();
      const alert = { id: 123 };
      jest.spyOn(alertFormService, 'getAlert').mockReturnValue({ id: null });
      jest.spyOn(alertService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ alert: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: alert }));
      saveSubject.complete();

      // THEN
      expect(alertFormService.getAlert).toHaveBeenCalled();
      expect(alertService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAlert>>();
      const alert = { id: 123 };
      jest.spyOn(alertService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ alert });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(alertService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
