import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITenant } from '../tenant.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../tenant.test-samples';

import { TenantService } from './tenant.service';

const requireRestSample: ITenant = {
  ...sampleWithRequiredData,
};

describe('Tenant Service', () => {
  let service: TenantService;
  let httpMock: HttpTestingController;
  let expectedResult: ITenant | ITenant[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TenantService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Tenant', () => {
      const tenant = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tenant).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Tenant', () => {
      const tenant = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tenant).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Tenant', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Tenant', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Tenant', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTenantToCollectionIfMissing', () => {
      it('should add a Tenant to an empty array', () => {
        const tenant: ITenant = sampleWithRequiredData;
        expectedResult = service.addTenantToCollectionIfMissing([], tenant);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tenant);
      });

      it('should not add a Tenant to an array that contains it', () => {
        const tenant: ITenant = sampleWithRequiredData;
        const tenantCollection: ITenant[] = [
          {
            ...tenant,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTenantToCollectionIfMissing(tenantCollection, tenant);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Tenant to an array that doesn't contain it", () => {
        const tenant: ITenant = sampleWithRequiredData;
        const tenantCollection: ITenant[] = [sampleWithPartialData];
        expectedResult = service.addTenantToCollectionIfMissing(tenantCollection, tenant);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tenant);
      });

      it('should add only unique Tenant to an array', () => {
        const tenantArray: ITenant[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tenantCollection: ITenant[] = [sampleWithRequiredData];
        expectedResult = service.addTenantToCollectionIfMissing(tenantCollection, ...tenantArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tenant: ITenant = sampleWithRequiredData;
        const tenant2: ITenant = sampleWithPartialData;
        expectedResult = service.addTenantToCollectionIfMissing([], tenant, tenant2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tenant);
        expect(expectedResult).toContain(tenant2);
      });

      it('should accept null and undefined values', () => {
        const tenant: ITenant = sampleWithRequiredData;
        expectedResult = service.addTenantToCollectionIfMissing([], null, tenant, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tenant);
      });

      it('should return initial array if no Tenant is added', () => {
        const tenantCollection: ITenant[] = [sampleWithRequiredData];
        expectedResult = service.addTenantToCollectionIfMissing(tenantCollection, undefined, null);
        expect(expectedResult).toEqual(tenantCollection);
      });
    });

    describe('compareTenant', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTenant(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTenant(entity1, entity2);
        const compareResult2 = service.compareTenant(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTenant(entity1, entity2);
        const compareResult2 = service.compareTenant(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTenant(entity1, entity2);
        const compareResult2 = service.compareTenant(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
