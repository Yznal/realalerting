import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';
import { MetricMetaService } from '../service/metric-meta.service';
import { IMetricMeta } from '../metric-meta.model';
import { MetricMetaFormService } from './metric-meta-form.service';

import { MetricMetaUpdateComponent } from './metric-meta-update.component';

describe('MetricMeta Management Update Component', () => {
  let comp: MetricMetaUpdateComponent;
  let fixture: ComponentFixture<MetricMetaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let metricMetaFormService: MetricMetaFormService;
  let metricMetaService: MetricMetaService;
  let tenantService: TenantService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, MetricMetaUpdateComponent],
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
      .overrideTemplate(MetricMetaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MetricMetaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    metricMetaFormService = TestBed.inject(MetricMetaFormService);
    metricMetaService = TestBed.inject(MetricMetaService);
    tenantService = TestBed.inject(TenantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call tenant query and add missing value', () => {
      const metricMeta: IMetricMeta = { id: 456 };
      const tenant: ITenant = { id: 17595 };
      metricMeta.tenant = tenant;

      const tenantCollection: ITenant[] = [{ id: 5142 }];
      jest.spyOn(tenantService, 'query').mockReturnValue(of(new HttpResponse({ body: tenantCollection })));
      const expectedCollection: ITenant[] = [tenant, ...tenantCollection];
      jest.spyOn(tenantService, 'addTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ metricMeta });
      comp.ngOnInit();

      expect(tenantService.query).toHaveBeenCalled();
      expect(tenantService.addTenantToCollectionIfMissing).toHaveBeenCalledWith(tenantCollection, tenant);
      expect(comp.tenantsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const metricMeta: IMetricMeta = { id: 456 };
      const tenant: ITenant = { id: 1379 };
      metricMeta.tenant = tenant;

      activatedRoute.data = of({ metricMeta });
      comp.ngOnInit();

      expect(comp.tenantsCollection).toContain(tenant);
      expect(comp.metricMeta).toEqual(metricMeta);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetricMeta>>();
      const metricMeta = { id: 123 };
      jest.spyOn(metricMetaFormService, 'getMetricMeta').mockReturnValue(metricMeta);
      jest.spyOn(metricMetaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metricMeta });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: metricMeta }));
      saveSubject.complete();

      // THEN
      expect(metricMetaFormService.getMetricMeta).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(metricMetaService.update).toHaveBeenCalledWith(expect.objectContaining(metricMeta));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetricMeta>>();
      const metricMeta = { id: 123 };
      jest.spyOn(metricMetaFormService, 'getMetricMeta').mockReturnValue({ id: null });
      jest.spyOn(metricMetaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metricMeta: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: metricMeta }));
      saveSubject.complete();

      // THEN
      expect(metricMetaFormService.getMetricMeta).toHaveBeenCalled();
      expect(metricMetaService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetricMeta>>();
      const metricMeta = { id: 123 };
      jest.spyOn(metricMetaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metricMeta });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(metricMetaService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
