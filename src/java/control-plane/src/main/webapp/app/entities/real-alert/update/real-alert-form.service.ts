import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IRealAlert, NewRealAlert } from '../real-alert.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRealAlert for edit and NewRealAlertFormGroupInput for create.
 */
type RealAlertFormGroupInput = IRealAlert | PartialWithRequiredKeyOf<NewRealAlert>;

type RealAlertFormDefaults = Pick<NewRealAlert, 'id'>;

type RealAlertFormGroupContent = {
  id: FormControl<IRealAlert['id'] | NewRealAlert['id']>;
  type: FormControl<IRealAlert['type']>;
  name: FormControl<IRealAlert['name']>;
  description: FormControl<IRealAlert['description']>;
  conf: FormControl<IRealAlert['conf']>;
  client: FormControl<IRealAlert['client']>;
  metric: FormControl<IRealAlert['metric']>;
};

export type RealAlertFormGroup = FormGroup<RealAlertFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RealAlertFormService {
  createRealAlertFormGroup(realAlert: RealAlertFormGroupInput = { id: null }): RealAlertFormGroup {
    const realAlertRawValue = {
      ...this.getFormDefaults(),
      ...realAlert,
    };
    return new FormGroup<RealAlertFormGroupContent>({
      id: new FormControl(
        { value: realAlertRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      type: new FormControl(realAlertRawValue.type, {
        validators: [Validators.required],
      }),
      name: new FormControl(realAlertRawValue.name),
      description: new FormControl(realAlertRawValue.description),
      conf: new FormControl(realAlertRawValue.conf),
      client: new FormControl(realAlertRawValue.client, {
        validators: [Validators.required],
      }),
      metric: new FormControl(realAlertRawValue.metric, {
        validators: [Validators.required],
      }),
    });
  }

  getRealAlert(form: RealAlertFormGroup): IRealAlert | NewRealAlert {
    return form.getRawValue() as IRealAlert | NewRealAlert;
  }

  resetForm(form: RealAlertFormGroup, realAlert: RealAlertFormGroupInput): void {
    const realAlertRawValue = { ...this.getFormDefaults(), ...realAlert };
    form.reset(
      {
        ...realAlertRawValue,
        id: { value: realAlertRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): RealAlertFormDefaults {
    return {
      id: null,
    };
  }
}
