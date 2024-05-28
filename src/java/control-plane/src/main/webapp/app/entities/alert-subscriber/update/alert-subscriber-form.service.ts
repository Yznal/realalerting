import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IAlertSubscriber, NewAlertSubscriber } from '../alert-subscriber.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAlertSubscriber for edit and NewAlertSubscriberFormGroupInput for create.
 */
type AlertSubscriberFormGroupInput = IAlertSubscriber | PartialWithRequiredKeyOf<NewAlertSubscriber>;

type AlertSubscriberFormDefaults = Pick<NewAlertSubscriber, 'id'>;

type AlertSubscriberFormGroupContent = {
  id: FormControl<IAlertSubscriber['id'] | NewAlertSubscriber['id']>;
  subscriberAddress: FormControl<IAlertSubscriber['subscriberAddress']>;
  subscriberPort: FormControl<IAlertSubscriber['subscriberPort']>;
  subscriberUri: FormControl<IAlertSubscriber['subscriberUri']>;
  subscriberStreamId: FormControl<IAlertSubscriber['subscriberStreamId']>;
  client: FormControl<IAlertSubscriber['client']>;
  realAlert: FormControl<IAlertSubscriber['realAlert']>;
};

export type AlertSubscriberFormGroup = FormGroup<AlertSubscriberFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AlertSubscriberFormService {
  createAlertSubscriberFormGroup(alertSubscriber: AlertSubscriberFormGroupInput = { id: null }): AlertSubscriberFormGroup {
    const alertSubscriberRawValue = {
      ...this.getFormDefaults(),
      ...alertSubscriber,
    };
    return new FormGroup<AlertSubscriberFormGroupContent>({
      id: new FormControl(
        { value: alertSubscriberRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      subscriberAddress: new FormControl(alertSubscriberRawValue.subscriberAddress),
      subscriberPort: new FormControl(alertSubscriberRawValue.subscriberPort),
      subscriberUri: new FormControl(alertSubscriberRawValue.subscriberUri),
      subscriberStreamId: new FormControl(alertSubscriberRawValue.subscriberStreamId),
      client: new FormControl(alertSubscriberRawValue.client, {
        validators: [Validators.required],
      }),
      realAlert: new FormControl(alertSubscriberRawValue.realAlert, {
        validators: [Validators.required],
      }),
    });
  }

  getAlertSubscriber(form: AlertSubscriberFormGroup): IAlertSubscriber | NewAlertSubscriber {
    return form.getRawValue() as IAlertSubscriber | NewAlertSubscriber;
  }

  resetForm(form: AlertSubscriberFormGroup, alertSubscriber: AlertSubscriberFormGroupInput): void {
    const alertSubscriberRawValue = { ...this.getFormDefaults(), ...alertSubscriber };
    form.reset(
      {
        ...alertSubscriberRawValue,
        id: { value: alertSubscriberRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AlertSubscriberFormDefaults {
    return {
      id: null,
    };
  }
}
