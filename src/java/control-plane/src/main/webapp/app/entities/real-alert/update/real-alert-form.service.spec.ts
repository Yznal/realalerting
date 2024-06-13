import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../real-alert.test-samples';

import { RealAlertFormService } from './real-alert-form.service';

describe('RealAlert Form Service', () => {
  let service: RealAlertFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RealAlertFormService);
  });

  describe('Service methods', () => {
    describe('createRealAlertFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRealAlertFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            type: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            conf: expect.any(Object),
            client: expect.any(Object),
            metric: expect.any(Object),
          }),
        );
      });

      it('passing IRealAlert should create a new form with FormGroup', () => {
        const formGroup = service.createRealAlertFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            type: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            conf: expect.any(Object),
            client: expect.any(Object),
            metric: expect.any(Object),
          }),
        );
      });
    });

    describe('getRealAlert', () => {
      it('should return NewRealAlert for default RealAlert initial value', () => {
        const formGroup = service.createRealAlertFormGroup(sampleWithNewData);

        const realAlert = service.getRealAlert(formGroup) as any;

        expect(realAlert).toMatchObject(sampleWithNewData);
      });

      it('should return NewRealAlert for empty RealAlert initial value', () => {
        const formGroup = service.createRealAlertFormGroup();

        const realAlert = service.getRealAlert(formGroup) as any;

        expect(realAlert).toMatchObject({});
      });

      it('should return IRealAlert', () => {
        const formGroup = service.createRealAlertFormGroup(sampleWithRequiredData);

        const realAlert = service.getRealAlert(formGroup) as any;

        expect(realAlert).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRealAlert should not enable id FormControl', () => {
        const formGroup = service.createRealAlertFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRealAlert should disable id FormControl', () => {
        const formGroup = service.createRealAlertFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
