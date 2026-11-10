package br.com.saulo.rumoaotaf;
import org.json.*;import java.util.*;
public final class LegacyRecommendations {
 private static final Locale PT=new Locale("pt","BR");
 public static Planner.Profile profile(JSONObject j){
  Planner.Profile p=new Planner.Profile();p.age=j.optInt("age");p.height=j.optDouble("height");p.weight=j.optDouble("weight");
  p.sex=j.optString("sex");p.activity=j.optString("activity");p.goal=j.optString("goal");p.health=j.optString("health");p.diet=j.optString("diet");p.experience=j.optString("experience");p.place=j.optString("place");p.swim=j.optBoolean("swim");p.meatLunch=j.optBoolean("meatLunch");p.healthNote=j.optString("healthNote");p.restrictionNote=j.optString("restrictionNote");JSONArray a=j.optJSONArray("restrictions");if(a!=null)for(int i=0;i<a.length();i++)p.restrictions.add(a.optString(i));return p;
 }
 public static JSONObject generate(JSONObject catalog,JSONObject json)throws JSONException{
  Planner.Profile p=profile(json);Planner.Decision d=Planner.decide(p);JSONObject result=new JSONObject(catalog.toString());JSONArray days=new JSONArray();double[] fractions={.25,.35,.15,.25};
  for(int day=0;day<7;day++){JSONObject dayObj=new JSONObject();dayObj.put("name",catalog.getJSONArray("days").getJSONObject(day).getString("name"));JSONArray meals=new JSONArray();
   for(int slot=0;slot<4;slot++){JSONObject m=new JSONObject();JSONObject original=catalog.getJSONArray("days").getJSONObject(0).getJSONArray("meals").getJSONObject(slot);m.put("label",original.getString("label")).put("time",original.getString("time"));
    JSONArray options=d.dietAllowed?choices(catalog,p,slot,d.target*fractions[slot]):new JSONArray();m.put("options",options).put("reason",d.dietAllowed?"Não encontramos três alternativas equivalentes para esta combinação. Esta refeição precisa de ajuste profissional.":d.dietReason);meals.put(m);
   }dayObj.put("meals",meals);days.put(dayObj);
  }
  result.put("days",days).put("workouts",training(catalog,p,d)).put("dietAllowed",d.dietAllowed).put("exerciseAllowed",d.exerciseAllowed).put("dietReason",d.dietReason).put("exerciseReason",d.exerciseReason).put("targetKcal",d.target).put("restingEstimate",d.bmr).put("maintenanceEstimate",d.tdee);
  result.put("nutritionNote",catalog.optString("nutritionNote")+" As porções são ajustadas por estimativa energética. Confira rótulos e contaminação cruzada; o filtro não certifica ausência de alérgenos. Bebida de soja e outros produtos variam por marca.");return result;
 }
 private static JSONArray choices(JSONObject catalog,Planner.Profile p,int slot,double kcal)throws JSONException{
  ArrayList<JSONObject> options=new ArrayList<>();JSONArray recipes=catalog.getJSONArray("recipes").getJSONArray(slot);JSONObject foods=catalog.getJSONObject("foods");
  for(int i=0;i<recipes.length();i++){JSONObject recipe=recipes.getJSONObject(i);boolean allowed=true;JSONArray items=recipe.getJSONArray("items");
   for(int j=0;j<items.length();j++){JSONObject f=foods.getJSONObject(items.getJSONObject(j).getString("food"));JSONArray tags=f.getJSONArray("allergens");String[] allergens=new String[tags.length()];for(int k=0;k<tags.length();k++)allergens[k]=tags.getString(k);
    if(!Planner.allows(p,f.getString("group"),f.getBoolean("meat"),allergens)||(p.meatLunch&&slot!=1&&f.getBoolean("meat"))){allowed=false;break;}}
   if(!allowed)continue;double energy=totals(foods,recipe)[0];double factor=kcal/energy;JSONObject scaled=new JSONObject(recipe.toString());JSONArray scaledItems=scaled.getJSONArray("items");boolean sensible=true;
   for(int j=0;j<scaledItems.length();j++){JSONObject item=scaledItems.getJSONObject(j);String key=item.getString("food");JSONObject f=foods.getJSONObject(key);double step=key.equals("egg")?50:key.equals("bread")?25:key.equals("oil")?.5:5;double g=Math.max(step,Math.round(item.getDouble("g")*factor/step)*step);if(g>500)sensible=false;item.put("g",g);item.put("portion",String.format(PT,"≈ %.1f %s",g/f.getDouble("unitGrams"),f.getString("unitLabel")));}
   if(sensible&&Math.abs(totals(foods,scaled)[0]-kcal)<=kcal*.10)options.add(scaled);
  }
  for(int i=0;i<options.size();i++){JSONArray chosen=new JSONArray();JSONObject first=options.get(i);double[] base=totals(foods,first);chosen.put(first);
   for(int j=0;j<options.size()&&chosen.length()<3;j++){if(j==i)continue;double[] v=totals(foods,options.get(j));if(Math.abs(v[0]-base[0])<=base[0]*.10&&Math.abs(v[1]-base[1])<=Math.max(3,base[1]*.20))chosen.put(options.get(j));}
   if(chosen.length()==3)return chosen;
  }return new JSONArray();
 }
 public static double[] totals(JSONObject foods,JSONObject option)throws JSONException{double[] n=new double[5];JSONArray items=option.getJSONArray("items");for(int i=0;i<items.length();i++){JSONObject item=items.getJSONObject(i);JSONArray values=foods.getJSONObject(item.getString("food")).getJSONArray("values");for(int j=0;j<5;j++)n[j]+=values.getDouble(j)*item.getDouble("g")/100;}return n;}
 private static JSONObject step(String name,String dose,String detail)throws JSONException{return new JSONObject().put("name",name).put("dose",dose).put("detail",detail);}
 private static JSONArray training(JSONObject catalog,Planner.Profile p,Planner.Decision d)throws JSONException{
  JSONArray weeks=new JSONArray();boolean regular=p.experience.equals("regular")&&!d.lowImpact;int base=regular?(p.activity.equals("high")?35:30):15;
  for(int w=0;w<4;w++){JSONArray days=new JSONArray();for(int day=0;day<7;day++){
   if(day==2||day==6){days.put(new JSONObject(catalog.getJSONArray("workouts").getJSONArray(w).getJSONObject(2).toString()));continue;}
   JSONObject t=new JSONObject();JSONArray steps=new JSONArray();String kind=day==1||day==4||p.goal.equals("strength")&&(day==0||day==3||day==5)?"strength":"walk";
   if(p.goal.equals("strength")&&(day==1||day==4))kind="walk";if(day==5&&p.swim)kind="swim";
   int minutes=0;
   if(!d.exerciseAllowed){kind="guide";t.put("title","Plano profissional antes de treinar");steps.put(step("Converse com um profissional","Sem treino automático",d.exerciseReason+" Em caso de sintomas importantes, procure atendimento."));}
   else if(kind.equals("walk")){minutes=base+w*3;t.put("title",d.lowImpact?"Caminhada leve, sem impacto":"Caminhada de condicionamento");steps.put(step("Comece devagar","5 min","Terreno plano e regular. Sem mudanças rápidas de direção."));steps.put(step("Mantenha um ritmo confortável",(minutes-10)+" min","Você deve conseguir conversar. Reduza o tempo ou faça pausas se necessário. Não é uma sessão de corrida."));steps.put(step("Desacelere","5 min","Observe recuperação e sintomas no dia seguinte. Não avance a semana se não estiver bem."));}
   else if(kind.equals("swim")){minutes=regular?30:20;t.put("title","Natação leve e técnica");steps.put(step("Adapte-se à água","5 min","Piscina com supervisão no local. A inclusão considera que você informou saber nadar e ter acesso à piscina."));steps.put(step("Trechos curtos com descanso",(minutes-10)+" min com pausas","Nade em ritmo confortável. Descanse 30–60 s ou mais. Ajuste pernadas e viradas se houver sintomas articulares."));steps.put(step("Finalize devagar","5 min","Sem apneia ou hiperventilação. Nunca treine sozinho."));}
   else {minutes=regular?30:20;int sets=regular?2:w>=2?2:1;String dose=sets+" série(s) de "+(regular?8:6)+" repetições";t.put("title",d.lowImpact?"Força leve e controle":"Força para o corpo todo");steps.put(step("Aqueça","5 min","Movimentos confortáveis de ombros, tornozelos e caminhada leve, se tolerada."));
    if(p.place.equals("gym")&&!d.lowImpact){steps.put(step("Remada na máquina",dose,"Peça ajuste do equipamento ao instrutor. Carga confortável, sem balanço ou falha muscular."));steps.put(step("Supino na máquina",dose,"Movimento controlado, respirando normalmente. Pare antes da exaustão."));steps.put(step("Leg press leve",dose,"Com orientação de ajuste, amplitude confortável e sem travar os joelhos. Pare se houver dor."));}
    else {steps.put(step("Flexão na parede",dose,"Mãos na altura do peito. Aproxime os pés da parede para facilitar e mantenha o tronco alinhado."));steps.put(step("Retração das escápulas",dose,"Sentado, aproxime suavemente as escápulas por 2 segundos, sem prender a respiração."));if(!p.health.equals("joint"))steps.put(step("Sentar e levantar",dose,"Cadeira firme e alta encostada na parede. Apoie as mãos se precisar. Só faça sem dor ou instabilidade."));}
    steps.put(step("Ativação do abdômen","5 vezes de 5 segundos","Sentado em cadeira firme, ative suavemente o abdômen enquanto respira. Relaxe entre as repetições."));steps.put(step("Pausas e finalização","60–90 s entre séries; 3 min ao final","Descanse mais se precisar. Técnica antes de carga. Interrompa com dor, inchaço ou instabilidade."));
   }
   t.put("kind",kind).put("minutes",minutes).put("effort",regular?"Confortável · 3–5 de 10":"Leve · 2–3 de 10").put("steps",steps);days.put(t);
  }weeks.put(days);}return weeks;
 }
 public static String goalLabel(String code){return code.equals("lose")?"Perder peso":code.equals("strength")?"Ganhar força e massa":code.equals("taf")?"Preparação inicial para TAF":"Manter físico e condicionamento";}
 public static JSONObject recordedTraining(JSONObject catalog,JSONObject legacy,JSONObject state,JSONObject day,int dayIndex)throws JSONException{String id=day.getString("trainingProfileId");JSONObject original=id.equals("legacy")?legacy:generate(catalog,state.getJSONObject("profiles").getJSONObject(id));return original.getJSONArray("workouts").getJSONArray(day.getInt("week")).getJSONObject(dayIndex);}
}
