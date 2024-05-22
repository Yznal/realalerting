import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAlert } from '../alert.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../alert.test-samples';

import { AlertService } from './alert.service';

const requireRestSample: IAlert = {
  ...sampleWithRequiredData,
};

describe('Alert Service', () => {
  let service: AlertService;
  let httpMock: HttpTestingController;
  let expectedResult: IAlert | IAlert[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AlertService);
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

    it('should create a Alert', () => {
      const alert = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(alert).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Alert', () => {
      const alert = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(alert).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Alert', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Alert', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Alert', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addAlertToCollectionIfMissing', () => {
      it('should add a Alert to an empty array', () => {
        const alert: IAlert = sampleWithRequiredData;
        expectedResult = service.addAlertToCollectionIfMissing([], alert);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(alert);
      });

      it('should not add a Alert to an array that contains it', () => {
        const alert: IAlert = sampleWithRequiredData;
        const alertCollection: IAlert[] = [
          {
            ...alert,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAlertToCollectionIfMissing(alertCollection, alert);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Alert to an array that doesn't contain it", () => {
        const alert: IAlert = sampleWithRequiredData;
        const alertCollection: IAlert[] = [sampleWithPartialData];
        expectedResult = service.addAlertToCollectionIfMissing(alertCollection, alert);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(alert);
      });

      it('should add only unique Alert to an array', () => {
        const alertArray: IAlert[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const alertCollection: IAlert[] = [sampleWithRequiredData];
        expectedResult = service.addAlertToCollectionIfMissing(alertCollection, ...alertArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const alert: IAlert = sampleWithRequiredData;
        const alert2: IAlert = sampleWithPartialData;
        expectedResult = service.addAlertToCollectionIfMissing([], alert, alert2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(alert);
        expect(expectedResult).toContain(alert2);
      });

      it('should accept null and undefined values', () => {
        const alert: IAlert = sampleWithRequiredData;
        expectedResult = service.addAlertToCollectionIfMissing([], null, alert, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(alert);
      });

      it('should return initial array if no Alert is added', () => {
        const alertCollection: IAlert[] = [sampleWithRequiredData];
        expectedResult = service.addAlertToCollectionIfMissing(alertCollection, undefined, null);
        expect(expectedResult).toEqual(alertCollection);
      });
    });

    describe('compareAlert', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAlert(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareAlert(entity1, entity2);
        const compareResult2 = service.compareAlert(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareAlert(entity1, entity2);
        const compareResult2 = service.compareAlert(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareAlert(entity1, entity2);
        const compareResult2 = service.compareAlert(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
