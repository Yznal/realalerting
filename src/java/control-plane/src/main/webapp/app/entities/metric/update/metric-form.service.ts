import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IMetric, NewMetric } from '../metric.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMetric for edit and NewMetricFormGroupInput for create.
 */
type MetricFormGroupInput = IMetric | PartialWithRequiredKeyOf<NewMetric>;

type MetricFormDefaults = Pick<NewMetric, 'id'>;

type MetricFormGroupContent = {
  id: FormControl<IMetric['id'] | NewMetric['id']>;
  type: FormControl<IMetric['type']>;
  name: FormControl<IMetric['name']>;
  description: FormControl<IMetric['description']>;
  client: FormControl<IMetric['client']>;
};

export type MetricFormGroup = FormGroup<MetricFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MetricFormService {
  createMetricFormGroup(metric: MetricFormGroupInput = { id: null }): MetricFormGroup {
    const metricRawValue = {
      ...this.getFormDefaults(),
      ...metric,
    };
    return new FormGroup<MetricFormGroupContent>({
      id: new FormControl(
        { value: metricRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      type: new FormControl(metricRawValue.type, {
        validators: [Validators.required],
      }),
      name: new FormControl(metricRawValue.name),
      description: new FormControl(metricRawValue.description),
      client: new FormControl(metricRawValue.client, {
        validators: [Validators.required],
      }),
    });
  }

  getMetric(form: MetricFormGroup): IMetric | NewMetric {
    return form.getRawValue() as IMetric | NewMetric;
  }

  resetForm(form: MetricFormGroup, metric: MetricFormGroupInput): void {
    const metricRawValue = { ...this.getFormDefaults(), ...metric };
    form.reset(
      {
        ...metricRawValue,
        id: { value: metricRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MetricFormDefaults {
    return {
      id: null,
    };
  }
}
