import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { RealAlertDetailComponent } from './real-alert-detail.component';

describe('RealAlert Management Detail Component', () => {
  let comp: RealAlertDetailComponent;
  let fixture: ComponentFixture<RealAlertDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RealAlertDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: RealAlertDetailComponent,
              resolve: { realAlert: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(RealAlertDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RealAlertDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load realAlert on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', RealAlertDetailComponent);

      // THEN
      expect(instance.realAlert()).toEqual(expect.objectContaining({ id: 123 }));
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
