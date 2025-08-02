import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ModalListItemComponent } from './modal-list-item.component';

describe('ModalListItemComponent', () => {
  let fixture: ComponentFixture<ModalListItemComponent>;
  let compiled: HTMLElement;
  let component: ModalListItemComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalListItemComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalListItemComponent);

    
    compiled = fixture.nativeElement as HTMLElement;
    component = fixture.componentInstance;
    
    fixture.componentRef.setInput('openModal', false);
    fixture.componentRef.setInput('idItem', "1");

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should execute onClickEdit when the option edit is clicked', () => {
    
    const spyOnClickEdit = jest.spyOn(component.onClickEdit, 'emit');

    component.handleClickOptionList(new CustomEvent('click', { detail: "1" }));

    expect(spyOnClickEdit).toBeCalled();
  });

  it('should change isOptionList to false when the option delete is clicked', () => {
    component.isOptionList.set(true);
    component.handleClickOptionList(new CustomEvent('click', { detail: "2" }));

    expect(component.isOptionList()).toBeFalsy()
  });

  it('should execute onClickDelete when the option "accept" in delete item is clicked', () => {
    
    const spyOnClickDelete = jest.spyOn(component.onClickDelete, 'emit');

    component.handleClickCTASection(true);

    expect(spyOnClickDelete).toBeCalled();
  });

  it('should change isOptionList to true when the option "cancel" in delete item is clicked', () => {
    component.isOptionList.set(false);
    component.handleClickCTASection(false);

    expect(component.isOptionList()).toBeTruthy()
  });

  it('should execute onCloseModal and isOptionList is true  when the the modal is closed', () => {
    component.isOptionList.set(false);
    
    const spyOnClickDelete = jest.spyOn(component.onCloseModal, 'emit');

    component.handleCloseModal();

    expect(spyOnClickDelete).toBeCalled();
    expect(component.isOptionList()).toBeTruthy()
  });
});
