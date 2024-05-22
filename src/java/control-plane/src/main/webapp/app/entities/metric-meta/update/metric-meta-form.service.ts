import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IMetricMeta, NewMetricMeta } from '../metric-meta.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMetricMeta for edit and NewMetricMetaFormGroupInput for create.
 */
type MetricMetaFormGroupInput = IMetricMeta | PartialWithRequiredKeyOf<NewMetricMeta>;

type MetricMetaFormDefaults = Pick<NewMetricMeta, 'id'>;

type MetricMetaFormGroupContent = {
  id: FormControl<IMetricMeta['id'] | NewMetricMeta['id']>;
  label01: FormControl<IMetricMeta['label01']>;
  label256: FormControl<IMetricMeta['label256']>;
  tenant: FormControl<IMetricMeta['tenant']>;
};

export type MetricMetaFormGroup = FormGroup<MetricMetaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MetricMetaFormService {
  createMetricMetaFormGroup(metricMeta: MetricMetaFormGroupInput = { id: null }): MetricMetaFormGroup {
    const metricMetaRawValue = {
      ...this.getFormDefaults(),
      ...metricMeta,
    };
    return new FormGroup<MetricMetaFormGroupContent>({
      id: new FormControl(
        { value: metricMetaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      label01: new FormControl(metricMetaRawValue.label01),
      label256: new FormControl(metricMetaRawValue.label256),
      tenant: new FormControl(metricMetaRawValue.tenant, {
        validators: [Validators.required],
      }),
    });
  }

  getMetricMeta(form: MetricMetaFormGroup): IMetricMeta | NewMetricMeta {
    return form.getRawValue() as IMetricMeta | NewMetricMeta;
  }

  resetForm(form: MetricMetaFormGroup, metricMeta: MetricMetaFormGroupInput): void {
    const metricMetaRawValue = { ...this.getFormDefaults(), ...metricMeta };
    form.reset(
      {
        ...metricMetaRawValue,
        id: { value: metricMetaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MetricMetaFormDefaults {
    return {
      id: null,
    };
  }
}
