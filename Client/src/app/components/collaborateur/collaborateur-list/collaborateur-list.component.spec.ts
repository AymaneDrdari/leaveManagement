import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollaborateurListComponent } from './collaborateur-list.component';

describe('CollaborateurListComponent', () => {
  let component: CollaborateurListComponent;
  let fixture: ComponentFixture<CollaborateurListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CollaborateurListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CollaborateurListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
