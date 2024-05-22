import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../alert.test-samples';

import { AlertFormService } from './alert-form.service';

describe('Alert Form Service', () => {
  let service: AlertFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AlertFormService);
  });

  describe('Service methods', () => {
    describe('createAlertFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAlertFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            type: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            metric: expect.any(Object),
          }),
        );
      });

      it('passing IAlert should create a new form with FormGroup', () => {
        const formGroup = service.createAlertFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            type: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            metric: expect.any(Object),
          }),
        );
      });
    });

    describe('getAlert', () => {
      it('should return NewAlert for default Alert initial value', () => {
        const formGroup = service.createAlertFormGroup(sampleWithNewData);

        const alert = service.getAlert(formGroup) as any;

        expect(alert).toMatchObject(sampleWithNewData);
      });

      it('should return NewAlert for empty Alert initial value', () => {
        const formGroup = service.createAlertFormGroup();

        const alert = service.getAlert(formGroup) as any;

        expect(alert).toMatchObject({});
      });

      it('should return IAlert', () => {
        const formGroup = service.createAlertFormGroup(sampleWithRequiredData);

        const alert = service.getAlert(formGroup) as any;

        expect(alert).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAlert should not enable id FormControl', () => {
        const formGroup = service.createAlertFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAlert should disable id FormControl', () => {
        const formGroup = service.createAlertFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
