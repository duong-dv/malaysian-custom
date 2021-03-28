import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MalaysiancustomSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './login.route';
import { LoginComponent } from './login.component';

@NgModule({
  imports: [MalaysiancustomSharedModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [LoginComponent],
})
export class MalaysiancustomLoginModule {}
