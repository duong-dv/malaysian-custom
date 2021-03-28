import { Route } from '@angular/router';

import { LoginComponent } from './login.component';
import { Authority } from 'app/shared/constants/authority.constants';

export const HOME_ROUTE: Route = {
  path: 'login',
  component: LoginComponent,
  data: {
    authorities: [Authority.ADMIN],
    pageTitle: 'home.title',
  },
};
