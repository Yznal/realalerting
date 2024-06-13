import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../metric-tags-value.test-samples';

import { MetricTagsValueFormService } from './metric-tags-value-form.service';

describe('MetricTagsValue Form Service', () => {
  let service: MetricTagsValueFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MetricTagsValueFormService);
  });

  describe('Service methods', () => {
    describe('createMetricTagsValueFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMetricTagsValueFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            value1: expect.any(Object),
            value256: expect.any(Object),
            metric: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });

      it('passing IMetricTagsValue should create a new form with FormGroup', () => {
        const formGroup = service.createMetricTagsValueFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            value1: expect.any(Object),
            value256: expect.any(Object),
            metric: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });
    });

    describe('getMetricTagsValue', () => {
      it('should return NewMetricTagsValue for default MetricTagsValue initial value', () => {
        const formGroup = service.createMetricTagsValueFormGroup(sampleWithNewData);

        const metricTagsValue = service.getMetricTagsValue(formGroup) as any;

        expect(metricTagsValue).toMatchObject(sampleWithNewData);
      });

      it('should return NewMetricTagsValue for empty MetricTagsValue initial value', () => {
        const formGroup = service.createMetricTagsValueFormGroup();

        const metricTagsValue = service.getMetricTagsValue(formGroup) as any;

        expect(metricTagsValue).toMatchObject({});
      });

      it('should return IMetricTagsValue', () => {
        const formGroup = service.createMetricTagsValueFormGroup(sampleWithRequiredData);

        const metricTagsValue = service.getMetricTagsValue(formGroup) as any;

        expect(metricTagsValue).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMetricTagsValue should not enable id FormControl', () => {
        const formGroup = service.createMetricTagsValueFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMetricTagsValue should disable id FormControl', () => {
        const formGroup = service.createMetricTagsValueFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
