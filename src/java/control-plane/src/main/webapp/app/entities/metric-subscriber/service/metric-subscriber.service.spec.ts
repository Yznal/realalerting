import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMetricSubscriber } from '../metric-subscriber.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../metric-subscriber.test-samples';

import { MetricSubscriberService } from './metric-subscriber.service';

const requireRestSample: IMetricSubscriber = {
  ...sampleWithRequiredData,
};

describe('MetricSubscriber Service', () => {
  let service: MetricSubscriberService;
  let httpMock: HttpTestingController;
  let expectedResult: IMetricSubscriber | IMetricSubscriber[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MetricSubscriberService);
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

    it('should create a MetricSubscriber', () => {
      const metricSubscriber = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(metricSubscriber).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MetricSubscriber', () => {
      const metricSubscriber = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(metricSubscriber).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MetricSubscriber', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MetricSubscriber', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MetricSubscriber', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMetricSubscriberToCollectionIfMissing', () => {
      it('should add a MetricSubscriber to an empty array', () => {
        const metricSubscriber: IMetricSubscriber = sampleWithRequiredData;
        expectedResult = service.addMetricSubscriberToCollectionIfMissing([], metricSubscriber);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(metricSubscriber);
      });

      it('should not add a MetricSubscriber to an array that contains it', () => {
        const metricSubscriber: IMetricSubscriber = sampleWithRequiredData;
        const metricSubscriberCollection: IMetricSubscriber[] = [
          {
            ...metricSubscriber,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMetricSubscriberToCollectionIfMissing(metricSubscriberCollection, metricSubscriber);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MetricSubscriber to an array that doesn't contain it", () => {
        const metricSubscriber: IMetricSubscriber = sampleWithRequiredData;
        const metricSubscriberCollection: IMetricSubscriber[] = [sampleWithPartialData];
        expectedResult = service.addMetricSubscriberToCollectionIfMissing(metricSubscriberCollection, metricSubscriber);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(metricSubscriber);
      });

      it('should add only unique MetricSubscriber to an array', () => {
        const metricSubscriberArray: IMetricSubscriber[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const metricSubscriberCollection: IMetricSubscriber[] = [sampleWithRequiredData];
        expectedResult = service.addMetricSubscriberToCollectionIfMissing(metricSubscriberCollection, ...metricSubscriberArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const metricSubscriber: IMetricSubscriber = sampleWithRequiredData;
        const metricSubscriber2: IMetricSubscriber = sampleWithPartialData;
        expectedResult = service.addMetricSubscriberToCollectionIfMissing([], metricSubscriber, metricSubscriber2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(metricSubscriber);
        expect(expectedResult).toContain(metricSubscriber2);
      });

      it('should accept null and undefined values', () => {
        const metricSubscriber: IMetricSubscriber = sampleWithRequiredData;
        expectedResult = service.addMetricSubscriberToCollectionIfMissing([], null, metricSubscriber, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(metricSubscriber);
      });

      it('should return initial array if no MetricSubscriber is added', () => {
        const metricSubscriberCollection: IMetricSubscriber[] = [sampleWithRequiredData];
        expectedResult = service.addMetricSubscriberToCollectionIfMissing(metricSubscriberCollection, undefined, null);
        expect(expectedResult).toEqual(metricSubscriberCollection);
      });
    });

    describe('compareMetricSubscriber', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMetricSubscriber(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMetricSubscriber(entity1, entity2);
        const compareResult2 = service.compareMetricSubscriber(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMetricSubscriber(entity1, entity2);
        const compareResult2 = service.compareMetricSubscriber(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMetricSubscriber(entity1, entity2);
        const compareResult2 = service.compareMetricSubscriber(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
