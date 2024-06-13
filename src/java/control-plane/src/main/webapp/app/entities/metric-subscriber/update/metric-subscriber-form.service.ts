import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IMetricSubscriber, NewMetricSubscriber } from '../metric-subscriber.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMetricSubscriber for edit and NewMetricSubscriberFormGroupInput for create.
 */
type MetricSubscriberFormGroupInput = IMetricSubscriber | PartialWithRequiredKeyOf<NewMetricSubscriber>;

type MetricSubscriberFormDefaults = Pick<NewMetricSubscriber, 'id'>;

type MetricSubscriberFormGroupContent = {
  id: FormControl<IMetricSubscriber['id'] | NewMetricSubscriber['id']>;
  subscriberAddress: FormControl<IMetricSubscriber['subscriberAddress']>;
  subscriberPort: FormControl<IMetricSubscriber['subscriberPort']>;
  subscriberUri: FormControl<IMetricSubscriber['subscriberUri']>;
  subscriberStreamId: FormControl<IMetricSubscriber['subscriberStreamId']>;
  client: FormControl<IMetricSubscriber['client']>;
  metric: FormControl<IMetricSubscriber['metric']>;
};

export type MetricSubscriberFormGroup = FormGroup<MetricSubscriberFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MetricSubscriberFormService {
  createMetricSubscriberFormGroup(metricSubscriber: MetricSubscriberFormGroupInput = { id: null }): MetricSubscriberFormGroup {
    const metricSubscriberRawValue = {
      ...this.getFormDefaults(),
      ...metricSubscriber,
    };
    return new FormGroup<MetricSubscriberFormGroupContent>({
      id: new FormControl(
        { value: metricSubscriberRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      subscriberAddress: new FormControl(metricSubscriberRawValue.subscriberAddress),
      subscriberPort: new FormControl(metricSubscriberRawValue.subscriberPort),
      subscriberUri: new FormControl(metricSubscriberRawValue.subscriberUri),
      subscriberStreamId: new FormControl(metricSubscriberRawValue.subscriberStreamId),
      client: new FormControl(metricSubscriberRawValue.client, {
        validators: [Validators.required],
      }),
      metric: new FormControl(metricSubscriberRawValue.metric, {
        validators: [Validators.required],
      }),
    });
  }

  getMetricSubscriber(form: MetricSubscriberFormGroup): IMetricSubscriber | NewMetricSubscriber {
    return form.getRawValue() as IMetricSubscriber | NewMetricSubscriber;
  }

  resetForm(form: MetricSubscriberFormGroup, metricSubscriber: MetricSubscriberFormGroupInput): void {
    const metricSubscriberRawValue = { ...this.getFormDefaults(), ...metricSubscriber };
    form.reset(
      {
        ...metricSubscriberRawValue,
        id: { value: metricSubscriberRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MetricSubscriberFormDefaults {
    return {
      id: null,
    };
  }
}
