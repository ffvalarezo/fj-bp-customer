import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BaseComponent } from './base.component';

describe('BaseComponent', () => {
  let fixture: ComponentFixture<BaseComponent>;
  let compiled: HTMLElement;
  let component: BaseComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BaseComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BaseComponent);
    compiled = fixture.nativeElement as HTMLElement;
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
