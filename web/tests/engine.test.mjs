import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import {decide, generate, totals, validateProfile} from '../dist/engine.js';
const catalog=JSON.parse(readFileSync(new URL('../dist/plan.json',import.meta.url)));
export const profile={name:'Alex',age:30,height:175,weight:80,sex:'male',activity:'low',goal:'lose',health:'none',diet:'common',experience:'beginner',place:'home',swim:false,meatLunch:true,restrictions:[],healthNote:'',restrictionNote:''};
test('estimativa e cinco atividades com descanso intercalado',()=>{
 const d=decide(profile);assert.equal(d.target,1800);assert.equal(d.bmr,1748.75);
 const p=generate(catalog,profile);assert.equal(p.workouts.length,4);
 assert.deepEqual(p.workouts[0].map(t=>t.kind),['walk','strength','rest','walk','strength','walk','rest']);
 assert.equal(p.workouts[0][0].minutes,15);assert.equal(p.workouts[3][0].minutes,24);
});
test('não gerar dieta clínica nem ignorar notas livres',()=>{
 for(const extra of [{health:'clinical'},{healthNote:'medicamento'},{restrictionNote:'outra alergia'},{sex:'unspecified'},{age:79},{weight:45}])assert.equal(decide({...profile,...extra}).dietAllowed,false);
 assert.equal(decide({...profile,health:'clinical'}).exerciseAllowed,false);
 assert.equal(decide({...profile,restrictionNote:'outra'}).exerciseAllowed,true);
});
test('filtros e equivalência antes de oferecer três refeições',()=>{
 for(const diet of ['common','vegetarian','vegan'])for(const restrictions of [[],['gluten'],['milk','egg','soy'],['lactose']]){
 const p={...profile,diet,restrictions};const plan=generate(catalog,p);
 for(const [slot,m] of plan.meals.entries()){
 assert.ok([0,3].includes(m.options.length));if(!m.options.length)continue;
 const base=totals(catalog.foods,m.options[0]);
 for(const o of m.options){const n=totals(catalog.foods,o);assert.ok(Math.abs(n[0]-base[0])<=base[0]*.1+.00001);assert.ok(Math.abs(n[1]-base[1])<=Math.max(3,base[1]*.2)+.00001);
 for(const item of o.items){const f=catalog.foods[item.food];assert.ok(item.g<=500);assert.ok(!f.allergens.some(a=>restrictions.includes(a)));if(diet!=='common'||slot!==1)assert.equal(f.meat,false);if(diet==='vegan')assert.ok(!f.allergens.some(a=>['milk','egg'].includes(a)));}
 }
 }
 }
});
test('natação no sábado e proteção articular',()=>{
 const plan=generate(catalog,{...profile,swim:true,health:'joint'});assert.equal(plan.workouts[0][5].kind,'swim');
 assert.ok(!JSON.stringify(plan.workouts).includes('Sentar e levantar'));
});
test('validação recusa números e códigos inválidos',()=>{
 assert.doesNotThrow(()=>validateProfile(profile));
 for(const extra of [{age:17},{height:NaN},{weight:400},{goal:'inventado'},{swim:'true'},{restrictions:['unknown']}])assert.throws(()=>validateProfile({...profile,...extra}));
});
