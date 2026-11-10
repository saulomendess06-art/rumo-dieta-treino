package br.com.saulo.rumoaotaf.test;
import android.app.Instrumentation;
import android.os.Bundle;
import br.com.saulo.rumoaotaf.Store;
import org.json.*;
public class BackupProbe extends Instrumentation {
 int count=0;java.util.List<String> failures=new java.util.ArrayList<>();
 void ok(boolean v,String msg){count++;if(!v)failures.add(msg);}
 void rejects(JSONObject x,String msg)throws Exception{boolean bad=false;try{Store.validate(x);}catch(Exception e){bad=true;}ok(bad,msg);}
 JSONObject copy(JSONObject x)throws Exception{return new JSONObject(x.toString());}
 @Override public void onCreate(Bundle args){super.onCreate(args);start();}
 @Override public void onStart(){Bundle out=new Bundle();try{
  JSONObject base=Store.fresh();Store.validate(base);
  JSONObject bad=copy(base);bad.put("version",99);rejects(bad,"versao desconhecida");
  bad=copy(base);bad.put("week",4);rejects(bad,"semana fora de 0..3");
  JSONObject day=Store.emptyDay();day.put("water",250);day.getJSONArray("choices").put(1,1);day.getJSONArray("choices").put(2,2);day.getJSONArray("meals").put(0,true);
  JSONObject good=copy(base);good.getJSONObject("days").put("2026-11-10",day);Store.validate(good);ok(true,"dia valido");
  bad=copy(good);bad.getJSONObject("days").put("2026-02-30",day);rejects(bad,"data inexistente");
  bad=copy(good);bad.getJSONObject("days").getJSONObject("2026-11-10").getJSONArray("choices").put(1,3);rejects(bad,"alternativa invalida");
  bad=copy(good);bad.getJSONObject("days").getJSONObject("2026-11-10").put("water",-250);rejects(bad,"agua negativa");
  bad=copy(good);bad.getJSONObject("days").getJSONObject("2026-11-10").put("done","true");rejects(bad,"boolean com tipo errado");
  bad=copy(good);bad.getJSONObject("weights").put("2026-11-10",500);rejects(bad,"peso fora de faixa");
  bad=copy(good);bad.getJSONObject("profile").put("age",10);rejects(bad,"perfil de adulto");
  Store store=new Store(getTargetContext());String original=store.state().toString();
  try{store.restore("{");}catch(Exception expected){}ok(original.equals(store.state().toString()),"json invalido preserva estado");
  try{store.restore(bad.toString());}catch(Exception expected){}ok(original.equals(store.state().toString()),"backup invalido preserva estado");
  store.restore(good.toString());ok(store.state().getJSONObject("days").getJSONObject("2026-11-10").getInt("water")==250,"backup valido restaurado");
  Store reopened=new Store(getTargetContext());ok(reopened.state().getJSONObject("days").has("2026-11-10"),"persistencia apos reabrir");
  String nested="0";for(int i=0;i<80;i++)nested="["+nested+"]";
  String prefix="/*";for(int i=0;i<100;i++)prefix+="}";prefix+="*/";
  String attack=prefix+base.toString().replaceFirst("\\{","{\"extra\":"+nested+",");
  boolean rejected=false;try{Store.decode(attack);}catch(Exception expected){rejected=true;}ok(rejected,"comentarios nao burlam limite de profundidade");
  rejected=false;try{Store.decode("{\"extra\":"+nested+","+base.toString().substring(1));}catch(Exception expected){rejected=true;}ok(rejected,"profundidade excessiva rejeitada");
  JSONObject large=copy(base);String note="";for(int i=0;i<1000;i++)note+="á";
  for(int i=0;i<900;i++){JSONObject entry=copy(day);entry.put("note",note);large.getJSONObject("days").put(java.time.LocalDate.of(2000,1,1).plusDays(i).toString(),entry);}
  boolean roundtrip=false;try{store.restore(large.toString());roundtrip=new Store(getTargetContext()).state().getJSONObject("days").length()==900;}catch(Exception e){}
  ok(roundtrip,"historico valido maior que 1 MB preserva reabertura e restauracao");
  if(roundtrip){String exported=store.backup();ok(Store.decode(exported).getJSONObject("days").length()==900,"backup exportado e importavel em UTF-8");
   String before=store.state().toString();char[] huge=new char[Store.MAX_BACKUP_BYTES+1];java.util.Arrays.fill(huge,'x');store.state().put("extra",new String(huge));
   rejected=false;try{store.save();}catch(Exception expected){rejected=true;}ok(rejected,"limite de gravacao impede estado irrestauravel");ok(before.equals(store.state().toString()),"gravacao rejeitada restaura estado em memoria");
   ok(new Store(getTargetContext()).state().getJSONObject("days").length()==900,"gravacao rejeitada preserva estado em disco");
  }
  store.restore(original);out.putString("result",failures.isEmpty()?"PASS "+count+" checks":"FAIL "+failures.toString());finish(failures.isEmpty()?-1:0,out);
 }catch(Throwable e){out.putString("result","FAIL "+e.toString());finish(0,out);}}
}
