import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMetric } from '../metric.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../metric.test-samples';

import { MetricService } from './metric.service';

const requireRestSample: IMetric = {
  ...sampleWithRequiredData,
};

describe('Metric Service', () => {
  let service: MetricService;
  let httpMock: HttpTestingController;
  let expectedResult: IMetric | IMetric[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MetricService);
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

    it('should create a Metric', () => {
      const metric = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(metric).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Metric', () => {
      const metric = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(metric).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Metric', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Metric', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Metric', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMetricToCollectionIfMissing', () => {
      it('should add a Metric to an empty array', () => {
        const metric: IMetric = sampleWithRequiredData;
        expectedResult = service.addMetricToCollectionIfMissing([], metric);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(metric);
      });

      it('should not add a Metric to an array that contains it', () => {
        const metric: IMetric = sampleWithRequiredData;
        const metricCollection: IMetric[] = [
          {
            ...metric,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMetricToCollectionIfMissing(metricCollection, metric);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Metric to an array that doesn't contain it", () => {
        const metric: IMetric = sampleWithRequiredData;
        const metricCollection: IMetric[] = [sampleWithPartialData];
        expectedResult = service.addMetricToCollectionIfMissing(metricCollection, metric);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(metric);
      });

      it('should add only unique Metric to an array', () => {
        const metricArray: IMetric[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const metricCollection: IMetric[] = [sampleWithRequiredData];
        expectedResult = service.addMetricToCollectionIfMissing(metricCollection, ...metricArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const metric: IMetric = sampleWithRequiredData;
        const metric2: IMetric = sampleWithPartialData;
        expectedResult = service.addMetricToCollectionIfMissing([], metric, metric2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(metric);
        expect(expectedResult).toContain(metric2);
      });

      it('should accept null and undefined values', () => {
        const metric: IMetric = sampleWithRequiredData;
        expectedResult = service.addMetricToCollectionIfMissing([], null, metric, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(metric);
      });

      it('should return initial array if no Metric is added', () => {
        const metricCollection: IMetric[] = [sampleWithRequiredData];
        expectedResult = service.addMetricToCollectionIfMissing(metricCollection, undefined, null);
        expect(expectedResult).toEqual(metricCollection);
      });
    });

    describe('compareMetric', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMetric(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMetric(entity1, entity2);
        const compareResult2 = service.compareMetric(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMetric(entity1, entity2);
        const compareResult2 = service.compareMetric(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMetric(entity1, entity2);
        const compareResult2 = service.compareMetric(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
