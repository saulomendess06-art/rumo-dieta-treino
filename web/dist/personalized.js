import{decide,totals,allows,goals,weekdays}from'./engine-core.js';
import{training}from'./training.js';
const composition={lose:{protein:.23,carb:.48,fiber:14},maintain:{protein:.18,carb:.5,fiber:14},strength:{protein:.25,carb:.45,fiber:14},taf:{protein:.2,carb:.55,fiber:14}};
const fractions={lose:[.25,.35,.15,.25],maintain:[.25,.35,.15,.25],strength:[.25,.3,.2,.25],taf:[.25,.35,.2,.2]};
function candidates(c,p,slot,kcal){
 const focus=composition[p.goal],result=[];
 for(const [index,recipe]of c.recipes[slot].entries()){
 if(!recipe.items.every(i=>allows(p,c.foods[i.food],slot)))continue;
 const factor=kcal/totals(c.foods,recipe)[0],option=structuredClone(recipe);
 for(const item of option.items){const f=c.foods[item.food],step=item.food==='egg'?50:item.food==='bread'?25:item.food==='oil'?.5:5;item.g=Math.max(step,Math.round(item.g*factor/step)*step);item.portion='≈ '+(item.g/f.unitGrams).toLocaleString('pt-BR',{maximumFractionDigits:1})+' '+f.unitLabel;}
 const n=totals(c.foods,option);if(option.items.some(i=>i.g>500)||Math.abs(n[0]-kcal)>kcal*.1)continue;
 const score=Math.abs(n[1]*4/n[0]-focus.protein)*50+Math.abs(n[2]*4/n[0]-focus.carb)*15+Math.max(0,focus.fiber-n[4]*1000/n[0])*.3+Math.abs(n[0]-kcal)/kcal;
 result.push({option,n,score,index});
 }
 return result;
}
function selectTrio(options,used){
 const ranked=options.slice().sort((a,b)=>(a.score+(used[a.index]??0)*20)-(b.score+(used[b.index]??0)*20)||a.index-b.index);
 for(const first of ranked){
 const compatible=options.filter(o=>o!==first&&Math.abs(o.n[0]-first.n[0])<=first.n[0]*.1&&Math.abs(o.n[1]-first.n[1])<=Math.max(3,first.n[1]*.2));
 if(compatible.length<2)continue;
 const selected=[first];
 while(selected.length<3){compatible.sort((a,b)=>{
 const score=o=>Math.abs(o.n[0]-first.n[0])/first.n[0]+Math.abs(o.n[1]-first.n[1])/Math.max(3,first.n[1])+selected.reduce((sum,s)=>{const keys=new Set(s.option.items.map(i=>i.food));return sum+o.option.items.filter(i=>keys.has(i.food)).length/o.option.items.length;},0)*.35;
 return score(a)-score(b)||a.score-b.score||a.index-b.index;
 });selected.push(compatible.shift());}
 used[first.index]=(used[first.index]??0)+1;return selected.map(o=>structuredClone(o.option));
 }
 return [];
}
export function generatePersonalized(c,p,day=0){
 const d=decide(p),distribution=fractions[p.goal],options=distribution.map((f,slot)=>d.dietAllowed?candidates(c,p,slot,d.target*f):[]),used=[{},{},{},{}];
 const days=weekdays.map((name,index)=>({name,meals:c.days[0].meals.map((m,slot)=>({...m,options:selectTrio(options[slot],used[slot]),reason:d.dietAllowed?'Não encontramos três alternativas equivalentes compatíveis. Esta refeição precisa de ajuste profissional.':d.dietReason}))}));
 const {workouts,reasons}=training(p,d);
 const diet=[`Objetivo: ${goals[p.goal]}.`,d.dietAllowed?`Referência de ${d.target} kcal/dia calculada com peso, altura, idade, parâmetro de sexo e atividade informada.`:d.dietReason,
 p.goal==='strength'?'Seleção prioriza preparações próximas da referência de proteína para a rotina de força e distribui mais energia no lanche.':p.goal==='lose'?'Seleção considera proteína, fibras e a redução energética estimada para organizar a perda de peso.':p.goal==='taf'?'Distribuição dá espaço a carboidratos para a rotina de condicionamento, sem déficit automático.':'Seleção busca equilíbrio entre energia, proteína e fibras para a manutenção.',
 'As preparações principais alternam ao longo dos sete dias dentro das opções compatíveis. As alternativas continuam próximas em energia e proteína.',
 p.diet==='vegan'?'Sem carnes, ovos ou laticínios.':p.diet==='vegetarian'?'Sem carnes; ovos e laticínios podem entrar quando permitidos.':p.meatLunch?'Carne apenas no almoço, conforme sua preferência.':'Padrão alimentar comum, respeitando as restrições selecionadas.'];
 if(p.restrictions.length)diet.push('Ingredientes marcados com os alérgenos selecionados são excluídos antes da escolha. Verifique rótulos e contaminação cruzada.');
 return {...d,days,meals:days[day].meals,workouts,adaptation:{diet,training:reasons},planVersion:2};
}
