import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MetricDetailComponent } from './metric-detail.component';

describe('Metric Management Detail Component', () => {
  let comp: MetricDetailComponent;
  let fixture: ComponentFixture<MetricDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MetricDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: MetricDetailComponent,
              resolve: { metric: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MetricDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MetricDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load metric on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MetricDetailComponent);

      // THEN
      expect(instance.metric()).toEqual(expect.objectContaining({ id: 123 }));
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
