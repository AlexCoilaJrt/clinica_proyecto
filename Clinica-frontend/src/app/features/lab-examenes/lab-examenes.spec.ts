import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LabExamenes } from './lab-examenes';

describe('LabExamenes', () => {
  let component: LabExamenes;
  let fixture: ComponentFixture<LabExamenes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LabExamenes]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LabExamenes);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
