import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditEquipeComponent } from './add-edit-equipe.component';

describe('AddEditEquipeComponent', () => {
  let component: AddEditEquipeComponent;
  let fixture: ComponentFixture<AddEditEquipeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddEditEquipeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddEditEquipeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
