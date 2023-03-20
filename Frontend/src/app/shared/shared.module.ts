import { NgModule } from '@angular/core';

import { AccordionAnchorDirective, AccordionLinkDirective, AccordionDirective } from './accordion';
import { ShopItems } from './shop-items';


@NgModule({
  declarations: [
    AccordionAnchorDirective,
    AccordionLinkDirective,
    AccordionDirective
  ],
  exports: [
    AccordionAnchorDirective,
    AccordionLinkDirective,
    AccordionDirective
   ],
  providers: [ ShopItems]
})
export class SharedModule { }
