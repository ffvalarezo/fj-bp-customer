import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import {
  PichinchaButtonModule,
  PichinchaLoadingScreenModule,
  PichinchaSelectorFavoriteListModule,
  PichinchaTypographyModule,
} from '@pichincha/ds-angular';
import { SelectorFavoriteListItemProps } from '@pichincha/ds-core/dist/types/global/interfaces/selector-favorite-list-item';

import { ModalListItemComponent } from '../../components/modal-list-item/modal-list-item.component';

import { CrudService } from '../../services/crud.service';

@Component({
  selector: 'app-list-items',
  standalone: true,
  imports: [
    RouterModule,
    PichinchaSelectorFavoriteListModule,
    PichinchaTypographyModule,
    PichinchaButtonModule,
    PichinchaLoadingScreenModule,
    ModalListItemComponent,
  ],
  providers: [CrudService],
  templateUrl: './list-items.component.html',
  styleUrl: './list-items.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListItemsComponent implements OnInit {
  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  private crudService = inject(CrudService);

  loadingData = signal<boolean>(true);
  items = signal<SelectorFavoriteListItemProps[]>([]);

  openModal = signal<boolean>(false);
  idItem = signal<string | undefined>(undefined);

  ngOnInit() {
    this.getProducts();
  }

  getProducts() {
    this.loadingData.set(true);

    this.crudService.getAll().then((resp) => {
      const list = resp.products.map((item) => {
        return {
          id: item.id,
          selectorName: item.title,
          selectorDescription: item.warrantyInformation,
          tag: {
            text: 'Precio',
            icon: 'attach_money',
            status: 'information',
          },
          paymentLabel: 'Información de envío',
          nextPayment: item.shippingInformation,
          paymentDate: `$${item.price}`,
          hideLinkButton: true,
          categoryIcon: {
            iconName: 'qr_code_scanner',
          },
        } as unknown as SelectorFavoriteListItemProps;
      });

      this.items.set(list);
      this.loadingData.set(false);
    });
  }

  handleCreateANewItem() {
    this.router.navigate(['..', 'edit'], { relativeTo: this.activatedRoute });
  }

  handleClickEdit() {
    this.router.navigate(['..', 'edit', this.idItem()], {
      relativeTo: this.activatedRoute,
    });
  }

  handleClickDelete() {
    this.crudService.delete(Number(this.idItem())).then((isOkDelete) => {
      if (isOkDelete) {
        this.idItem.set(undefined);
        this.openModal.set(false);

        this.getProducts();
      }
    });
  }

  handleClickItem(e: any) {
    const { detail } = e as unknown as CustomEvent;
    this.idItem.set(detail);
    this.openModal.set(true);
  }

  handleCloseModal() {
    this.idItem.set(undefined);
    this.openModal.set(false);
  }
}
