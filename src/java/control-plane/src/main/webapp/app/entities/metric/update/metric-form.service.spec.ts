import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../metric.test-samples';

import { MetricFormService } from './metric-form.service';

describe('Metric Form Service', () => {
  let service: MetricFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MetricFormService);
  });

  describe('Service methods', () => {
    describe('createMetricFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMetricFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            type: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            criticalAlertProducerAddress: expect.any(Object),
            criticalAlertProducerPort: expect.any(Object),
            criticalAlertProducerUri: expect.any(Object),
            criticalAlertProducerStreamId: expect.any(Object),
            client: expect.any(Object),
          }),
        );
      });

      it('passing IMetric should create a new form with FormGroup', () => {
        const formGroup = service.createMetricFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            type: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            criticalAlertProducerAddress: expect.any(Object),
            criticalAlertProducerPort: expect.any(Object),
            criticalAlertProducerUri: expect.any(Object),
            criticalAlertProducerStreamId: expect.any(Object),
            client: expect.any(Object),
          }),
        );
      });
    });

    describe('getMetric', () => {
      it('should return NewMetric for default Metric initial value', () => {
        const formGroup = service.createMetricFormGroup(sampleWithNewData);

        const metric = service.getMetric(formGroup) as any;

        expect(metric).toMatchObject(sampleWithNewData);
      });

      it('should return NewMetric for empty Metric initial value', () => {
        const formGroup = service.createMetricFormGroup();

        const metric = service.getMetric(formGroup) as any;

        expect(metric).toMatchObject({});
      });

      it('should return IMetric', () => {
        const formGroup = service.createMetricFormGroup(sampleWithRequiredData);

        const metric = service.getMetric(formGroup) as any;

        expect(metric).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMetric should not enable id FormControl', () => {
        const formGroup = service.createMetricFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMetric should disable id FormControl', () => {
        const formGroup = service.createMetricFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
