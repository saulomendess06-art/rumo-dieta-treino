export * from './engine-core.js';
import {generate as previous} from './engine-v1.js';
import {generatePersonalized} from './personalized.js';
export function generate(c,p,day=0){return p.planVersion===1?previous(c,p):generatePersonalized(c,p,day);}
