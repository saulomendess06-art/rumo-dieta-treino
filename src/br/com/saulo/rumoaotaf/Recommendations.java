package br.com.saulo.rumoaotaf;
import org.json.*;import java.util.*;
public final class Recommendations {
 public static Planner.Profile profile(JSONObject j){
  Planner.Profile p=new Planner.Profile();p.frequency=j.optInt("frequency",5);p.age=j.optInt("age");p.height=j.optDouble("height");p.weight=j.optDouble("weight");
  p.sex=j.optString("sex");p.activity=j.optString("activity");p.goal=j.optString("goal");p.health=j.optString("health");p.diet=j.optString("diet");p.experience=j.optString("experience");p.place=j.optString("place");p.swim=j.optBoolean("swim");p.meatLunch=j.optBoolean("meatLunch");p.healthNote=j.optString("healthNote");p.restrictionNote=j.optString("restrictionNote");JSONArray a=j.optJSONArray("restrictions");if(a!=null)for(int i=0;i<a.length();i++)p.restrictions.add(a.optString(i));return p;
 }
 public static JSONObject generate(JSONObject catalog,JSONObject json)throws JSONException{return json.optInt("planVersion",2)==1?LegacyRecommendations.generate(catalog,json):Personalization.generate(catalog,json);}
 public static double[] totals(JSONObject foods,JSONObject option)throws JSONException{return LegacyRecommendations.totals(foods,option);}
 public static String goalLabel(String code){return code.equals("lose")?"Perder peso":code.equals("strength")?"Ganhar força e massa":code.equals("taf")?"Preparação inicial para TAF":"Manter físico e condicionamento";}
 public static JSONObject recordedTraining(JSONObject catalog,JSONObject legacy,JSONObject state,JSONObject day,int dayIndex)throws JSONException{String id=day.getString("trainingProfileId");JSONObject original=id.equals("legacy")?legacy:generate(catalog,state.getJSONObject("profiles").getJSONObject(id));return original.getJSONArray("workouts").getJSONArray(day.getInt("week")).getJSONObject(dayIndex);}
}
