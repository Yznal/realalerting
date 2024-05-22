import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMetricMeta } from '../metric-meta.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../metric-meta.test-samples';

import { MetricMetaService } from './metric-meta.service';

const requireRestSample: IMetricMeta = {
  ...sampleWithRequiredData,
};

describe('MetricMeta Service', () => {
  let service: MetricMetaService;
  let httpMock: HttpTestingController;
  let expectedResult: IMetricMeta | IMetricMeta[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MetricMetaService);
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

    it('should create a MetricMeta', () => {
      const metricMeta = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(metricMeta).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MetricMeta', () => {
      const metricMeta = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(metricMeta).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MetricMeta', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MetricMeta', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MetricMeta', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMetricMetaToCollectionIfMissing', () => {
      it('should add a MetricMeta to an empty array', () => {
        const metricMeta: IMetricMeta = sampleWithRequiredData;
        expectedResult = service.addMetricMetaToCollectionIfMissing([], metricMeta);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(metricMeta);
      });

      it('should not add a MetricMeta to an array that contains it', () => {
        const metricMeta: IMetricMeta = sampleWithRequiredData;
        const metricMetaCollection: IMetricMeta[] = [
          {
            ...metricMeta,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMetricMetaToCollectionIfMissing(metricMetaCollection, metricMeta);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MetricMeta to an array that doesn't contain it", () => {
        const metricMeta: IMetricMeta = sampleWithRequiredData;
        const metricMetaCollection: IMetricMeta[] = [sampleWithPartialData];
        expectedResult = service.addMetricMetaToCollectionIfMissing(metricMetaCollection, metricMeta);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(metricMeta);
      });

      it('should add only unique MetricMeta to an array', () => {
        const metricMetaArray: IMetricMeta[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const metricMetaCollection: IMetricMeta[] = [sampleWithRequiredData];
        expectedResult = service.addMetricMetaToCollectionIfMissing(metricMetaCollection, ...metricMetaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const metricMeta: IMetricMeta = sampleWithRequiredData;
        const metricMeta2: IMetricMeta = sampleWithPartialData;
        expectedResult = service.addMetricMetaToCollectionIfMissing([], metricMeta, metricMeta2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(metricMeta);
        expect(expectedResult).toContain(metricMeta2);
      });

      it('should accept null and undefined values', () => {
        const metricMeta: IMetricMeta = sampleWithRequiredData;
        expectedResult = service.addMetricMetaToCollectionIfMissing([], null, metricMeta, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(metricMeta);
      });

      it('should return initial array if no MetricMeta is added', () => {
        const metricMetaCollection: IMetricMeta[] = [sampleWithRequiredData];
        expectedResult = service.addMetricMetaToCollectionIfMissing(metricMetaCollection, undefined, null);
        expect(expectedResult).toEqual(metricMetaCollection);
      });
    });

    describe('compareMetricMeta', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMetricMeta(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMetricMeta(entity1, entity2);
        const compareResult2 = service.compareMetricMeta(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMetricMeta(entity1, entity2);
        const compareResult2 = service.compareMetricMeta(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMetricMeta(entity1, entity2);
        const compareResult2 = service.compareMetricMeta(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
