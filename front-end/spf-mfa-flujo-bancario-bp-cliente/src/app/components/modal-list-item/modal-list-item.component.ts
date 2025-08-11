import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
  signal,
} from '@angular/core';
import {
  PichinchaCtaSectionModule,
  PichinchaModalDialogModule,
  PichinchaSelectorListCardModule,
  PichinchaTypographyModule,
} from '@pichincha/ds-angular';

@Component({
  selector: 'app-modal-list-item',
  standalone: true,
  imports: [
    PichinchaModalDialogModule,
    PichinchaSelectorListCardModule,
    PichinchaCtaSectionModule,
    PichinchaTypographyModule,
  ],
  templateUrl: './modal-list-item.component.html',
  styleUrl: './modal-list-item.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ModalListItemComponent {
  openModal = input.required<boolean>();
  idItem = input<string>();

  isOptionList = signal<boolean>(true);

  oClickEdit = output();
  oClickDelete = output();
  oCloseModal = output();

  readonly modalOptions = [
    {
      id: '1',
      selectorInformation: {
        selectorName: 'Editar',
        selectorDescription: null,
      },
      selectorElement: { variant: 'icon', iconName: 'edit' },
    },
    {
      id: '2',
      selected: false,
      selectorInformation: {
        selectorName: 'Eliminar',
        selectorDescription: null,
      },
      selectorElement: { variant: 'icon', iconName: 'delete' },
    },
  ];

  handleClickOptionList(e: Event) {
    const { detail } = e as CustomEvent;

    if (detail === '1') {
      this.oClickEdit.emit();
      return;
    }

    this.isOptionList.set(false);
  }

  handleClickCTASection(isAccepted?: boolean) {
    if (isAccepted) {
      this.isOptionList.set(true);
      this.oClickDelete.emit();
      return;
    }

    this.isOptionList.set(true);
  }

  handleCloseModal() {
    this.isOptionList.set(true);
    this.oCloseModal.emit();
  }
}
