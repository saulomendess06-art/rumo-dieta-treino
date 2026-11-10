import br.com.saulo.rumoaotaf.Planner;
public class PlannerTest {
 static int n;static void ok(boolean b,String m){n++;if(!b)throw new AssertionError(m);}
 static Planner.Profile profile(){Planner.Profile p=new Planner.Profile();p.age=30;p.height=175;p.weight=80;p.sex="male";p.activity="low";p.goal="lose";return p;}
 public static void main(String[] args){
  Planner.Profile p=profile();Planner.Decision d=Planner.decide(p);ok(Math.abs(d.bmr-1748.75)<.001,"Mifflin masculino");ok(d.target==1800,"deficit moderado arredondado");ok(d.dietAllowed,"adulto saudavel elegivel");
  p.sex="female";p.goal="maintain";d=Planner.decide(p);ok(Math.abs(d.bmr-1582.75)<.001,"Mifflin feminino");ok(d.target==1900,"manutencao feminina");
  p.sex="unspecified";ok(!Planner.decide(p).dietAllowed,"sem sexo nao inventa parametro");
  p=profile();p.age=17;ok(!Planner.decide(p).dietAllowed,"menor sem meta calorica");
  p=profile();p.health="clinical";ok(!Planner.decide(p).dietAllowed&&!Planner.decide(p).exerciseAllowed,"condicao clinica nao recebe prescricao");
  p=profile();p.health="joint";ok(Planner.decide(p).lowImpact&&Planner.decide(p).exerciseAllowed,"historico articular estavel recebe baixo impacto");
  p=profile();p.restrictionNote="alergia especial";ok(!Planner.decide(p).dietAllowed,"restricao livre nao e ignorada");
  p=profile();p.healthNote="condicao nao listada";ok(!Planner.decide(p).dietAllowed&&!Planner.decide(p).exerciseAllowed,"saude livre requer revisao");
  p=profile();p.weight=45;ok(!Planner.decide(p).dietAllowed,"baixo peso nao recebe deficit");
  p=profile();p.goal="strength";p.activity="moderate";ok(Planner.decide(p).target>Planner.decide(profile()).target,"objetivo e atividade mudam energia");
  p=profile();p.age=82;ok(!Planner.decide(p).dietAllowed,"faixa fora da equacao automatica");
  p=profile();p.diet="vegan";ok(!Planner.allows(p,"lacteo",false,"milk","lactose"),"vegano exclui leite");ok(!Planner.allows(p,"proteina",false,"egg"),"vegano exclui ovos");ok(Planner.allows(p,"leguminosa",false),"vegano permite leguminosas");
  p=profile();p.restrictions.add("gluten");ok(!Planner.allows(p,"cereal",false,"gluten"),"sem gluten exclui aveia nao certificada");
  p=profile();p.restrictions.add("milk");ok(!Planner.allows(p,"lacteo",false,"milk"),"alergia leite exclui derivados");
  System.out.println("Planner: "+n+" verificacoes passaram");
 }
}
