import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditCollaborateurComponent } from './add-edit-collaborateur.component';

describe('AddEditCollaborateurComponent', () => {
  let component: AddEditCollaborateurComponent;
  let fixture: ComponentFixture<AddEditCollaborateurComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddEditCollaborateurComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddEditCollaborateurComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
