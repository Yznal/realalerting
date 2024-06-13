import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../metric-meta.test-samples';

import { MetricMetaFormService } from './metric-meta-form.service';

describe('MetricMeta Form Service', () => {
  let service: MetricMetaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MetricMetaFormService);
  });

  describe('Service methods', () => {
    describe('createMetricMetaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMetricMetaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label1: expect.any(Object),
            label256: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });

      it('passing IMetricMeta should create a new form with FormGroup', () => {
        const formGroup = service.createMetricMetaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label1: expect.any(Object),
            label256: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });
    });

    describe('getMetricMeta', () => {
      it('should return NewMetricMeta for default MetricMeta initial value', () => {
        const formGroup = service.createMetricMetaFormGroup(sampleWithNewData);

        const metricMeta = service.getMetricMeta(formGroup) as any;

        expect(metricMeta).toMatchObject(sampleWithNewData);
      });

      it('should return NewMetricMeta for empty MetricMeta initial value', () => {
        const formGroup = service.createMetricMetaFormGroup();

        const metricMeta = service.getMetricMeta(formGroup) as any;

        expect(metricMeta).toMatchObject({});
      });

      it('should return IMetricMeta', () => {
        const formGroup = service.createMetricMetaFormGroup(sampleWithRequiredData);

        const metricMeta = service.getMetricMeta(formGroup) as any;

        expect(metricMeta).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMetricMeta should not enable id FormControl', () => {
        const formGroup = service.createMetricMetaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMetricMeta should disable id FormControl', () => {
        const formGroup = service.createMetricMetaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
