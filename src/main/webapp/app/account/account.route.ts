import { Routes } from '@angular/router';
import { passwordRoute } from './password/password.route';

const ACCOUNT_ROUTES = [passwordRoute];

export const accountState: Routes = [
  {
    path: '',
    children: ACCOUNT_ROUTES,
  },
];
