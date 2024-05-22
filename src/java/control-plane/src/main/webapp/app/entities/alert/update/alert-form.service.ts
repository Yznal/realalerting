import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IAlert, NewAlert } from '../alert.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAlert for edit and NewAlertFormGroupInput for create.
 */
type AlertFormGroupInput = IAlert | PartialWithRequiredKeyOf<NewAlert>;

type AlertFormDefaults = Pick<NewAlert, 'id'>;

type AlertFormGroupContent = {
  id: FormControl<IAlert['id'] | NewAlert['id']>;
  type: FormControl<IAlert['type']>;
  name: FormControl<IAlert['name']>;
  description: FormControl<IAlert['description']>;
  metric: FormControl<IAlert['metric']>;
};

export type AlertFormGroup = FormGroup<AlertFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AlertFormService {
  createAlertFormGroup(alert: AlertFormGroupInput = { id: null }): AlertFormGroup {
    const alertRawValue = {
      ...this.getFormDefaults(),
      ...alert,
    };
    return new FormGroup<AlertFormGroupContent>({
      id: new FormControl(
        { value: alertRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      type: new FormControl(alertRawValue.type, {
        validators: [Validators.required],
      }),
      name: new FormControl(alertRawValue.name),
      description: new FormControl(alertRawValue.description),
      metric: new FormControl(alertRawValue.metric, {
        validators: [Validators.required],
      }),
    });
  }

  getAlert(form: AlertFormGroup): IAlert | NewAlert {
    return form.getRawValue() as IAlert | NewAlert;
  }

  resetForm(form: AlertFormGroup, alert: AlertFormGroupInput): void {
    const alertRawValue = { ...this.getFormDefaults(), ...alert };
    form.reset(
      {
        ...alertRawValue,
        id: { value: alertRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AlertFormDefaults {
    return {
      id: null,
    };
  }
}
