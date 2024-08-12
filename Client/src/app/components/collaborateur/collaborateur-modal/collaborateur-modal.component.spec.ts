import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollaborateurModalComponent } from './collaborateur-modal.component';

describe('CollaborateurModalComponent', () => {
  let component: CollaborateurModalComponent;
  let fixture: ComponentFixture<CollaborateurModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CollaborateurModalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CollaborateurModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
