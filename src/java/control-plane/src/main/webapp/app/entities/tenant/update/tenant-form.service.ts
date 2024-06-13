import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITenant, NewTenant } from '../tenant.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITenant for edit and NewTenantFormGroupInput for create.
 */
type TenantFormGroupInput = ITenant | PartialWithRequiredKeyOf<NewTenant>;

type TenantFormDefaults = Pick<NewTenant, 'id'>;

type TenantFormGroupContent = {
  id: FormControl<ITenant['id'] | NewTenant['id']>;
  name: FormControl<ITenant['name']>;
  description: FormControl<ITenant['description']>;
};

export type TenantFormGroup = FormGroup<TenantFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TenantFormService {
  createTenantFormGroup(tenant: TenantFormGroupInput = { id: null }): TenantFormGroup {
    const tenantRawValue = {
      ...this.getFormDefaults(),
      ...tenant,
    };
    return new FormGroup<TenantFormGroupContent>({
      id: new FormControl(
        { value: tenantRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(tenantRawValue.name),
      description: new FormControl(tenantRawValue.description),
    });
  }

  getTenant(form: TenantFormGroup): ITenant | NewTenant {
    return form.getRawValue() as ITenant | NewTenant;
  }

  resetForm(form: TenantFormGroup, tenant: TenantFormGroupInput): void {
    const tenantRawValue = { ...this.getFormDefaults(), ...tenant };
    form.reset(
      {
        ...tenantRawValue,
        id: { value: tenantRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TenantFormDefaults {
    return {
      id: null,
    };
  }
}
