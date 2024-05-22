import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { AlertSubscriberDetailComponent } from './alert-subscriber-detail.component';

describe('AlertSubscriber Management Detail Component', () => {
  let comp: AlertSubscriberDetailComponent;
  let fixture: ComponentFixture<AlertSubscriberDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlertSubscriberDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: AlertSubscriberDetailComponent,
              resolve: { alertSubscriber: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(AlertSubscriberDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertSubscriberDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load alertSubscriber on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AlertSubscriberDetailComponent);

      // THEN
      expect(instance.alertSubscriber()).toEqual(expect.objectContaining({ id: 123 }));
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
