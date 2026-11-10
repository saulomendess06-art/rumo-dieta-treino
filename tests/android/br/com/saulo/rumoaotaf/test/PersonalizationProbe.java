package br.com.saulo.rumoaotaf.test;
import android.app.Instrumentation;import android.os.Bundle;import org.json.*;import java.io.*;import java.util.*;import br.com.saulo.rumoaotaf.*;
public class PersonalizationProbe extends Instrumentation {
 int checks=0;void ok(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 JSONObject catalog()throws Exception{ByteArrayOutputStream o=new ByteArrayOutputStream();try(InputStream in=getTargetContext().getAssets().open("plan.json")){byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1)o.write(b,0,n);}return new JSONObject(o.toString("UTF-8"));}
 String signature(JSONObject plan)throws Exception{String s="";for(int day=0;day<7;day++)for(int slot=0;slot<4;slot++){JSONArray a=plan.getJSONArray("days").getJSONObject(day).getJSONArray("meals").getJSONObject(slot).getJSONArray("options");if(a.length()>0)s+=a.getJSONObject(0).getString("title")+"|";}return s;}
 @Override public void onCreate(Bundle b){super.onCreate(b);start();}
 @Override public void onStart(){Bundle out=new Bundle();try{
 JSONObject c=catalog(),p=ProfileProbe.fixture();p.put("goal","lose").put("planVersion",2).put("frequency",3);
 JSONObject loss=Recommendations.generate(c,p);p.put("goal","strength");JSONObject strength=Recommendations.generate(c,p);ok(!signature(loss).equals(signature(strength)),"objetivo muda selecao das receitas");
 for(int slot=0;slot<4;slot++){Set<String> seen=new HashSet<>();for(int day=0;day<7;day++)seen.add(loss.getJSONArray("days").getJSONObject(day).getJSONArray("meals").getJSONObject(slot).getJSONArray("options").getJSONObject(0).getString("title"));ok(seen.size()>=3,"variedade semanal "+slot);}
 for(String goal:new String[]{"lose","maintain","strength","taf"})for(int frequency=1;frequency<=6;frequency++)for(boolean swim:new boolean[]{false,true}){
 p.put("goal",goal).put("frequency",frequency).put("swim",swim);JSONObject plan=Recommendations.generate(c,p);
 for(int week=0;week<4;week++){JSONArray days=plan.getJSONArray("workouts").getJSONArray(week);int n=0,last=-2;for(int day=0;day<7;day++){String kind=days.getJSONObject(day).getString("kind");if(!kind.equals("rest"))n++;if(kind.equals("strength")){ok(day-last>1,"forca nao consecutiva");last=day;}}ok(n==frequency,"frequencia "+frequency);if(swim)ok(days.getJSONObject(5).getString("kind").equals("swim"),"sabado aquático");}
 }
 p=ProfileProbe.fixture();p.put("frequency",0);try{Store.validateProfile(p);throw new AssertionError("aceitou frequencia 0");}catch(JSONException expected){checks++;}
 Store store=new Store(getTargetContext());String saved=store.backup();try{
 store.restore(Store.fresh().toString());store.setProfile(ProfileProbe.fixture());String date=java.time.LocalDate.now().toString(),oldId=store.state().getString("activeProfile");JSONObject day=store.day(date);day.put("water",500);store.saveDay(date,day);store.saveTraining(date,true,3,false,"registro anterior");
 JSONObject old=new JSONObject(store.backup());old.getJSONObject("profile").remove("planVersion");old.getJSONObject("profile").remove("frequency");old.getJSONObject("profiles").getJSONObject(oldId).remove("planVersion");old.getJSONObject("profiles").getJSONObject(oldId).remove("frequency");store.restore(old.toString());
 ok(!store.state().getString("activeProfile").equals(oldId),"migracao cria revisao nova");ok(store.state().getJSONObject("profiles").getJSONObject(oldId).getInt("planVersion")==1,"perfil antigo fica no motor 1");ok(store.state().getJSONObject("profile").getInt("frequency")==5,"migracao mantem frequencia padrao editavel");ok(store.day(date).getInt("water")==500&&store.day(date).getString("trainingProfileId").equals(oldId),"migracao preserva agua e contexto do treino");
 }finally{store.restore(saved);}
 out.putString("result","PASS "+checks+" personalization checks");finish(-1,out);
 }catch(Throwable e){out.putString("result","FAIL "+e);finish(0,out);}}
}
