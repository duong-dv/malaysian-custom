import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Observable } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { PasswordService } from './password.service';
import { Router } from '@angular/router';
import { LoginService } from '../../core/login/login.service';

@Component({
  selector: 'jhi-password',
  templateUrl: './password.component.html',
})
export class PasswordComponent implements OnInit {
  doNotMatch = false;
  error = false;
  errorStr = '';
  errorQuestion = false;
  success = false;
  account$?: Observable<Account | null>;
  passwordForm = this.fb.group({
    currentPassword: ['', [Validators.required]],
    newPassword: [
      '',
      [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(8),
        Validators.pattern('^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+)$'),
      ],
    ],
    confirmPassword: [
      '',
      [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(8),
        Validators.pattern('^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+)$'),
      ],
    ],
    questionOne: 0,
    questionTwo: 0,
    questionThree: 0,
    questionAnswerOne: [''],
    questionAnswerTwo: [''],
    questionAnswerThree: [''],
  });

  constructor(
    private passwordService: PasswordService,
    private accountService: AccountService,
    private fb: FormBuilder,
    private router: Router,
    private loginService: LoginService
  ) {}

  ngOnInit(): void {
    this.account$ = this.accountService.identity();
  }

  cancel(): void {
    if (this.accountService.getLoginFirst()) {
      this.loginService.logout();
      this.router.navigate(['']);
    }
    this.router.navigate(['home']);
  }

  changePassword(): void {
    this.errorStr = '';
    this.error = false;
    this.errorQuestion = false;
    this.success = false;
    this.doNotMatch = false;

    if (
      this.passwordForm.get(['questionOne'])!.value === 0 &&
      this.passwordForm.get(['questionTwo'])!.value === 0 &&
      this.passwordForm.get(['questionThree'])!.value === 0
    ) {
      this.errorQuestion = true;
      return;
    } else {
      if (
        (this.passwordForm.get(['questionOne'])!.value !== 0 && this.passwordForm.get(['questionAnswerOne'])!.value?.trim() === '') ||
        this.passwordForm.get(['questionAnswerOne'])!.value?.trim() === undefined
      ) {
        this.errorQuestion = true;
        return;
      }
      if (
        this.passwordForm.get(['questionTwo'])!.value !== 0 &&
        (this.passwordForm.get(['questionAnswerTwo'])!.value?.trim() === '' ||
          this.passwordForm.get(['questionAnswerTwo'])!.value?.trim() === undefined)
      ) {
        this.errorQuestion = true;
        return;
      }
      if (
        this.passwordForm.get(['questionThree'])!.value !== 0 &&
        (this.passwordForm.get(['questionAnswerThree'])!.value?.trim() === '' ||
          this.passwordForm.get(['questionAnswerThree'])!.value?.trim() === undefined)
      ) {
        this.errorQuestion = true;
        return;
      }
    }

    const newPassword = this.passwordForm.get(['newPassword'])!.value;
    if (newPassword !== this.passwordForm.get(['confirmPassword'])!.value) {
      this.doNotMatch = true;
    } else {
      this.passwordService
        .save(
          newPassword,
          this.passwordForm.get(['currentPassword'])!.value,
          this.passwordForm.get(['questionOne'])!.value,
          this.passwordForm.get(['questionTwo'])!.value,
          this.passwordForm.get(['questionThree'])!.value,
          this.passwordForm.get(['questionAnswerOne'])!.value,
          this.passwordForm.get(['questionAnswerTwo'])!.value,
          this.passwordForm.get(['questionAnswerThree'])!.value
        )
        .subscribe(
          () => {
            this.success = true;
            this.accountService.setLoginFirst();
          },
          err => {
            this.error = true;
            this.errorStr = err.error.detail;
          }
        );
    }
  }
}
