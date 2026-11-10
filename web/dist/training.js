import {weekdays} from './engine-core.js';
export function trainingDays(frequency,swim){
 const days={1:[1],2:[1,4],3:[0,3,5],4:[0,1,3,5],5:[0,1,3,4,5],6:[0,1,2,3,4,5]}[frequency].slice();
 if(swim&&!days.includes(5))days[days.length-1]=5;return days;
}
function strengthDays(days,count,swim,goal){
 const possible=days.filter(d=>!(swim&&d===5)),preferred=goal==='strength'?[0,3,5]:[1,4,0];
 for(let n=Math.min(count,possible.length);n>=0;n--){let best=[],bestScore=-Infinity;const choose=(start,selected)=>{
 if(selected.length===n){const score=selected.reduce((s,d)=>s+(preferred.includes(d)?10-preferred.indexOf(d):0),0);if(score>bestScore){best=selected.slice();bestScore=score;}return;}
 for(let i=start;i<possible.length;i++){const d=possible[i];if(selected.some(v=>Math.abs(d-v)===1))continue;choose(i+1,[...selected,d]);}
 };choose(0,[]);if(bestScore>-Infinity)return best;}
 return [];
}
const step=(name,dose,detail)=>({name,dose,detail});
export const movements={
 wall: ['Flexão na parede','Mãos na altura do peito. Ajuste a distância para facilitar. Tronco alinhado; sem dor.'],
 incline:['Flexão em apoio alto e firme','Bancada fixa e estável. Sem ir até a falha; use a parede se a técnica piorar.'],
 row:['Retração das escápulas sentado','Aproxime suavemente as escápulas por 2 segundos, sem elevar ombros ou prender a respiração.'],
 machineRow:['Remada na máquina','Peça ajuste ao instrutor. Carga leve e movimento controlado, sem balanço.'],
 machinePress:['Supino na máquina','Peça ajuste ao instrutor. Comece leve e pare antes da exaustão; sem dor.'],
 chair:['Sentar e levantar','Cadeira firme e alta encostada na parede. Apoie as mãos se necessário. Só faça sem dor ou instabilidade.'],
 leg:['Leg press leve','Com orientação do instrutor, carga leve e amplitude confortável. Sem travar os joelhos. Pare com dor.'],
 heel:['Elevação dos calcanhares com apoio','Segure um apoio fixo, suba e desça lentamente. Interrompa com desconforto ou desequilíbrio.'],
 hinge:['Movimento de quadril sem carga','Em pé junto a apoio firme, leve levemente o quadril para trás sem curvar as costas. Amplitude pequena, sem dor.'],
 core:['Ativação do abdômen sentado','Cadeira firme. Ative suavemente o abdômen enquanto respira; relaxe entre repetições.'],
 wallCore:['Estabilidade do tronco na parede','Antebraços na parede e pés perto dela. Mantenha o tronco alinhado, sem prender a respiração.']
};
export function training(p,d){
 const frequency=p.frequency??5,days=trainingDays(frequency,p.swim),bmi=p.weight/(p.height/100)**2;
 const protectedStart=p.health==='joint'||p.age>=65||bmi>=35;
 const level=protectedStart?0:p.experience==='beginner'||p.activity==='low'?1:p.activity==='light'?2:3;
 const regular=level>=2;
 const wanted=p.goal==='strength'?[0,1,2,2,3,3,3][frequency]:[0,1,1,2,2,2,3][frequency];
 const strength=strengthDays(days,protectedStart?Math.min(2,wanted):wanted,p.swim,p.goal);
 const base=[12,15,22,30][level],growth=level===0?2:3;
 const reasons=[`${frequency} ${frequency===1?'treino':'treinos'} por semana, com ${7-frequency} ${7-frequency===1?'dia':'dias'} de descanso.`,
  protectedStart?'Começo conservador: menor duração e movimentos apoiados pelo histórico articular, idade ou relação peso/altura.':regular?'Experiência regular e atividade informada permitem sessões um pouco mais longas.':'Iniciante, retorno ou rotina pouco ativa: sessões curtas, sem corrida ou saltos.',
  p.goal==='strength'?'Prioridade em força, com sessões separadas por recuperação.':p.goal==='taf'?'Base para TAF: resistência aeróbica, empurrar e controle do tronco, sem presumir índices ou prontidão para corrida.':p.goal==='lose'?'Caminhadas para constância e sessões de força para acompanhar o objetivo de perda de peso.':'Equilíbrio entre condicionamento, força e recuperação.',
  p.place==='gym'?'Exercícios em aparelhos com ajuste orientado pelo instrutor.':'Movimentos em casa com parede, cadeira e apoios firmes.'];
 if(p.swim)reasons.push('Natação no sábado porque você declarou domínio, acesso à piscina supervisionada e preferência.');
 if(frequency<=2)reasons.push('Com poucas sessões, força e caminhada podem compartilhar o mesmo dia. A frequência escolhida não cobre todos os componentes de uma preparação completa.');
 const workouts=Array.from({length:4},(_,w)=>weekdays.map((_,day)=>{
 if(!days.includes(day))return {kind:'rest',title:'Descanso e recuperação',minutes:0,effort:'Sem meta de esforço',steps:[step('Recupere-se','Dia sem treino','Sono e recuperação. Movimentos suaves apenas se estiver confortável; não é obrigatório treinar hoje.')]};
 if(!d.exerciseAllowed)return {kind:'guide',title:'Plano profissional antes de treinar',minutes:0,effort:'Sem treino automático',steps:[step('Converse com um profissional','Avaliação individual',d.exerciseReason)]};
 const kind=day===5&&p.swim?'swim':strength.includes(day)?'strength':'walk';
 const t={kind,minutes:0,effort:regular?'Confortável · 3–5 de 10':'Leve · 2–3 de 10',steps:[]};
 if(kind==='walk'){
 t.minutes=Math.min(p.goal==='strength'?28:42,base+w*growth+(p.goal==='taf'&&regular?3:0));
 t.title=p.goal==='strength'?'Caminhada de recuperação':p.goal==='taf'?'Base aeróbica para TAF':p.goal==='lose'?'Caminhada para constância':'Condicionamento e mobilidade';
 t.steps.push(step('Comece devagar','5 min','Terreno plano e regular, sem mudanças rápidas de direção.'));
 const brisk=regular&&!protectedStart&&(p.goal==='taf'||(p.goal==='maintain'&&day===3));
 t.steps.push(step(brisk?'Alternância de caminhada confortável':'Caminhada contínua confortável',(t.minutes-10)+' min',brisk?'Alterne 2 minutos confortáveis e 1 minuto um pouco mais vivo. Continue conseguindo conversar. Caminhada, sem corrida.':'Ritmo em que consiga conversar. Faça pausas e reduza a duração se precisar.'));
 t.steps.push(step('Desacelere e observe','5 min','Recupere-se devagar. Só avance de semana se estiver bem durante a atividade e no dia seguinte.'));
 }else if(kind==='swim'){
 t.minutes=(protectedStart?15:regular?25:20)+w*2;t.title=p.goal==='taf'?'Base aquática para TAF':'Natação leve e técnica';
 t.steps=[step('Adapte-se à água','5 min','Piscina supervisionada. Nunca treine sozinho.'),step(p.goal==='taf'?'Técnica e trechos curtos':'Trechos confortáveis com pausas',(t.minutes-10)+' min com pausas','Sem meta de velocidade. Descanse 30–60 s ou mais entre trechos. Ajuste pernadas e viradas se houver sintomas articulares.'),step('Finalize devagar','5 min','Sem apneia, hiperventilação ou simulado máximo. Observe sua recuperação.')];
 }else {
 const index=strength.indexOf(day),variant=index%2,sets=level===0?1:regular?(p.goal==='strength'&&w>=2?3:2):w>=2?2:1;
 const reps=regular?(p.goal==='strength'?8:10):6;
 t.minutes=(protectedStart?16:regular?28:20)+(sets>1?4:0)+(frequency<=2?5:0);
 t.title=(p.goal==='taf'?'Força de base para TAF':p.goal==='strength'?'Força progressiva':p.goal==='lose'?'Força para acompanhar a perda de peso':'Força e controle')+' · '+(variant?'B':'A');
 t.steps.push(step('Aqueça','5 min','Ombros, tornozelos e caminhada leve, apenas se tolerada.'));
 const upper=p.place==='gym'?['machineRow','machinePress']:[regular?'incline':'wall','row'];
 let keys=[...upper];if(p.health!=='joint')keys.push(p.place==='gym'?'leg':variant?'hinge':'chair');
 if(!protectedStart&&variant&&p.goal!=='taf')keys.push('heel');keys.push(p.goal==='taf'||variant?'wallCore':'core');
 for(const key of keys){const [name,detail]=movements[key];const dose=key==='core'||key==='wallCore'?`${sets} série(s) de 5 ativações de 5 segundos`:`${sets} série(s) de ${reps} repetições`;t.steps.push(step(name,dose,detail));}
 if(frequency<=2)t.steps.push(step('Caminhada complementar','5 min','Em ritmo leve e terreno plano, se tolerada. Pode separar da força no mesmo dia.'));
 t.steps.push(step('Pausas e finalização','60–90 s entre séries; 3 min ao final','Descanse mais se precisar. Sem falha muscular. Pare com dor, inchaço ou instabilidade. Não aumente carga e volume ao mesmo tempo.'));
 }
 return t;
 }));
 return {workouts,reasons};
}
