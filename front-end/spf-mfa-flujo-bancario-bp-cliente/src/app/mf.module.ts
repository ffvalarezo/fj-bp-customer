import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { routes } from './app-routing.module';

import { SharedModule } from './shared.module';

import { BaseComponent } from './components/base/base.component';

@NgModule({
  declarations: [],
  imports: [
    BaseComponent,
    /* ------------------- */
    RouterModule.forChild(routes),
    SharedModule,
  ],
  providers: [],
})
export class MFModule {}
