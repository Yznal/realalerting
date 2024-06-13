import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { TenantService } from '../service/tenant.service';
import { ITenant } from '../tenant.model';
import { TenantFormService } from './tenant-form.service';

import { TenantUpdateComponent } from './tenant-update.component';

describe('Tenant Management Update Component', () => {
  let comp: TenantUpdateComponent;
  let fixture: ComponentFixture<TenantUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tenantFormService: TenantFormService;
  let tenantService: TenantService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, TenantUpdateComponent],
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
      .overrideTemplate(TenantUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TenantUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tenantFormService = TestBed.inject(TenantFormService);
    tenantService = TestBed.inject(TenantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const tenant: ITenant = { id: 456 };

      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      expect(comp.tenant).toEqual(tenant);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITenant>>();
      const tenant = { id: 123 };
      jest.spyOn(tenantFormService, 'getTenant').mockReturnValue(tenant);
      jest.spyOn(tenantService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tenant }));
      saveSubject.complete();

      // THEN
      expect(tenantFormService.getTenant).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tenantService.update).toHaveBeenCalledWith(expect.objectContaining(tenant));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITenant>>();
      const tenant = { id: 123 };
      jest.spyOn(tenantFormService, 'getTenant').mockReturnValue({ id: null });
      jest.spyOn(tenantService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenant: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tenant }));
      saveSubject.complete();

      // THEN
      expect(tenantFormService.getTenant).toHaveBeenCalled();
      expect(tenantService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITenant>>();
      const tenant = { id: 123 };
      jest.spyOn(tenantService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tenantService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
