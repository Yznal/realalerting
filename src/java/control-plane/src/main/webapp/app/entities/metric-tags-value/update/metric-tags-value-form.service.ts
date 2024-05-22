import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IMetricTagsValue, NewMetricTagsValue } from '../metric-tags-value.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMetricTagsValue for edit and NewMetricTagsValueFormGroupInput for create.
 */
type MetricTagsValueFormGroupInput = IMetricTagsValue | PartialWithRequiredKeyOf<NewMetricTagsValue>;

type MetricTagsValueFormDefaults = Pick<NewMetricTagsValue, 'id'>;

type MetricTagsValueFormGroupContent = {
  id: FormControl<IMetricTagsValue['id'] | NewMetricTagsValue['id']>;
  value01: FormControl<IMetricTagsValue['value01']>;
  value256: FormControl<IMetricTagsValue['value256']>;
  metric: FormControl<IMetricTagsValue['metric']>;
  tenant: FormControl<IMetricTagsValue['tenant']>;
};

export type MetricTagsValueFormGroup = FormGroup<MetricTagsValueFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MetricTagsValueFormService {
  createMetricTagsValueFormGroup(metricTagsValue: MetricTagsValueFormGroupInput = { id: null }): MetricTagsValueFormGroup {
    const metricTagsValueRawValue = {
      ...this.getFormDefaults(),
      ...metricTagsValue,
    };
    return new FormGroup<MetricTagsValueFormGroupContent>({
      id: new FormControl(
        { value: metricTagsValueRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      value01: new FormControl(metricTagsValueRawValue.value01),
      value256: new FormControl(metricTagsValueRawValue.value256),
      metric: new FormControl(metricTagsValueRawValue.metric, {
        validators: [Validators.required],
      }),
      tenant: new FormControl(metricTagsValueRawValue.tenant, {
        validators: [Validators.required],
      }),
    });
  }

  getMetricTagsValue(form: MetricTagsValueFormGroup): IMetricTagsValue | NewMetricTagsValue {
    return form.getRawValue() as IMetricTagsValue | NewMetricTagsValue;
  }

  resetForm(form: MetricTagsValueFormGroup, metricTagsValue: MetricTagsValueFormGroupInput): void {
    const metricTagsValueRawValue = { ...this.getFormDefaults(), ...metricTagsValue };
    form.reset(
      {
        ...metricTagsValueRawValue,
        id: { value: metricTagsValueRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MetricTagsValueFormDefaults {
    return {
      id: null,
    };
  }
}
