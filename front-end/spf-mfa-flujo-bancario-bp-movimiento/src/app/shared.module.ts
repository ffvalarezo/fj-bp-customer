import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { EventBusModule } from "@pichincha/angular-sdk/eventbus";
import { HttpModule } from "@pichincha/angular-sdk/http";
import { StorageModule } from "@pichincha/angular-sdk/storage";
import { EStorageType } from "@pichincha/angular-sdk/auth";

import { AppComponent } from './app.component';

import { AccountService } from './services/account.service';

import { environment } from 'src/environments/environment';

@NgModule({
  declarations: [AppComponent],
  imports: [
    RouterModule,

    /* --- pichincha-angular-sdk ---*/
    EventBusModule,
    StorageModule.forRoot({
      storageType: EStorageType.SESSION,
      secretKey: environment.storage.key,
    }),
    HttpModule.forRoot({ api_url: environment.apiUrl }),
  ],
  providers: [AccountService],
  exports: [AppComponent],
  schemas: [],
})
export class SharedModule {}
