import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MetricMetaDetailComponent } from './metric-meta-detail.component';

describe('MetricMeta Management Detail Component', () => {
  let comp: MetricMetaDetailComponent;
  let fixture: ComponentFixture<MetricMetaDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MetricMetaDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: MetricMetaDetailComponent,
              resolve: { metricMeta: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MetricMetaDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MetricMetaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load metricMeta on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MetricMetaDetailComponent);

      // THEN
      expect(instance.metricMeta()).toEqual(expect.objectContaining({ id: 123 }));
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
