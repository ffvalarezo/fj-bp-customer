import { Component, ViewEncapsulation } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-base',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './base.component.html',
  styleUrls: ['./base.component.scss'],
  encapsulation: ViewEncapsulation.ShadowDom,
})
export class BaseComponent {
}
