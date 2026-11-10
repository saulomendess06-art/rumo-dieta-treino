const {test}=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs');
const plan=JSON.parse(fs.readFileSync(require('node:path').join(__dirname,'../assets/plan.json'),'utf8'));
function totals(o){return o.items.reduce((a,x)=>{const f=plan.foods[x.food];for(let i=0;i<5;i++)a[i]+=f.values[i]*x.g/100;return a;},[0,0,0,0,0]);}
test('cada dia oferece quatro refeicoes com uma principal e duas alternativas',()=>{
 assert.equal(plan.days?.length,7);
 for(const d of plan.days){assert.equal(d.meals.length,4);for(const m of d.meals)assert.equal(m.options.length,3);}
});
test('alternativas mantem energia e proteina proximas com porcoes reais',()=>{
 assert.ok(plan.days?.length);
 for(const d of plan.days)for(const m of d.meals){const a=totals(m.options[0]);assert.ok(a[0]>200);
  for(const o of m.options){const b=totals(o);assert.ok(Math.abs(b[0]-a[0])/a[0]<=.10,`${d.name}/${m.label}/${o.title}: energia ${b[0]} vs ${a[0]}`);
   assert.ok(Math.abs(b[1]-a[1])<=Math.max(a[1]*.20,3),`${d.name}/${m.label}: proteina ${b[1]} vs ${a[1]}`);
   for(const i of o.items){assert.ok(i.g>0 && i.g<=500);assert.ok(i.portion.length>2);assert.ok(plan.foods[i.food].taco>0);}
  }
 }
});
test('cardapio sem whey e carne somente no almoco',()=>{
 assert.ok(plan.days?.length);
 assert.doesNotMatch(JSON.stringify(plan.days),/whey/i);
 for(const d of plan.days)for(let j=0;j<4;j++)for(const o of d.meals[j].options)for(const i of o.items)if(j!==1)assert.equal(plan.foods[i.food].meat,false);
});
test('estimativa diaria nao cria uma dieta extrema e preserva vegetais e frutas',()=>{
 assert.ok(plan.days?.length);
 for(const d of plan.days){const sums=d.meals.map(m=>totals(m.options[0]));const kcal=sums.reduce((a,x)=>a+x[0],0);assert.ok(kcal>=2000 && kcal<=2400,`${d.name}: ${kcal}`);
  for(const idx of [1,3])assert.ok(d.meals[idx].options.every(o=>o.items.some(i=>plan.foods[i.food].group==='vegetal')));
  for(const idx of [0,2])assert.ok(d.meals[idx].options.every(o=>o.items.some(i=>plan.foods[i.food].group==='fruta')));
 }
});
test('cada semana tem cinco atividades com pausas na quarta e domingo e natacao no sabado',()=>{
 assert.equal(plan.workouts?.length,4);
 for(const w of plan.workouts){assert.equal(w.length,7);assert.equal(w.filter(x=>x.kind!=='rest').length,5);assert.equal(w[2].kind,'rest');assert.equal(w[6].kind,'rest');assert.equal(w[5].kind,'swim');}
});
