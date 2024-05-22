import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IMetric } from 'app/entities/metric/metric.model';
import { MetricService } from 'app/entities/metric/service/metric.service';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';
import { IMetricTagsValue } from '../metric-tags-value.model';
import { MetricTagsValueService } from '../service/metric-tags-value.service';
import { MetricTagsValueFormService } from './metric-tags-value-form.service';

import { MetricTagsValueUpdateComponent } from './metric-tags-value-update.component';

describe('MetricTagsValue Management Update Component', () => {
  let comp: MetricTagsValueUpdateComponent;
  let fixture: ComponentFixture<MetricTagsValueUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let metricTagsValueFormService: MetricTagsValueFormService;
  let metricTagsValueService: MetricTagsValueService;
  let metricService: MetricService;
  let tenantService: TenantService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, MetricTagsValueUpdateComponent],
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
      .overrideTemplate(MetricTagsValueUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MetricTagsValueUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    metricTagsValueFormService = TestBed.inject(MetricTagsValueFormService);
    metricTagsValueService = TestBed.inject(MetricTagsValueService);
    metricService = TestBed.inject(MetricService);
    tenantService = TestBed.inject(TenantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call metric query and add missing value', () => {
      const metricTagsValue: IMetricTagsValue = { id: 456 };
      const metric: IMetric = { id: 1523 };
      metricTagsValue.metric = metric;

      const metricCollection: IMetric[] = [{ id: 20824 }];
      jest.spyOn(metricService, 'query').mockReturnValue(of(new HttpResponse({ body: metricCollection })));
      const expectedCollection: IMetric[] = [metric, ...metricCollection];
      jest.spyOn(metricService, 'addMetricToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ metricTagsValue });
      comp.ngOnInit();

      expect(metricService.query).toHaveBeenCalled();
      expect(metricService.addMetricToCollectionIfMissing).toHaveBeenCalledWith(metricCollection, metric);
      expect(comp.metricsCollection).toEqual(expectedCollection);
    });

    it('Should call Tenant query and add missing value', () => {
      const metricTagsValue: IMetricTagsValue = { id: 456 };
      const tenant: ITenant = { id: 6266 };
      metricTagsValue.tenant = tenant;

      const tenantCollection: ITenant[] = [{ id: 30562 }];
      jest.spyOn(tenantService, 'query').mockReturnValue(of(new HttpResponse({ body: tenantCollection })));
      const additionalTenants = [tenant];
      const expectedCollection: ITenant[] = [...additionalTenants, ...tenantCollection];
      jest.spyOn(tenantService, 'addTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ metricTagsValue });
      comp.ngOnInit();

      expect(tenantService.query).toHaveBeenCalled();
      expect(tenantService.addTenantToCollectionIfMissing).toHaveBeenCalledWith(
        tenantCollection,
        ...additionalTenants.map(expect.objectContaining),
      );
      expect(comp.tenantsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const metricTagsValue: IMetricTagsValue = { id: 456 };
      const metric: IMetric = { id: 2776 };
      metricTagsValue.metric = metric;
      const tenant: ITenant = { id: 13455 };
      metricTagsValue.tenant = tenant;

      activatedRoute.data = of({ metricTagsValue });
      comp.ngOnInit();

      expect(comp.metricsCollection).toContain(metric);
      expect(comp.tenantsSharedCollection).toContain(tenant);
      expect(comp.metricTagsValue).toEqual(metricTagsValue);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetricTagsValue>>();
      const metricTagsValue = { id: 123 };
      jest.spyOn(metricTagsValueFormService, 'getMetricTagsValue').mockReturnValue(metricTagsValue);
      jest.spyOn(metricTagsValueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metricTagsValue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: metricTagsValue }));
      saveSubject.complete();

      // THEN
      expect(metricTagsValueFormService.getMetricTagsValue).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(metricTagsValueService.update).toHaveBeenCalledWith(expect.objectContaining(metricTagsValue));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetricTagsValue>>();
      const metricTagsValue = { id: 123 };
      jest.spyOn(metricTagsValueFormService, 'getMetricTagsValue').mockReturnValue({ id: null });
      jest.spyOn(metricTagsValueService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metricTagsValue: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: metricTagsValue }));
      saveSubject.complete();

      // THEN
      expect(metricTagsValueFormService.getMetricTagsValue).toHaveBeenCalled();
      expect(metricTagsValueService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetricTagsValue>>();
      const metricTagsValue = { id: 123 };
      jest.spyOn(metricTagsValueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metricTagsValue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(metricTagsValueService.update).toHaveBeenCalled();
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

    describe('compareTenant', () => {
      it('Should forward to tenantService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(tenantService, 'compareTenant');
        comp.compareTenant(entity, entity2);
        expect(tenantService.compareTenant).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
