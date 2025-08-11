import { Routes } from '@angular/router';

import { BaseComponent } from './components/base/base.component';
import { ListItemsComponent } from './pages/list-items/list-items.component';
import { EditItemComponent } from './pages/edit-item/edit-item.component';
import { AccountResolver } from './resolvers/account.resolver';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: BaseComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'list',
        component: ListItemsComponent,
      },
      {
        path: 'edit',
        component: EditItemComponent,
      },
      {
        path: 'edit/:id',
        component: EditItemComponent,
        resolve: {
          account: AccountResolver,
        },
        providers: [AccountResolver],
      },
      {
        path: '',
        redirectTo: 'list',
        pathMatch: 'full',
      },
    ],
  },
];
