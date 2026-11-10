import {validateProfile} from './engine.js';
export const KEY='rumo.web.v1';
export const today=()=>{const d=new Date();return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;};
export const dayIndex=date=>(new Date(date+'T12:00:00').getDay()+6)%7;
export function fresh(){return {format:'rumo-web',version:1,activeProfile:null,profiles:{},week:0,days:{},archivedDays:[],weights:[]};}
export function dayRecord(s,date){return s.days[date]??= {profileId:s.activeProfile,water:0,meals:Array.from({length:4},()=>({option:0,done:false})),training:null};}
export function saveProfile(s,p,date=today()){
 p={...p,frequency:p.frequency??5,planVersion:2};validateProfile(p);const id='p'+Date.now().toString(36)+'_'+Math.random().toString(36).slice(2,8);
 if(s.days[date]){s.archivedDays.push({date,record:structuredClone(s.days[date])});const r=s.days[date];r.profileId=id;r.meals=Array.from({length:4},()=>({option:0,done:false}));}
 s.profiles[id]=structuredClone(p);s.activeProfile=id;return id;
}
export function upgradePlans(s,date=today()){
 if(!s.activeProfile||s.profiles[s.activeProfile].planVersion===2)return false;
 for(const p of Object.values(s.profiles))if(p.planVersion===undefined)p.planVersion=1;
 saveProfile(s,s.profiles[s.activeProfile],date);return true;
}
export function recordTraining(s,date,fields){const r=dayRecord(s,date);r.training={...fields,profileId:r.training?.profileId??r.profileId,week:r.training?.week??s.week};}
export function persist(s,storage=localStorage){try{storage.setItem(KEY,JSON.stringify(s));}catch{throw Error('Não foi possível salvar neste navegador. Confira o espaço ou use uma janela normal. Exporte um backup para proteger os dados.');}}
function validDate(s){if(typeof s!=='string'||!/^\d{4}-\d{2}-\d{2}$/.test(s))return false;const d=new Date(s+'T12:00:00Z');return !isNaN(d)&&d.toISOString().slice(0,10)===s;}
const obj=x=>x&&typeof x==='object'&&!Array.isArray(x);
export function parseBackup(text){
 if(typeof text!=='string'||new TextEncoder().encode(text).length>8*1024*1024)throw Error('Backup excede 8 MB.');
 let depth=0,string=false,escape=false;for(const ch of text){if(string){if(escape)escape=false;else if(ch==='\\')escape=true;else if(ch==='"')string=false;}else if(ch==='"')string=true;else if(ch==='{'||ch==='['){if(++depth>32)throw Error('Backup muito complexo.');}else if(ch==='}'||ch===']')depth--;}
 let s;try{s=JSON.parse(text);}catch{throw Error('Arquivo JSON inválido.');}
 const fail=()=>{throw Error('Backup Rumo Web inválido ou incompatível. O backup Android possui outro formato.');};
 if(!obj(s)||s.format!=='rumo-web'||s.version!==1||!obj(s.profiles)||!obj(s.days)||!Array.isArray(s.archivedDays)||!Array.isArray(s.weights)||!Number.isInteger(s.week)||s.week<0||s.week>3)fail();
 for(const [id,p] of Object.entries(s.profiles)){if(!/^p[a-z0-9_]{1,60}$/.test(id))fail();validateProfile(p);}
 const profileId=id=>typeof id==='string'&&Object.hasOwn(s.profiles,id);
 if(s.activeProfile!==null&&!profileId(s.activeProfile))fail();
 const record=r=>{
 if(!obj(r)||!profileId(r.profileId)||!Number.isInteger(r.water)||r.water<0||r.water>20000||!Array.isArray(r.meals)||r.meals.length!==4)fail();
 for(const m of r.meals)if(!obj(m)||!Number.isInteger(m.option)||m.option<0||m.option>2||typeof m.done!=='boolean')fail();
 if(r.training!==null){const t=r.training;if(!obj(t)||!profileId(t.profileId)||!Number.isInteger(t.week)||t.week<0||t.week>3||typeof t.done!=='boolean'||typeof t.pain!=='boolean'||!Number.isInteger(t.effort)||t.effort<0||t.effort>10||typeof t.note!=='string'||t.note.length>500)fail();}
 };
 for(const [date,r] of Object.entries(s.days)){if(!validDate(date))fail();record(r);}
 for(const a of s.archivedDays){if(!obj(a)||!validDate(a.date))fail();record(a.record);}
 for(const w of s.weights)if(!obj(w)||!validDate(w.date)||!Number.isFinite(w.value)||w.value<30||w.value>350)fail();
 if(s.activeProfile===null&&(Object.keys(s.profiles).length||Object.keys(s.days).length||s.archivedDays.length||s.weights.length))fail();
 return s;
}
