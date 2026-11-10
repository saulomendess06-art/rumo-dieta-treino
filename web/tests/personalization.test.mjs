import test from 'node:test';import assert from 'node:assert/strict';import{readFileSync}from'node:fs';
import{generate,totals,validateProfile}from'../dist/engine.js';
import{fresh,saveProfile,dayRecord,recordTraining,upgradePlans,parseBackup}from'../dist/store.js';
const c=JSON.parse(readFileSync(new URL('../dist/plan.json',import.meta.url)));
const base={name:'Alex',age:30,height:175,weight:80,sex:'male',activity:'low',goal:'lose',health:'none',diet:'common',experience:'beginner',place:'home',swim:false,meatLunch:true,restrictions:[],healthNote:'',restrictionNote:'',frequency:5,planVersion:2};
const signature=plan=>plan.days.map(d=>d.meals.map(m=>(m.options[0]?.name??m.options[0]?.title))).flat().join('|');
test('cardápio varia nos sete dias e objetivo participa da seleção real',()=>{
 const lose=generate(c,base),strength=generate(c,{...base,goal:'strength'}),taf=generate(c,{...base,goal:'taf'});
 assert.equal(lose.days.length,7);assert.notEqual(signature(lose),signature(strength));assert.notEqual(signature(strength),signature(taf));
 for(let slot=0;slot<4;slot++)assert.ok(new Set(lose.days.map(d=>(d.meals[slot].options[0]?.name??d.meals[slot].options[0]?.title))).size>=3);
 assert.ok(lose.adaptation.diet.length);assert.ok(strength.adaptation.training.length);
 assert.equal(signature(generate(c,{...base,name:'Outro nome'})),signature(lose));
});
test('frequência 1–6 respeita quantidade, descanso e sábado aquático',()=>{
 for(const goal of ['lose','maintain','strength','taf'])for(let frequency=1;frequency<=6;frequency++)for(const swim of [false,true]){
 const p=generate(c,{...base,goal,frequency,swim});
 for(const week of p.workouts){assert.equal(week.filter(t=>t.kind!=='rest').length,frequency);assert.ok(week.some(t=>t.kind==='rest'));if(swim)assert.equal(week[5].kind,'swim');
 const strengthDays=week.flatMap((t,i)=>t.kind==='strength'?[i]:[]);for(let i=1;i<strengthDays.length;i++)assert.ok(strengthDays[i]-strengthDays[i-1]>1,'sem força em dias consecutivos');}
 }
 for(const frequency of [0,7,2.5,'3'])assert.throws(()=>validateProfile({...base,frequency}));
});
test('experiência, local, condicionamento e objetivo mudam exercícios e dose',()=>{
 const home=generate(c,base),gym=generate(c,{...base,place:'gym'}),regular=generate(c,{...base,experience:'regular',activity:'moderate'}),joint=generate(c,{...base,health:'joint'}),taf=generate(c,{...base,goal:'taf'});
 assert.notDeepEqual(home.workouts,gym.workouts);assert.notDeepEqual(home.workouts,regular.workouts);assert.notDeepEqual(home.workouts,taf.workouts);
 assert.ok(!JSON.stringify(joint.workouts).includes('Sentar e levantar'));
 assert.ok(generate(c,{...base,weight:130}).workouts[0][0].minutes<home.workouts[0][0].minutes);
});
test('todos os dias mantêm filtros e equivalência ao personalizar',()=>{
 for(const diet of ['common','vegetarian','vegan'])for(const restrictions of [[],['milk','egg','gluten'],['soy','milk','egg']]){
 const plan=generate(c,{...base,diet,restrictions});for(const d of plan.days)for(const [slot,m] of d.meals.entries()){
 assert.ok(m.options.length===0||m.options.length===3);if(!m.options.length)continue;const first=totals(c.foods,m.options[0]);
 for(const o of m.options){const n=totals(c.foods,o);assert.ok(Math.abs(n[0]-first[0])<=first[0]*.1+.00001);assert.ok(Math.abs(n[1]-first[1])<=Math.max(3,first[1]*.2)+.00001);for(const i of o.items){const f=c.foods[i.food];assert.ok(!f.allergens.some(a=>restrictions.includes(a)));if(diet!=='common'||slot!==1)assert.equal(f.meat,false);if(diet==='vegan')assert.ok(!f.allergens.some(a=>['milk','egg'].includes(a)));}}
 }
 }
});
test('migração congela motor anterior e cria perfil novo sem reinterpretar treino',()=>{
 const s=fresh();saveProfile(s,base,'2026-11-09');const id=s.activeProfile;delete s.profiles[id].planVersion;delete s.profiles[id].frequency;
 dayRecord(s,'2026-11-09').water=500;recordTraining(s,'2026-11-09',{done:true,effort:2,pain:false,note:'antes'});
 const before=generate(c,{...s.profiles[id],planVersion:1});upgradePlans(s,'2026-11-10');
 assert.notEqual(s.activeProfile,id);assert.equal(s.profiles[id].planVersion,1);assert.equal(s.profiles[s.activeProfile].planVersion,2);assert.equal(s.profiles[s.activeProfile].frequency,5);
 assert.equal(s.days['2026-11-09'].training.profileId,id);assert.deepEqual(generate(c,s.profiles[id]),before);assert.equal(s.days['2026-11-09'].water,500);assert.deepEqual(parseBackup(JSON.stringify(s)),s);
 const active=s.activeProfile;upgradePlans(s,'2026-11-10');assert.equal(s.activeProfile,active);
});

