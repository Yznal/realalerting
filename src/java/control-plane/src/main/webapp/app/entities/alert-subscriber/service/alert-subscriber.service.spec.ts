import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAlertSubscriber } from '../alert-subscriber.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../alert-subscriber.test-samples';

import { AlertSubscriberService } from './alert-subscriber.service';

const requireRestSample: IAlertSubscriber = {
  ...sampleWithRequiredData,
};

describe('AlertSubscriber Service', () => {
  let service: AlertSubscriberService;
  let httpMock: HttpTestingController;
  let expectedResult: IAlertSubscriber | IAlertSubscriber[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AlertSubscriberService);
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

    it('should create a AlertSubscriber', () => {
      const alertSubscriber = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(alertSubscriber).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AlertSubscriber', () => {
      const alertSubscriber = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(alertSubscriber).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AlertSubscriber', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AlertSubscriber', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a AlertSubscriber', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addAlertSubscriberToCollectionIfMissing', () => {
      it('should add a AlertSubscriber to an empty array', () => {
        const alertSubscriber: IAlertSubscriber = sampleWithRequiredData;
        expectedResult = service.addAlertSubscriberToCollectionIfMissing([], alertSubscriber);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(alertSubscriber);
      });

      it('should not add a AlertSubscriber to an array that contains it', () => {
        const alertSubscriber: IAlertSubscriber = sampleWithRequiredData;
        const alertSubscriberCollection: IAlertSubscriber[] = [
          {
            ...alertSubscriber,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAlertSubscriberToCollectionIfMissing(alertSubscriberCollection, alertSubscriber);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AlertSubscriber to an array that doesn't contain it", () => {
        const alertSubscriber: IAlertSubscriber = sampleWithRequiredData;
        const alertSubscriberCollection: IAlertSubscriber[] = [sampleWithPartialData];
        expectedResult = service.addAlertSubscriberToCollectionIfMissing(alertSubscriberCollection, alertSubscriber);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(alertSubscriber);
      });

      it('should add only unique AlertSubscriber to an array', () => {
        const alertSubscriberArray: IAlertSubscriber[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const alertSubscriberCollection: IAlertSubscriber[] = [sampleWithRequiredData];
        expectedResult = service.addAlertSubscriberToCollectionIfMissing(alertSubscriberCollection, ...alertSubscriberArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const alertSubscriber: IAlertSubscriber = sampleWithRequiredData;
        const alertSubscriber2: IAlertSubscriber = sampleWithPartialData;
        expectedResult = service.addAlertSubscriberToCollectionIfMissing([], alertSubscriber, alertSubscriber2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(alertSubscriber);
        expect(expectedResult).toContain(alertSubscriber2);
      });

      it('should accept null and undefined values', () => {
        const alertSubscriber: IAlertSubscriber = sampleWithRequiredData;
        expectedResult = service.addAlertSubscriberToCollectionIfMissing([], null, alertSubscriber, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(alertSubscriber);
      });

      it('should return initial array if no AlertSubscriber is added', () => {
        const alertSubscriberCollection: IAlertSubscriber[] = [sampleWithRequiredData];
        expectedResult = service.addAlertSubscriberToCollectionIfMissing(alertSubscriberCollection, undefined, null);
        expect(expectedResult).toEqual(alertSubscriberCollection);
      });
    });

    describe('compareAlertSubscriber', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAlertSubscriber(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareAlertSubscriber(entity1, entity2);
        const compareResult2 = service.compareAlertSubscriber(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareAlertSubscriber(entity1, entity2);
        const compareResult2 = service.compareAlertSubscriber(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareAlertSubscriber(entity1, entity2);
        const compareResult2 = service.compareAlertSubscriber(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
