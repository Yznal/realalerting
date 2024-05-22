import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tenant.test-samples';

import { TenantFormService } from './tenant-form.service';

describe('Tenant Form Service', () => {
  let service: TenantFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TenantFormService);
  });

  describe('Service methods', () => {
    describe('createTenantFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTenantFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing ITenant should create a new form with FormGroup', () => {
        const formGroup = service.createTenantFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getTenant', () => {
      it('should return NewTenant for default Tenant initial value', () => {
        const formGroup = service.createTenantFormGroup(sampleWithNewData);

        const tenant = service.getTenant(formGroup) as any;

        expect(tenant).toMatchObject(sampleWithNewData);
      });

      it('should return NewTenant for empty Tenant initial value', () => {
        const formGroup = service.createTenantFormGroup();

        const tenant = service.getTenant(formGroup) as any;

        expect(tenant).toMatchObject({});
      });

      it('should return ITenant', () => {
        const formGroup = service.createTenantFormGroup(sampleWithRequiredData);

        const tenant = service.getTenant(formGroup) as any;

        expect(tenant).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITenant should not enable id FormControl', () => {
        const formGroup = service.createTenantFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTenant should disable id FormControl', () => {
        const formGroup = service.createTenantFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
