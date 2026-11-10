import test from 'node:test';import assert from 'node:assert/strict';
import {fresh,saveProfile,dayRecord,recordTraining,parseBackup,persist} from '../dist/store.js';
const p={name:'Alex',age:30,height:175,weight:80,sex:'male',activity:'low',goal:'lose',health:'none',diet:'common',experience:'beginner',place:'home',swim:false,meatLunch:true,restrictions:[],healthNote:'',restrictionNote:''};
test('histórico preserva perfil e semana do treino após editar cadastro',()=>{
 const s=fresh();saveProfile(s,p,'2026-11-10');const id=s.activeProfile;s.week=2;
 dayRecord(s,'2026-11-10').water=500;recordTraining(s,'2026-11-10',{done:true,effort:3,pain:false,note:'leve'});
 dayRecord(s,'2026-11-10').meals[0]={option:2,done:true};saveProfile(s,{...p,health:'clinical'},'2026-11-10');
 assert.equal(s.days['2026-11-10'].training.profileId,id);assert.equal(s.days['2026-11-10'].training.week,2);assert.equal(s.days['2026-11-10'].water,500);assert.equal(s.days['2026-11-10'].meals[0].done,false);
 recordTraining(s,'2026-11-10',{done:true,effort:4,pain:false,note:'editado'});assert.equal(s.days['2026-11-10'].training.profileId,id);assert.ok(s.archivedDays.length);
});
test('backup válido roundtrip e inválidos recusados',()=>{
 const s=fresh();saveProfile(s,p,'2026-11-10');assert.deepEqual(parseBackup(JSON.stringify(s)),s);
 for(const x of ['{}','null','[]','{',JSON.stringify({...s,week:10}),JSON.stringify({...s,activeProfile:'missing'}),' '.repeat(8*1024*1024+1)])assert.throws(()=>parseBackup(x));
 const bad=structuredClone(s);dayRecord(bad,'2026-11-10').water=-1;assert.throws(()=>parseBackup(JSON.stringify(bad)));
});
test('quota e armazenamento bloqueado informam erro',()=>{
 assert.throws(()=>persist(fresh(),{setItem(){throw Error('QuotaExceeded');}}),/salvar/);
});
test('primeiro registro em data antiga usa o perfil apresentado naquela data',()=>{
 const s=fresh();saveProfile(s,p,'2026-11-09');const old=s.activeProfile;dayRecord(s,'2026-11-09');
 saveProfile(s,{...p,health:'clinical'},'2026-11-10');recordTraining(s,'2026-11-09',{done:true,effort:2,pain:false,note:''});
 assert.equal(s.days['2026-11-09'].training.profileId,old);
});
