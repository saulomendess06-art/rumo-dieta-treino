package br.com.saulo.rumoaotaf;
import java.util.*;
public final class Planner {
 public static class Profile {
  public int age;public double height,weight;public String sex="unspecified",activity="low",goal="maintain",health="none",diet="common",restrictionNote="",healthNote="",experience="beginner",place="home";
  public boolean swim=false,meatLunch=false;public Set<String> restrictions=new HashSet<>();
 }
 public static class Decision {public double bmr,tdee,target;public boolean dietAllowed,exerciseAllowed,lowImpact;public String dietReason="",exerciseReason="";}
 public static Decision decide(Profile p){
  Decision d=new Decision();d.lowImpact=p.health.equals("joint")||p.experience.equals("beginner")||p.age>=65;
  boolean valid=p.age>=18&&p.age<=100&&Double.isFinite(p.weight)&&p.weight>=30&&p.weight<=350&&Double.isFinite(p.height)&&p.height>=130&&p.height<=220;
  d.exerciseAllowed=valid&&!p.health.equals("clinical")&&p.healthNote.trim().isEmpty();
  if(!d.exerciseAllowed)d.exerciseReason="Seu perfil precisa de orientação profissional antes de montar exercícios. O diário continua disponível.";
  if(!valid)d.dietReason="As sugestões automáticas são para adultos com dados dentro das faixas do cadastro.";
  else if(p.health.equals("clinical")||!p.healthNote.trim().isEmpty())d.dietReason="Uma condição ou informação de saúde exige avaliação individual. Não vamos montar uma dieta clínica automaticamente.";
  else if(!p.restrictionNote.trim().isEmpty())d.dietReason="Sua restrição personalizada precisa de revisão. As opções automáticas não devem ser consideradas compatíveis com ela.";
  else if(p.age>78)d.dietReason="Nesta faixa de idade, a estimativa alimentar precisa de avaliação individual.";
  else if(!p.sex.equals("male")&&!p.sex.equals("female"))d.dietReason="Sem informar o parâmetro de sexo, o app mantém o diário e as orientações gerais, sem inventar uma meta calórica.";
  else if(p.weight/Math.pow(p.height/100,2)<18.5)d.dietReason="O peso em relação à altura pede avaliação antes de definir metas alimentares. Não será aplicado déficit automático.";
  if(!d.dietReason.isEmpty())return d;
  d.bmr=10*p.weight+6.25*p.height-5*p.age+(p.sex.equals("male")?5:-161);
  double factor=p.activity.equals("high")?1.725:p.activity.equals("moderate")?1.55:p.activity.equals("light")?1.375:1.2;
  d.tdee=d.bmr*factor;double target=p.goal.equals("lose")?d.tdee*.85:p.goal.equals("strength")?d.tdee+Math.min(150,d.tdee*.05):d.tdee;
  double floor=p.sex.equals("male")?1600:1400;
  if(target<floor||target>3400){d.dietReason="A estimativa ficou fora da faixa de geração automática ("+(int)floor+"–3400 kcal para este perfil). Procure ajuste profissional; não limitaremos o cálculo silenciosamente.";return d;}
  d.target=Math.round(target/50)*50;d.dietAllowed=true;return d;
 }
 public static boolean allows(Profile p,String group,boolean meat,String... allergens){
  if((p.diet.equals("vegetarian")||p.diet.equals("vegan"))&&meat)return false;
  if(p.diet.equals("vegan")&&group.equals("lacteo"))return false;
  for(String tag:allergens){if(p.restrictions.contains(tag))return false;if(p.diet.equals("vegan")&&(tag.equals("milk")||tag.equals("egg")))return false;}
  return true;
 }
}
