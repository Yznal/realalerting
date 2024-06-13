import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '5bbb1075-4c51-4736-869c-7c4de7d8c326',
};

export const sampleWithPartialData: IAuthority = {
  name: '229d6b48-3649-47c9-9c96-a8ac054ae95d',
};

export const sampleWithFullData: IAuthority = {
  name: '4095c50a-2a9b-418e-913c-b8cf075acbe1',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
