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
 for(const [key,max] of [['frequency',6],['planVersion',2]])if(p[key]!==undefined&&(!Number.isInteger(p[key])||p[key]<1||p[key]>max))throw Error('Frequência: escolha de 1 a 6 dias por semana.');
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
export function allows(p,f,slot){
 if(f.meat&&(p.diet!=='common'||(p.meatLunch&&slot!==1)))return false;
 if(p.diet==='vegan'&&(f.group==='lacteo'||f.allergens.some(a=>a==='milk'||a==='egg')))return false;
 return !f.allergens.some(a=>p.restrictions.includes(a));
}
