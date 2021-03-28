import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { MalaysiancustomSharedModule } from 'app/shared/shared.module';
import { MalaysiancustomCoreModule } from 'app/core/core.module';
import { MalaysiancustomAppRoutingModule } from './app-routing.module';
import { MalaysiancustomLoginModule } from './login/login.module';
import { MalaysiancustomEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ActiveMenuDirective } from './layouts/navbar/active-menu.directive';
import { ErrorComponent } from './layouts/error/error.component';
import { FooterComponent } from 'app/layouts/footer/footer.component';
import { MalaysiancustomHomeModule } from 'app/home/home.module';

@NgModule({
  imports: [
    BrowserModule,
    MalaysiancustomSharedModule,
    MalaysiancustomCoreModule,
    MalaysiancustomLoginModule,
    MalaysiancustomHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    MalaysiancustomEntityModule,
    MalaysiancustomAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
  bootstrap: [MainComponent],
})
export class MalaysiancustomAppModule {}
