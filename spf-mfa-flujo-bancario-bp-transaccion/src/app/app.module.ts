import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { SharedModule } from './shared.module';
import { BaseComponent } from './components/base/base.component';
import { routes } from './app-routing.module';

@NgModule({
  declarations: [],
  imports: [
    BaseComponent,
    BrowserModule,
    CommonModule,
    SharedModule,
    RouterModule.forRoot(routes),
  ],
  providers: [],
  schemas: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
