import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../metric-subscriber.test-samples';

import { MetricSubscriberFormService } from './metric-subscriber-form.service';

describe('MetricSubscriber Form Service', () => {
  let service: MetricSubscriberFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MetricSubscriberFormService);
  });

  describe('Service methods', () => {
    describe('createMetricSubscriberFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMetricSubscriberFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            subscriberAddress: expect.any(Object),
            subscriberPort: expect.any(Object),
            subscriberUri: expect.any(Object),
            subscriberStreamId: expect.any(Object),
            client: expect.any(Object),
            metric: expect.any(Object),
          }),
        );
      });

      it('passing IMetricSubscriber should create a new form with FormGroup', () => {
        const formGroup = service.createMetricSubscriberFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            subscriberAddress: expect.any(Object),
            subscriberPort: expect.any(Object),
            subscriberUri: expect.any(Object),
            subscriberStreamId: expect.any(Object),
            client: expect.any(Object),
            metric: expect.any(Object),
          }),
        );
      });
    });

    describe('getMetricSubscriber', () => {
      it('should return NewMetricSubscriber for default MetricSubscriber initial value', () => {
        const formGroup = service.createMetricSubscriberFormGroup(sampleWithNewData);

        const metricSubscriber = service.getMetricSubscriber(formGroup) as any;

        expect(metricSubscriber).toMatchObject(sampleWithNewData);
      });

      it('should return NewMetricSubscriber for empty MetricSubscriber initial value', () => {
        const formGroup = service.createMetricSubscriberFormGroup();

        const metricSubscriber = service.getMetricSubscriber(formGroup) as any;

        expect(metricSubscriber).toMatchObject({});
      });

      it('should return IMetricSubscriber', () => {
        const formGroup = service.createMetricSubscriberFormGroup(sampleWithRequiredData);

        const metricSubscriber = service.getMetricSubscriber(formGroup) as any;

        expect(metricSubscriber).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMetricSubscriber should not enable id FormControl', () => {
        const formGroup = service.createMetricSubscriberFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMetricSubscriber should disable id FormControl', () => {
        const formGroup = service.createMetricSubscriberFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
