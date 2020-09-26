
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login';
import { RegisterComponent } from './register';
import { HomeComponent } from './home';
import { AboutComponent } from './about';
import { AuthGuard } from './_helpers';
import { PolicyComponent } from './policy/policy.component';
import { TrackComponent } from './track/track.component';

const routes: Routes = [
  { path: '', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'about', component: AboutComponent }, 
  { path: 'info', component: PolicyComponent }, 
  { path: 'track', component: TrackComponent }, 
  // otherwise redirect to home
  { path: '**', redirectTo: '' }
];

export const AppRoutingModule = RouterModule.forRoot(routes);
