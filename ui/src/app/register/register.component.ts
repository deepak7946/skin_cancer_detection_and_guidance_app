import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';

import { AlertService, UserService, AuthenticationService } from '../_services';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent implements OnInit {

  registerForm: FormGroup;
  loading = false;
  submitted = false;
  genders;


  constructor(
      private formBuilder: FormBuilder,
      private router: Router,
      private authenticationService: AuthenticationService,
      private userService: UserService,
      private alertService: AlertService,
  ) {
        this.genders =  ["M", "F", "O"];
      // redirect to home if already logged in
      if (this.authenticationService.currentUserValue) {
          this.router.navigate(['/']);
      }
  }

  ngOnInit() {
      this.registerForm = this.formBuilder.group({
          firstname: ['', Validators.required],
          lastname: ['', Validators.required],
          username: ['', Validators.required],
          gender: ['', Validators.required],
          dob: ['', Validators.required],
          password: ['', [Validators.required, Validators.minLength(6)]]
      });
  }

  // convenience getter for easy access to form fields
  get f() { return this.registerForm.controls; }

  onSubmit() {
      this.submitted = true;

      // reset alerts on submit
      this.alertService.clear();

      // stop here if form is invalid
      if (this.registerForm.invalid) {
          return;
      }

      this.loading = true;
      this.userService.register(this.registerForm.value)
          .pipe(first())
          .subscribe(
              data => {
                  this.alertService.success('Registration successful', true);
                  this.router.navigate(['/login']);
              },
              error => {
                  this.alertService.error(error);
                  this.loading = false;
              });
  }
}
