import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TenantDetailComponent } from './tenant-detail.component';

describe('Tenant Management Detail Component', () => {
  let comp: TenantDetailComponent;
  let fixture: ComponentFixture<TenantDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TenantDetailComponent,
              resolve: { tenant: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TenantDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load tenant on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TenantDetailComponent);

      // THEN
      expect(instance.tenant()).toEqual(expect.objectContaining({ id: 123 }));
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
