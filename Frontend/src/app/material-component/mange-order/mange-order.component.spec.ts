import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MangeOrderComponent } from './mange-order.component';

describe('MangeOrderComponent', () => {
  let component: MangeOrderComponent;
  let fixture: ComponentFixture<MangeOrderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MangeOrderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MangeOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
