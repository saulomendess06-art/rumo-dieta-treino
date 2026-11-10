export const goals={lose:'Perder peso',maintain:'Manter condicionamento',strength:'Ganhar força e massa',taf:'Começar a preparação para TAF'};
export const weekdays=['Segunda','Terça','Quarta','Quinta','Sexta','Sábado','Domingo'];
export const allergens={gluten:'Glúten',lactose:'Lactose',milk:'Leite e derivados',egg:'Ovo',soy:'Soja',peanut:'Amendoim',nuts:'Castanhas',fish:'Peixe'};
const enums={sex:['male','female','unspecified'],activity:['low','light','moderate','high'],goal:Object.keys(goals),health:['none','joint','clinical'],diet:['common','vegetarian','vegan'],experience:['beginner','regular'],place:['home','gym']};
export function validateProfile(p){
 if(!p||typeof p!=='object'||Array.isArray(p))throw Error('Perfil inválido.');
 for(const [k,min,max] of [['age',18,100],['height',130,220],['weight',30,350]])if(!Number.isFinite(p[k])||p[k]<min||p[k]>max||(k==='age'&&!Number.isInteger(p[k])))throw Error('Confira idade (18–100 anos), altura (130–220 cm) e peso (30–350 kg).');
 for(const [k,values] of Object.entries(enums))if(!values.includes(p[k]))throw Error('Selecione uma opção válida para '+k+'.');
 for(const k of ['swim','meatLunch'])if(typeof p[k]!=='boolean')throw Error('Preferência inválida.');
 for(const k of ['name','healthNote','restrictionNote'])if(typeof p[k]!=='string'||p[k].length>(k==='name'?60:500))throw Error('Texto do perfil inválido.');
 if(!Array.isArray(p.restrictions)||p.restrictions.length>8||new Set(p.restrictions).size!==p.restrictions.length||p.restrictions.some(a=>!Object.hasOwn(allergens,a)))throw Error('Restrição inválida.');
 return p;
}
export function decide(p){
 validateProfile(p);
 const d={bmr:0,tdee:0,target:0,dietAllowed:false,exerciseAllowed:p.health!=='clinical'&&!p.healthNote.trim(),lowImpact:p.health==='joint'||p.experience==='beginner'||p.age>=65,dietReason:'',exerciseReason:''};
 if(!d.exerciseAllowed)d.exerciseReason='Seu perfil precisa de orientação profissional antes de montar exercícios. O diário continua disponível.';
 if(p.health==='clinical'||p.healthNote.trim())d.dietReason='Uma condição ou informação de saúde exige avaliação individual. Não será montada dieta clínica automaticamente.';
 else if(p.restrictionNote.trim())d.dietReason='Sua restrição personalizada precisa de revisão profissional. As opções automáticas não devem ser consideradas compatíveis com ela.';
 else if(p.age>78)d.dietReason='Nesta faixa de idade, a estimativa alimentar precisa de avaliação individual.';
 else if(p.sex==='unspecified')d.dietReason='Sem o parâmetro de sexo, o diário e as orientações gerais continuam disponíveis, sem meta calórica automática.';
 else if(p.weight/(p.height/100)**2<18.5)d.dietReason='O peso em relação à altura pede avaliação antes de definir metas alimentares.';
 if(d.dietReason)return d;
 d.bmr=10*p.weight+6.25*p.height-5*p.age+(p.sex==='male'?5:-161);
 d.tdee=d.bmr*({low:1.2,light:1.375,moderate:1.55,high:1.725}[p.activity]);
 const target=p.goal==='lose'?d.tdee*.85:p.goal==='strength'?d.tdee+Math.min(150,d.tdee*.05):d.tdee;
 if(target<(p.sex==='male'?1600:1400)||target>3400){d.dietReason='A estimativa ficou fora da faixa de geração automática. Procure ajuste profissional; o cálculo não será limitado silenciosamente.';return d;}
 d.target=Math.round(target/50)*50;d.dietAllowed=true;return d;
}
export function totals(foods,option){return option.items.reduce((sum,item)=>sum.map((v,i)=>v+foods[item.food].values[i]*item.g/100),[0,0,0,0,0]);}
function allows(p,f,slot){
 if(f.meat&&(p.diet!=='common'||(p.meatLunch&&slot!==1)))return false;
 if(p.diet==='vegan'&&(f.group==='lacteo'||f.allergens.some(a=>a==='milk'||a==='egg')))return false;
 return !f.allergens.some(a=>p.restrictions.includes(a));
}
function choices(c,p,slot,kcal){
 const options=[];
 for(const recipe of c.recipes[slot]){
 if(!recipe.items.every(i=>allows(p,c.foods[i.food],slot)))continue;
 const factor=kcal/totals(c.foods,recipe)[0];const scaled=structuredClone(recipe);
 for(const item of scaled.items){const f=c.foods[item.food];const step=item.food==='egg'?50:item.food==='bread'?25:item.food==='oil'?.5:5;item.g=Math.max(step,Math.round(item.g*factor/step)*step);item.portion='≈ '+(item.g/f.unitGrams).toLocaleString('pt-BR',{maximumFractionDigits:1})+' '+f.unitLabel;}
 if(scaled.items.some(i=>i.g>500)||Math.abs(totals(c.foods,scaled)[0]-kcal)>kcal*.1)continue;
 options.push(scaled);
 }
 for(const first of options){const base=totals(c.foods,first);const chosen=[first];for(const other of options){if(other===first)continue;const n=totals(c.foods,other);if(Math.abs(n[0]-base[0])<=base[0]*.1&&Math.abs(n[1]-base[1])<=Math.max(3,base[1]*.2))chosen.push(other);if(chosen.length===3)return chosen;}}
 return [];
}
const step=(name,dose,detail)=>({name,dose,detail});
export function generate(c,p){
 const d=decide(p);const fractions=[.25,.35,.15,.25];
 const meals=c.days[0].meals.map((m,i)=>({...m,options:d.dietAllowed?choices(c,p,i,d.target*fractions[i]):[],reason:d.dietAllowed?'Não encontramos três alternativas equivalentes compatíveis. Esta refeição precisa de ajuste profissional.':d.dietReason}));
 const regular=p.experience==='regular'&&!d.lowImpact;const base=regular?(p.activity==='high'?35:30):15;
 const workouts=Array.from({length:4},(_,w)=>weekdays.map((_,day)=>{
 if(day===2||day===6)return {kind:'rest',title:'Descanso e recuperação',minutes:0,effort:'Sem meta de esforço',steps:[step('Recupere-se','Dia de descanso','Priorize sono e recuperação. Movimentos suaves apenas se estiver confortável. Não é obrigatório fazer atividade hoje.')]};
 let kind=day===1||day===4||(p.goal==='strength'&&[0,3,5].includes(day))?'strength':'walk';
 if(p.goal==='strength'&&(day===1||day===4))kind='walk';if(day===5&&p.swim)kind='swim';
 const t={kind,minutes:0,effort:regular?'Confortável · 3–5 de 10':'Leve · 2–3 de 10',steps:[]};
 if(!d.exerciseAllowed)return {...t,kind:'guide',title:'Plano profissional antes de treinar',steps:[step('Converse com um profissional','Sem treino automático',d.exerciseReason)]};
 if(kind==='walk'){t.minutes=base+w*3;t.title=d.lowImpact?'Caminhada leve, sem impacto':'Caminhada de condicionamento';t.steps=[step('Comece devagar','5 min','Terreno plano e regular. Sem mudanças rápidas de direção.'),step('Ritmo confortável',(t.minutes-10)+' min','Você deve conseguir conversar. Reduza o tempo ou faça pausas se necessário. Não é uma sessão de corrida.'),step('Desacelere','5 min','Observe recuperação e sintomas no dia seguinte. Só avance a semana se estiver bem.')];}
 else if(kind==='swim'){t.minutes=regular?30:20;t.title='Natação leve e técnica';t.steps=[step('Adapte-se à água','5 min','Piscina com supervisão no local. Apenas para quem sabe nadar e tem acesso à piscina.'),step('Trechos curtos com descanso',(t.minutes-10)+' min com pausas','Ritmo confortável. Descanse 30–60 s ou mais. Ajuste pernadas e viradas se houver sintomas articulares.'),step('Finalize devagar','5 min','Sem apneia ou hiperventilação. Nunca treine sozinho.')];}
 else {t.minutes=regular?30:20;t.title=d.lowImpact?'Força leve e controle':'Força para o corpo todo';const dose=(regular||w>=2?2:1)+' série(s) de '+(regular?8:6)+' repetições';t.steps=[step('Aqueça','5 min','Movimentos confortáveis de ombros, tornozelos e caminhada leve, se tolerada.')];
 if(p.place==='gym'&&!d.lowImpact)t.steps.push(step('Remada na máquina',dose,'Peça ajuste ao instrutor. Carga confortável, sem balanço ou falha muscular.'),step('Supino na máquina',dose,'Movimento controlado, respirando normalmente. Pare antes da exaustão.'),step('Leg press leve',dose,'Com orientação de ajuste, amplitude confortável e sem travar os joelhos. Pare se houver dor.'));
 else {t.steps.push(step('Flexão na parede',dose,'Mãos na altura do peito. Pés perto da parede para facilitar, tronco alinhado.'),step('Retração das escápulas',dose,'Sentado, aproxime suavemente as escápulas por 2 segundos. Respire normalmente.'));if(p.health!=='joint')t.steps.push(step('Sentar e levantar',dose,'Cadeira firme e alta encostada na parede. Apoie as mãos se precisar. Somente sem dor ou instabilidade.'));}
 t.steps.push(step('Ativação do abdômen','5 vezes de 5 segundos','Sentado em cadeira firme, ative suavemente o abdômen enquanto respira. Relaxe entre repetições.'),step('Pausas e finalização','60–90 s entre séries; 3 min ao final','Descanse mais se precisar. Técnica antes de carga. Interrompa com dor, inchaço ou instabilidade.'));}
 return t;
 }));
 return {...d,meals,workouts};
}
