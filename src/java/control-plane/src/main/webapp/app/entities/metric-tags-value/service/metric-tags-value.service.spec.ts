import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMetricTagsValue } from '../metric-tags-value.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../metric-tags-value.test-samples';

import { MetricTagsValueService } from './metric-tags-value.service';

const requireRestSample: IMetricTagsValue = {
  ...sampleWithRequiredData,
};

describe('MetricTagsValue Service', () => {
  let service: MetricTagsValueService;
  let httpMock: HttpTestingController;
  let expectedResult: IMetricTagsValue | IMetricTagsValue[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MetricTagsValueService);
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

    it('should create a MetricTagsValue', () => {
      const metricTagsValue = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(metricTagsValue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MetricTagsValue', () => {
      const metricTagsValue = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(metricTagsValue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MetricTagsValue', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MetricTagsValue', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MetricTagsValue', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMetricTagsValueToCollectionIfMissing', () => {
      it('should add a MetricTagsValue to an empty array', () => {
        const metricTagsValue: IMetricTagsValue = sampleWithRequiredData;
        expectedResult = service.addMetricTagsValueToCollectionIfMissing([], metricTagsValue);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(metricTagsValue);
      });

      it('should not add a MetricTagsValue to an array that contains it', () => {
        const metricTagsValue: IMetricTagsValue = sampleWithRequiredData;
        const metricTagsValueCollection: IMetricTagsValue[] = [
          {
            ...metricTagsValue,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMetricTagsValueToCollectionIfMissing(metricTagsValueCollection, metricTagsValue);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MetricTagsValue to an array that doesn't contain it", () => {
        const metricTagsValue: IMetricTagsValue = sampleWithRequiredData;
        const metricTagsValueCollection: IMetricTagsValue[] = [sampleWithPartialData];
        expectedResult = service.addMetricTagsValueToCollectionIfMissing(metricTagsValueCollection, metricTagsValue);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(metricTagsValue);
      });

      it('should add only unique MetricTagsValue to an array', () => {
        const metricTagsValueArray: IMetricTagsValue[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const metricTagsValueCollection: IMetricTagsValue[] = [sampleWithRequiredData];
        expectedResult = service.addMetricTagsValueToCollectionIfMissing(metricTagsValueCollection, ...metricTagsValueArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const metricTagsValue: IMetricTagsValue = sampleWithRequiredData;
        const metricTagsValue2: IMetricTagsValue = sampleWithPartialData;
        expectedResult = service.addMetricTagsValueToCollectionIfMissing([], metricTagsValue, metricTagsValue2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(metricTagsValue);
        expect(expectedResult).toContain(metricTagsValue2);
      });

      it('should accept null and undefined values', () => {
        const metricTagsValue: IMetricTagsValue = sampleWithRequiredData;
        expectedResult = service.addMetricTagsValueToCollectionIfMissing([], null, metricTagsValue, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(metricTagsValue);
      });

      it('should return initial array if no MetricTagsValue is added', () => {
        const metricTagsValueCollection: IMetricTagsValue[] = [sampleWithRequiredData];
        expectedResult = service.addMetricTagsValueToCollectionIfMissing(metricTagsValueCollection, undefined, null);
        expect(expectedResult).toEqual(metricTagsValueCollection);
      });
    });

    describe('compareMetricTagsValue', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMetricTagsValue(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMetricTagsValue(entity1, entity2);
        const compareResult2 = service.compareMetricTagsValue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMetricTagsValue(entity1, entity2);
        const compareResult2 = service.compareMetricTagsValue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMetricTagsValue(entity1, entity2);
        const compareResult2 = service.compareMetricTagsValue(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
