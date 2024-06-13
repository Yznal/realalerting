export interface ITenant {
  id: number;
  name?: string | null;
  description?: string | null;
}

export type NewTenant = Omit<ITenant, 'id'> & { id: null };
