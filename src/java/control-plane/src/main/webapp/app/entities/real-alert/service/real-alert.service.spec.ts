import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRealAlert } from '../real-alert.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../real-alert.test-samples';

import { RealAlertService } from './real-alert.service';

const requireRestSample: IRealAlert = {
  ...sampleWithRequiredData,
};

describe('RealAlert Service', () => {
  let service: RealAlertService;
  let httpMock: HttpTestingController;
  let expectedResult: IRealAlert | IRealAlert[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RealAlertService);
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

    it('should create a RealAlert', () => {
      const realAlert = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(realAlert).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RealAlert', () => {
      const realAlert = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(realAlert).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RealAlert', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RealAlert', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RealAlert', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRealAlertToCollectionIfMissing', () => {
      it('should add a RealAlert to an empty array', () => {
        const realAlert: IRealAlert = sampleWithRequiredData;
        expectedResult = service.addRealAlertToCollectionIfMissing([], realAlert);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(realAlert);
      });

      it('should not add a RealAlert to an array that contains it', () => {
        const realAlert: IRealAlert = sampleWithRequiredData;
        const realAlertCollection: IRealAlert[] = [
          {
            ...realAlert,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRealAlertToCollectionIfMissing(realAlertCollection, realAlert);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RealAlert to an array that doesn't contain it", () => {
        const realAlert: IRealAlert = sampleWithRequiredData;
        const realAlertCollection: IRealAlert[] = [sampleWithPartialData];
        expectedResult = service.addRealAlertToCollectionIfMissing(realAlertCollection, realAlert);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(realAlert);
      });

      it('should add only unique RealAlert to an array', () => {
        const realAlertArray: IRealAlert[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const realAlertCollection: IRealAlert[] = [sampleWithRequiredData];
        expectedResult = service.addRealAlertToCollectionIfMissing(realAlertCollection, ...realAlertArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const realAlert: IRealAlert = sampleWithRequiredData;
        const realAlert2: IRealAlert = sampleWithPartialData;
        expectedResult = service.addRealAlertToCollectionIfMissing([], realAlert, realAlert2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(realAlert);
        expect(expectedResult).toContain(realAlert2);
      });

      it('should accept null and undefined values', () => {
        const realAlert: IRealAlert = sampleWithRequiredData;
        expectedResult = service.addRealAlertToCollectionIfMissing([], null, realAlert, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(realAlert);
      });

      it('should return initial array if no RealAlert is added', () => {
        const realAlertCollection: IRealAlert[] = [sampleWithRequiredData];
        expectedResult = service.addRealAlertToCollectionIfMissing(realAlertCollection, undefined, null);
        expect(expectedResult).toEqual(realAlertCollection);
      });
    });

    describe('compareRealAlert', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRealAlert(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareRealAlert(entity1, entity2);
        const compareResult2 = service.compareRealAlert(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareRealAlert(entity1, entity2);
        const compareResult2 = service.compareRealAlert(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareRealAlert(entity1, entity2);
        const compareResult2 = service.compareRealAlert(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
