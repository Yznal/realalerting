import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MetricSubscriberDetailComponent } from './metric-subscriber-detail.component';

describe('MetricSubscriber Management Detail Component', () => {
  let comp: MetricSubscriberDetailComponent;
  let fixture: ComponentFixture<MetricSubscriberDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MetricSubscriberDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: MetricSubscriberDetailComponent,
              resolve: { metricSubscriber: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MetricSubscriberDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MetricSubscriberDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load metricSubscriber on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MetricSubscriberDetailComponent);

      // THEN
      expect(instance.metricSubscriber()).toEqual(expect.objectContaining({ id: 123 }));
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
