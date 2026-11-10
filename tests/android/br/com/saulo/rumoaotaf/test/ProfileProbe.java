package br.com.saulo.rumoaotaf.test;
import android.app.Instrumentation;import android.os.Bundle;import org.json.*;import java.io.*;import java.util.*;
import br.com.saulo.rumoaotaf.*;
public class ProfileProbe extends Instrumentation {
 int n=0;ArrayList<String> failed=new ArrayList<>();void ok(boolean v,String s){n++;if(!v)failed.add(s);}
 public static JSONObject fixture()throws Exception{return new JSONObject("{name:'Pessoa de teste',age:30,height:175,weight:80,sex:'male',activity:'low',goal:'maintain',experience:'beginner',place:'home',health:'none',healthNote:'',diet:'common',meatLunch:false,swim:false,restrictions:[],restrictionNote:''}");}
 @Override public void onCreate(Bundle b){super.onCreate(b);start();}
 @Override public void onStart(){Bundle out=new Bundle();try{
  ByteArrayOutputStream bytes=new ByteArrayOutputStream();try(InputStream in=getTargetContext().getAssets().open("plan.json")){byte[] b=new byte[4096];int c;while((c=in.read(b))!=-1)bytes.write(b,0,c);}JSONObject catalog=new JSONObject(bytes.toString("UTF-8"));
  JSONObject fresh=Store.fresh();ok(fresh.optInt("version")==2&&!fresh.optBoolean("onboarded")&&fresh.optJSONObject("profile").length()==0,"instalacao nova nao contem perfil pessoal");
  if(!failed.isEmpty())throw new AssertionError(failed.get(0));
  for(int scenario=0;scenario<7;scenario++){
   JSONObject p=fixture();if(scenario==1){p.put("sex","female");p.put("weight",65);p.put("height",165);p.put("activity","moderate");p.put("goal","lose");}
   if(scenario==2)p.put("diet","vegan");if(scenario==3)p.put("restrictions",new JSONArray("['gluten','lactose']"));
   if(scenario==4){p.put("diet","vegan");p.put("restrictions",new JSONArray("['gluten','milk','egg','soy']"));}
   if(scenario==5)p.put("health","clinical");if(scenario==6)p.put("sex","unspecified");
   JSONObject plan=Recommendations.generate(catalog,p);ok(plan.has("dietAllowed"),"resultado informa modo de geracao");boolean allowed=plan.optBoolean("dietAllowed");
   if(scenario>=5)ok(!allowed,"perfil sem meta numerica "+scenario);else ok(allowed,"perfil padrao elegivel "+scenario);
   for(int m=0;m<4;m++){JSONObject meal=plan.getJSONArray("days").getJSONObject(0).getJSONArray("meals").getJSONObject(m);JSONArray opts=meal.getJSONArray("options");
    if(scenario<4)ok(opts.length()==3,"3 escolhas compativeis: "+scenario+"/"+m);if(!allowed)ok(opts.length()==0,"nao gera dieta restritiva em modo de orientacao");
    for(int j=0;j<opts.length();j++){JSONObject opt=opts.getJSONObject(j);JSONArray items=opt.getJSONArray("items");double[] base=totals(catalog,opts.getJSONObject(0)),val=totals(catalog,opt);
     ok(Math.abs(val[0]-base[0])<=base[0]*.10+1,"energia equivalente "+scenario+"/"+m);ok(Math.abs(val[1]-base[1])<=Math.max(3,base[1]*.20)+.01,"proteina equivalente "+scenario+"/"+m);
     for(int i=0;i<items.length();i++){JSONObject item=items.getJSONObject(i),f=catalog.getJSONObject("foods").getJSONObject(item.getString("food"));JSONArray tags=f.getJSONArray("allergens");
      ok(item.getDouble("g")>0&&item.getDouble("g")<=500,"porcao valida");if(scenario==2||scenario==4)ok(!f.optBoolean("meat")&&!f.optString("group").equals("lacteo")&&!tags.toString().contains("egg"),"ingrediente vegano");
      if(scenario==3||scenario==4)ok(!tags.toString().contains("gluten"),"exclui gluten");if(scenario==3)ok(!tags.toString().contains("lactose"),"exclui lactose");if(scenario==4)ok(!tags.toString().contains("milk")&&!tags.toString().contains("soy"),"exclui leite e soja");
     }
    }
   }
   if(scenario==5)ok(!plan.optBoolean("exerciseAllowed"),"condicao clinica sem prescricao de treino");
  }
  JSONObject v1=new JSONObject("{version:1,week:0,start:'2026-01-01',profile:{age:33,height:170,initialWeight:82,health:'Nota ficticia',preferences:'Preferencia ficticia',taf:''},days:{},weights:{'2026-01-02':81.5}}");
  JSONObject migrated=Store.decode(v1.toString());ok(migrated.optInt("version")==2&&!migrated.optBoolean("onboarded"),"v1 migra para revisao de cadastro");ok(migrated.getJSONObject("weights").getDouble("2026-01-02")==81.5,"migracao preserva peso");
  Store store=new Store(getTargetContext());String old=store.backup();store.restore(Store.fresh().toString());store.setProfile(fixture());JSONObject day=store.day(java.time.LocalDate.now().toString());day.put("water",500);day.getJSONArray("meals").put(0,true);store.saveDay(java.time.LocalDate.now().toString(),day);JSONObject changed=fixture();changed.put("diet","vegan");store.setProfile(changed);
  ok(store.state().getJSONObject("days").getJSONObject(java.time.LocalDate.now().toString()).getInt("water")==500,"editar perfil preserva hidratacao");ok(store.state().getJSONObject("archivedDays").length()==1,"editar perfil preserva marcacoes anteriores em arquivo");store.restore(old);
  out.putString("result",failed.isEmpty()?"PASS "+n+" profile checks":"FAIL "+failed.toString());finish(failed.isEmpty()?-1:0,out);
 }catch(Throwable e){out.putString("result","FAIL "+e);finish(0,out);}}
 static double[] totals(JSONObject c,JSONObject option)throws Exception{double[] n=new double[5];JSONArray items=option.getJSONArray("items");for(int i=0;i<items.length();i++){JSONObject x=items.getJSONObject(i);JSONArray v=c.getJSONObject("foods").getJSONObject(x.getString("food")).getJSONArray("values");for(int j=0;j<5;j++)n[j]+=v.getDouble(j)*x.getDouble("g")/100;}return n;}
}
