package br.com.saulo.rumoaotaf;
import org.json.*;import java.util.*;

public final class Personalization {
 private static final Locale PT=new Locale("pt","BR");
 private static class Candidate {JSONObject option;double[] n;double score;int index;}
 private static List<Candidate> candidates(JSONObject c,Planner.Profile p,int slot,double kcal)throws JSONException{
  List<Candidate> out=new ArrayList<>();JSONArray recipes=c.getJSONArray("recipes").getJSONArray(slot);JSONObject foods=c.getJSONObject("foods");
  double protein=p.goal.equals("strength")?.25:p.goal.equals("lose")?.23:p.goal.equals("taf")?.20:.18,carb=p.goal.equals("strength")?.45:p.goal.equals("lose")?.48:p.goal.equals("taf")?.55:.50;
  for(int i=0;i<recipes.length();i++){
   JSONObject original=recipes.getJSONObject(i);JSONArray items=original.getJSONArray("items");boolean allowed=true;
   for(int j=0;j<items.length();j++){JSONObject f=foods.getJSONObject(items.getJSONObject(j).getString("food"));JSONArray tags=f.getJSONArray("allergens");String[] allergens=new String[tags.length()];for(int k=0;k<tags.length();k++)allergens[k]=tags.getString(k);if(!Planner.allows(p,f.getString("group"),f.getBoolean("meat"),allergens)||(p.meatLunch&&slot!=1&&f.getBoolean("meat"))){allowed=false;break;}}
   if(!allowed)continue;double factor=kcal/Recommendations.totals(foods,original)[0];JSONObject option=new JSONObject(original.toString());items=option.getJSONArray("items");boolean sensible=true;
   for(int j=0;j<items.length();j++){JSONObject item=items.getJSONObject(j);String key=item.getString("food");JSONObject f=foods.getJSONObject(key);double step=key.equals("egg")?50:key.equals("bread")?25:key.equals("oil")?.5:5;double g=Math.max(step,Math.round(item.getDouble("g")*factor/step)*step);if(g>500)sensible=false;item.put("g",g).put("portion",String.format(PT,"≈ %.1f %s",g/f.getDouble("unitGrams"),f.getString("unitLabel")));}
   double[] n=Recommendations.totals(foods,option);if(!sensible||Math.abs(n[0]-kcal)>kcal*.10)continue;
   Candidate v=new Candidate();v.option=option;v.n=n;v.index=i;v.score=Math.abs(n[1]*4/n[0]-protein)*50+Math.abs(n[2]*4/n[0]-carb)*15+Math.max(0,14-n[4]*1000/n[0])*.3+Math.abs(n[0]-kcal)/kcal;out.add(v);
  }return out;
 }
 private static double diversity(Candidate o,List<Candidate> chosen)throws JSONException{
  Candidate first=chosen.get(0);double score=Math.abs(o.n[0]-first.n[0])/first.n[0]+Math.abs(o.n[1]-first.n[1])/Math.max(3,first.n[1]);
  JSONArray items=o.option.getJSONArray("items");for(Candidate prior:chosen){Set<String> keys=new HashSet<>();JSONArray prev=prior.option.getJSONArray("items");for(int i=0;i<prev.length();i++)keys.add(prev.getJSONObject(i).getString("food"));int common=0;for(int i=0;i<items.length();i++)if(keys.contains(items.getJSONObject(i).getString("food")))common++;score+=common/(double)items.length()*.35;}return score;
 }
 private static JSONArray trio(List<Candidate> all,Map<Integer,Integer> used)throws JSONException{
  List<Candidate> ranked=new ArrayList<>(all);Collections.sort(ranked,(a,b)->{int n=Double.compare(a.score+used.getOrDefault(a.index,0)*20,b.score+used.getOrDefault(b.index,0)*20);return n==0?Integer.compare(a.index,b.index):n;});
  for(Candidate first:ranked){List<Candidate> compatible=new ArrayList<>();for(Candidate o:all)if(o!=first&&Math.abs(o.n[0]-first.n[0])<=first.n[0]*.1&&Math.abs(o.n[1]-first.n[1])<=Math.max(3,first.n[1]*.2))compatible.add(o);if(compatible.size()<2)continue;
   List<Candidate> chosen=new ArrayList<>();chosen.add(first);while(chosen.size()<3){Candidate best=null;double score=Double.POSITIVE_INFINITY;for(Candidate o:compatible){double v=diversity(o,chosen);if(best==null||v<score||v==score&&(o.score<best.score||o.score==best.score&&o.index<best.index)){best=o;score=v;}}chosen.add(best);compatible.remove(best);}
   used.put(first.index,used.getOrDefault(first.index,0)+1);JSONArray out=new JSONArray();for(Candidate o:chosen)out.put(new JSONObject(o.option.toString()));return out;
  }return new JSONArray();
 }
 public static JSONObject generate(JSONObject c,JSONObject json)throws JSONException{
  Planner.Profile p=Recommendations.profile(json);Planner.Decision d=Planner.decide(p);JSONObject result=new JSONObject(c.toString());
  double[] fractions=p.goal.equals("strength")?new double[]{.25,.30,.20,.25}:p.goal.equals("taf")?new double[]{.25,.35,.20,.20}:new double[]{.25,.35,.15,.25};
  List<List<Candidate>> options=new ArrayList<>();List<Map<Integer,Integer>> used=new ArrayList<>();for(int slot=0;slot<4;slot++){options.add(d.dietAllowed?candidates(c,p,slot,d.target*fractions[slot]):new ArrayList<>());used.add(new HashMap<>());}
  JSONArray days=new JSONArray();for(int day=0;day<7;day++){JSONArray meals=new JSONArray();for(int slot=0;slot<4;slot++){JSONObject original=c.getJSONArray("days").getJSONObject(0).getJSONArray("meals").getJSONObject(slot);JSONObject m=new JSONObject().put("label",original.getString("label")).put("time",original.getString("time"));m.put("options",trio(options.get(slot),used.get(slot))).put("reason",d.dietAllowed?"Não encontramos três alternativas equivalentes compatíveis. Esta refeição precisa de ajuste profissional.":d.dietReason);meals.put(m);}days.put(new JSONObject().put("name",c.getJSONArray("days").getJSONObject(day).getString("name")).put("meals",meals));}
  JSONArray diet=new JSONArray().put("Objetivo: "+Recommendations.goalLabel(p.goal)+".").put(d.dietAllowed?"Referência de "+Math.round(d.target)+" kcal/dia calculada com peso, altura, idade, parâmetro de sexo e atividade informada.":d.dietReason);
  diet.put(p.goal.equals("strength")?"Seleção considera proteína para acompanhar a rotina de força e distribui mais energia no lanche.":p.goal.equals("lose")?"Seleção considera proteína, fibras e a redução energética estimada para organizar a perda de peso.":p.goal.equals("taf")?"Distribuição dá espaço a carboidratos para a rotina de condicionamento, sem déficit automático.":"Seleção busca equilíbrio entre energia, proteína e fibras para a manutenção.");
  diet.put("Preparações principais alternam nos sete dias dentro das opções compatíveis. Alternativas continuam próximas em energia e proteína.");diet.put(p.diet.equals("vegan")?"Sem carnes, ovos ou laticínios.":p.diet.equals("vegetarian")?"Sem carnes; ovos e laticínios podem entrar quando permitidos.":p.meatLunch?"Carne somente no almoço, conforme sua preferência.":"Padrão comum, respeitando as restrições selecionadas.");if(!p.restrictions.isEmpty())diet.put("Alérgenos selecionados são excluídos antes da escolha. Confira rótulos e contaminação cruzada.");
  result.put("days",days).put("workouts",PersonalizedTraining.generate(c,p,d)).put("adaptation",new JSONObject().put("diet",diet).put("training",PersonalizedTraining.reasons(p))).put("planVersion",2).put("dietAllowed",d.dietAllowed).put("exerciseAllowed",d.exerciseAllowed).put("dietReason",d.dietReason).put("exerciseReason",d.exerciseReason).put("targetKcal",d.target).put("restingEstimate",d.bmr).put("maintenanceEstimate",d.tdee);
  result.put("nutritionNote",c.optString("nutritionNote")+" Confira rótulos e contaminação cruzada. As regras de seleção são educativas e não garantem adequação clínica individual.");return result;
 }
}
