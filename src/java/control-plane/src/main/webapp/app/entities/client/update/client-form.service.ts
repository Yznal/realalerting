import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IClient, NewClient } from '../client.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClient for edit and NewClientFormGroupInput for create.
 */
type ClientFormGroupInput = IClient | PartialWithRequiredKeyOf<NewClient>;

type ClientFormDefaults = Pick<NewClient, 'id'>;

type ClientFormGroupContent = {
  id: FormControl<IClient['id'] | NewClient['id']>;
  protocolProducerAddress: FormControl<IClient['protocolProducerAddress']>;
  protocolProducerPort: FormControl<IClient['protocolProducerPort']>;
  protocolProducerUri: FormControl<IClient['protocolProducerUri']>;
  protocolProducerStreamId: FormControl<IClient['protocolProducerStreamId']>;
  protocolSubscriberAddress: FormControl<IClient['protocolSubscriberAddress']>;
  protocolSubscriberPort: FormControl<IClient['protocolSubscriberPort']>;
  protocolSubscriberUri: FormControl<IClient['protocolSubscriberUri']>;
  protocolSubscriberStreamId: FormControl<IClient['protocolSubscriberStreamId']>;
  metricProducerAddress: FormControl<IClient['metricProducerAddress']>;
  metricProducerPort: FormControl<IClient['metricProducerPort']>;
  metricProducerUri: FormControl<IClient['metricProducerUri']>;
  metricProducerStreamId: FormControl<IClient['metricProducerStreamId']>;
  tenant: FormControl<IClient['tenant']>;
};

export type ClientFormGroup = FormGroup<ClientFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClientFormService {
  createClientFormGroup(client: ClientFormGroupInput = { id: null }): ClientFormGroup {
    const clientRawValue = {
      ...this.getFormDefaults(),
      ...client,
    };
    return new FormGroup<ClientFormGroupContent>({
      id: new FormControl(
        { value: clientRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      protocolProducerAddress: new FormControl(clientRawValue.protocolProducerAddress),
      protocolProducerPort: new FormControl(clientRawValue.protocolProducerPort),
      protocolProducerUri: new FormControl(clientRawValue.protocolProducerUri),
      protocolProducerStreamId: new FormControl(clientRawValue.protocolProducerStreamId),
      protocolSubscriberAddress: new FormControl(clientRawValue.protocolSubscriberAddress),
      protocolSubscriberPort: new FormControl(clientRawValue.protocolSubscriberPort),
      protocolSubscriberUri: new FormControl(clientRawValue.protocolSubscriberUri),
      protocolSubscriberStreamId: new FormControl(clientRawValue.protocolSubscriberStreamId),
      metricProducerAddress: new FormControl(clientRawValue.metricProducerAddress),
      metricProducerPort: new FormControl(clientRawValue.metricProducerPort),
      metricProducerUri: new FormControl(clientRawValue.metricProducerUri),
      metricProducerStreamId: new FormControl(clientRawValue.metricProducerStreamId),
      tenant: new FormControl(clientRawValue.tenant, {
        validators: [Validators.required],
      }),
    });
  }

  getClient(form: ClientFormGroup): IClient | NewClient {
    return form.getRawValue() as IClient | NewClient;
  }

  resetForm(form: ClientFormGroup, client: ClientFormGroupInput): void {
    const clientRawValue = { ...this.getFormDefaults(), ...client };
    form.reset(
      {
        ...clientRawValue,
        id: { value: clientRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ClientFormDefaults {
    return {
      id: null,
    };
  }
}
