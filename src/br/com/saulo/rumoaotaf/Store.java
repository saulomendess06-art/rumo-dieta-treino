package br.com.saulo.rumoaotaf;
import android.content.*;import org.json.*;import java.time.LocalDate;import java.util.*;import java.nio.charset.StandardCharsets;
public class Store {
 public static final int MAX_BACKUP_BYTES=8*1024*1024;
 private final SharedPreferences preferences;private JSONObject data;private String committed;public boolean recovered=false;
 public Store(Context c){preferences=c.getSharedPreferences("rumo-local",Context.MODE_PRIVATE);String raw=preferences.getString("state",null);if(raw==null)data=fresh();else try{data=decode(raw);}catch(Exception e){preferences.edit().putString("recovery",raw).commit();data=fresh();recovered=true;}committed=data.toString();}
 public static JSONObject fresh(){try{return new JSONObject().put("version",2).put("onboarded",false).put("legacy",false).put("profile",new JSONObject()).put("profiles",new JSONObject()).put("activeProfile","").put("archivedDays",new JSONObject()).put("week",0).put("start",LocalDate.now().toString()).put("days",new JSONObject()).put("weights",new JSONObject());}catch(JSONException e){throw new IllegalStateException(e);}}
 public static JSONObject emptyDay(){try{return new JSONObject("{water:0,choices:[0,0,0,0],meals:[false,false,false,false],done:false,effort:0,pain:false,note:'',trainingRecorded:false,profileId:'',trainingProfileId:'',week:0}");}catch(JSONException e){throw new IllegalStateException(e);}}
 public static boolean hasTraining(JSONObject d){return d.optBoolean("trainingRecorded")||d.optBoolean("done")||d.optInt("effort")>0||d.optBoolean("pain")||!d.optString("note").trim().isEmpty();}
 public JSONObject state(){return data;}
 public JSONObject day(String date){try{JSONObject old=data.getJSONObject("days").optJSONObject(date);if(old!=null)return new JSONObject(old.toString());JSONObject d=emptyDay();d.put("profileId",data.getString("activeProfile")).put("trainingProfileId",data.getString("activeProfile")).put("week",data.getInt("week"));return d;}catch(JSONException e){throw new IllegalStateException(e);}}
 public void saveDay(String date,JSONObject day){try{JSONObject next=new JSONObject(data.toString());next.getJSONObject("days").put(date,day);commit(next);}catch(JSONException e){throw new IllegalStateException(e);}}
 public void saveTraining(String date,boolean done,int effort,boolean pain,String note){try{JSONObject d=day(date);if(!hasTraining(d))d.put("trainingProfileId",d.getString("profileId")).put("week",data.getInt("week"));d.put("trainingRecorded",true).put("done",done).put("effort",effort).put("pain",pain).put("note",note);saveDay(date,d);}catch(JSONException e){throw new IllegalStateException(e);}}
 public void setProfile(JSONObject profile){try{
  validateProfile(profile);JSONObject next=new JSONObject(data.toString());JSONObject profiles=next.getJSONObject("profiles");String id="p"+System.currentTimeMillis()+"_"+profiles.length();profiles.put(id,new JSONObject(profile.toString()));next.put("profile",new JSONObject(profile.toString())).put("activeProfile",id).put("onboarded",true);
  String today=LocalDate.now().toString();JSONObject current=next.getJSONObject("days").optJSONObject(today);
  if(current!=null){String previous=current.optString("profileId");if(!previous.isEmpty())next.getJSONObject("archivedDays").put(today+"@"+previous,new JSONObject(current.toString()));current.put("profileId",id).put("choices",new JSONArray("[0,0,0,0]")).put("meals",new JSONArray("[false,false,false,false]"));}
  commit(next);
 }catch(JSONException e){throw new IllegalArgumentException(e.getMessage());}}
 private void commit(JSONObject candidate)throws JSONException{validate(candidate);String raw=encode(candidate);if(!preferences.edit().putString("state",raw).commit())throw new IllegalStateException("Não foi possível salvar os registros.");data=candidate;committed=raw;}
 public void save(){try{commit(data);}catch(Exception e){try{data=new JSONObject(committed);}catch(JSONException impossible){throw new IllegalStateException(impossible);}throw new IllegalStateException(e.getMessage());}}
 public String backup(){try{validate(data);return encode(data);}catch(JSONException e){throw new IllegalStateException(e);}}
 public void restore(String raw){try{commit(decode(raw));}catch(JSONException e){throw new IllegalArgumentException("Backup inválido: "+e.getMessage());}}
 private static String encode(JSONObject x)throws JSONException{String raw=x.toString();if(raw.getBytes(StandardCharsets.UTF_8).length>MAX_BACKUP_BYTES)throw new JSONException("Limite de 8 MB atingido. Reduza anotações antigas antes de salvar mais dados.");return raw;}
 public static JSONObject decode(String raw)throws JSONException{
  if(raw==null||raw.length()>MAX_BACKUP_BYTES||raw.getBytes(StandardCharsets.UTF_8).length>MAX_BACKUP_BYTES)throw new JSONException("Limite de backup: 8 MB.");
  int depth=0;char[] expected=new char[32];boolean quoted=false,escape=false;
  for(int i=0;i<raw.length();i++){char c=raw.charAt(i);if(quoted){if(escape)escape=false;else if(c=='\\')escape=true;else if(c=='\"')quoted=false;}
   else if(c=='\"')quoted=true;else if(c=='/'||c=='\'')throw new JSONException("Use JSON sem comentários e com aspas duplas.");else if(c=='{'||c=='['){if(depth==32)throw new JSONException("Estrutura muito profunda.");expected[depth++]=c=='{'?'}':']';}else if(c=='}'||c==']'){if(depth==0||expected[depth-1]!=c)throw new JSONException("Estrutura inválida.");depth--;}}
  if(quoted||depth!=0)throw new JSONException("JSON incompleto.");JSONTokener tokener=new JSONTokener(raw);JSONObject x=new JSONObject(tokener);if(tokener.nextClean()!=0)throw new JSONException("Conteúdo extra após o backup.");if(x.optInt("version")==1)x=migrate(x);validate(x);return x;
 }
 private static JSONObject migrate(JSONObject old)throws JSONException{
  integer(old.get("version"),1,1);JSONObject op=old.getJSONObject("profile");number(op.get("initialWeight"),30,350);number(op.get("height"),130,220);integer(op.get("age"),18,100);
  JSONObject p=new JSONObject().put("name","").put("age",op.get("age")).put("height",op.get("height")).put("weight",op.get("initialWeight")).put("sex","unspecified").put("activity","low").put("goal","taf").put("experience","beginner").put("place","home").put("health","clinical").put("healthNote",op.optString("health")).put("diet","common").put("meatLunch",true).put("swim",false).put("restrictions",new JSONArray()).put("restrictionNote",op.optString("preferences"));
  JSONObject x=fresh();x.put("legacy",true).put("profile",p).put("activeProfile","legacy").put("week",old.get("week")).put("start",old.get("start")).put("weights",old.getJSONObject("weights"));JSONObject days=old.getJSONObject("days");
  for(Iterator<String> i=days.keys();i.hasNext();){JSONObject d=days.getJSONObject(i.next());d.put("trainingRecorded",hasTraining(d)).put("profileId","legacy").put("trainingProfileId","legacy").put("week",old.getInt("week"));}x.put("days",days);return x;
 }
 public static void validateProfile(JSONObject p)throws JSONException{
  text(p.get("name"),50);integer(p.get("age"),18,100);number(p.get("height"),130,220);number(p.get("weight"),30,350);
  code(p,"sex","male","female","unspecified");code(p,"activity","low","light","moderate","high");code(p,"goal","lose","maintain","strength","taf");code(p,"experience","beginner","regular");code(p,"place","home","gym");code(p,"health","none","joint","clinical");code(p,"diet","common","vegetarian","vegan");text(p.get("healthNote"),2000);text(p.get("restrictionNote"),2000);bool(p.get("swim"));bool(p.get("meatLunch"));JSONArray a=p.getJSONArray("restrictions");if(a.length()>8)throw new JSONException("Restrições demais.");HashSet<String> seen=new HashSet<>();for(int i=0;i<a.length();i++){String s=a.getString(i);if(!Arrays.asList("gluten","lactose","milk","egg","soy","peanut","nuts","fish").contains(s)||!seen.add(s))throw new JSONException("Restrição inválida.");}
 }
 public static void validate(JSONObject x)throws JSONException{
  integer(x.get("version"),2,2);integer(x.get("week"),0,3);bool(x.get("onboarded"));bool(x.get("legacy"));if(!Core.checkDate(x.getString("start")))throw new JSONException("Data inicial inválida.");JSONObject p=x.getJSONObject("profile"),profiles=x.getJSONObject("profiles");if(p.length()>0)validateProfile(p);if(profiles.length()>1000)throw new JSONException("Perfis anteriores demais.");
  for(Iterator<String> i=profiles.keys();i.hasNext();){String id=i.next();if(!id.matches("p[0-9]+_[0-9]+"))throw new JSONException("Identificador de perfil inválido.");validateProfile(profiles.getJSONObject(id));}
  String active=x.getString("activeProfile");if(x.getBoolean("onboarded")){if(!profiles.has(active)||!profiles.getJSONObject(active).toString().equals(p.toString()))throw new JSONException("Perfil ativo incoerente.");}else if(!active.isEmpty()&&!(active.equals("legacy")&&x.getBoolean("legacy")))throw new JSONException("Cadastro incompleto inválido.");
  JSONObject days=x.getJSONObject("days"),archived=x.getJSONObject("archivedDays"),weights=x.getJSONObject("weights");if(days.length()+archived.length()>20000||weights.length()>20000)throw new JSONException("Registros demais.");
  for(Iterator<String> i=days.keys();i.hasNext();){String date=i.next();if(!Core.checkDate(date))throw new JSONException("Data inválida.");validateDay(days.getJSONObject(date),x);}
  for(Iterator<String> i=archived.keys();i.hasNext();){String key=i.next();int split=key.indexOf('@');if(split!=10||!Core.checkDate(key.substring(0,10)))throw new JSONException("Data de versão anterior inválida.");validateDay(archived.getJSONObject(key),x);}
  for(Iterator<String> i=weights.keys();i.hasNext();){String date=i.next();if(!Core.checkDate(date))throw new JSONException("Data de peso inválida.");number(weights.get(date),30,350);}
 }
 private static void validateDay(JSONObject d,JSONObject root)throws JSONException{
  if(d.has("trainingRecorded"))bool(d.get("trainingRecorded"));
  integer(d.get("water"),0,20000);integer(d.get("week"),0,3);integer(d.get("effort"),0,10);bool(d.get("done"));bool(d.get("pain"));text(d.get("note"),1000);for(String key:new String[]{"profileId","trainingProfileId"}){String id=d.getString(key);if(!root.getJSONObject("profiles").has(id)&&!(id.equals("legacy")&&root.getBoolean("legacy"))&&!(id.isEmpty()&&!root.getBoolean("onboarded")))throw new JSONException("Perfil do registro não encontrado.");}
  JSONArray a=d.getJSONArray("choices"),b=d.getJSONArray("meals");if(a.length()!=4||b.length()!=4)throw new JSONException("Refeições inválidas.");for(int i=0;i<4;i++){integer(a.get(i),0,2);bool(b.get(i));}
 }
 private static void code(JSONObject p,String key,String... codes)throws JSONException{Object v=p.get(key);if(!(v instanceof String)||!Arrays.asList(codes).contains(v))throw new JSONException("Confira o campo "+key+".");}
 private static double number(Object n,double min,double max)throws JSONException{if(!(n instanceof Number))throw new JSONException("Número esperado.");double v=((Number)n).doubleValue();if(!Double.isFinite(v)||v<min||v>max)throw new JSONException("Valor fora da faixa permitida.");return v;}
 private static void integer(Object n,int min,int max)throws JSONException{double v=number(n,min,max);if(v!=Math.rint(v))throw new JSONException("Número inteiro esperado.");}
 private static void text(Object v,int max)throws JSONException{if(!(v instanceof String)||((String)v).length()>max)throw new JSONException("Texto inválido.");}
 private static void bool(Object v)throws JSONException{if(!(v instanceof Boolean))throw new JSONException("Valor lógico esperado.");}
}
