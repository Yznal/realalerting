import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MetricTagsValueDetailComponent } from './metric-tags-value-detail.component';

describe('MetricTagsValue Management Detail Component', () => {
  let comp: MetricTagsValueDetailComponent;
  let fixture: ComponentFixture<MetricTagsValueDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MetricTagsValueDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: MetricTagsValueDetailComponent,
              resolve: { metricTagsValue: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MetricTagsValueDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MetricTagsValueDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load metricTagsValue on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MetricTagsValueDetailComponent);

      // THEN
      expect(instance.metricTagsValue()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
