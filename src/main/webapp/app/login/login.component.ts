import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { Subscription } from 'rxjs';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { FormBuilder, Validators } from '@angular/forms';
import { LoginService } from 'app/core/login/login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'jhi-home',
  templateUrl: './login.component.html',
  styleUrls: ['login.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
  @ViewChild('username', { static: false })
  username?: ElementRef;

  authenticationError = false;

  account: Account | null = null;
  authSubscription?: Subscription;

  error: string[] = [];

  loginForm = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]],
    rememberMe: [false],
  });

  constructor(
    private accountService: AccountService,
    private fb: FormBuilder,
    private loginService: LoginService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loginService.logout();
    if (this.username) {
      this.username.nativeElement.focus();
    }
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.error = [];
    this.loginService
      .login({
        username: this.loginForm.get('username')!.value,
        password: this.loginForm.get('password')!.value,
        rememberMe: this.loginForm.get('rememberMe')!.value,
      })
      .subscribe(
        acc => {
          this.authenticationError = false;
          if (acc?.loginFirst) {
            this.router.navigate(['account/password']);
          } else {
            this.router.navigate(['home']);
          }
        },
        err => {
          const errs = [];
          if (err.error.fieldErrors !== undefined) {
            err.error.fieldErrors.forEach(function (e: any) {
              errs.push(e.message);
            });
          } else {
            errs.push(err.error.detail);
          }
          this.error = errs;
          this.authenticationError = true;
        }
      );
  }

  cancel(): void {
    this.authenticationError = false;
    this.loginForm.patchValue({
      username: '',
      password: '',
    });
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
}
