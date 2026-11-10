import br.com.saulo.rumoaotaf.Core;
import java.time.LocalDate;

public class CoreTest {
  static int n=0;
  static void ok(boolean value,String label){n++;if(!value)throw new AssertionError(label);}
  public static void main(String[] args){
    ok(Core.dayIndex(LocalDate.of(2026,11,9))==0,"segunda-feira inicia semana");
    ok(Core.dayIndex(LocalDate.of(2026,11,15))==6,"domingo termina semana");
    ok(Core.parseWeight("109,5")==109.5,"peso com virgula");
    ok(Core.parseWeight(" 110.2 ")==110.2,"peso com ponto");
    for(String s:new String[]{"NaN","Infinity","-10","0","351","1e2","110,2.3",""}){
      boolean rejected=false;try{Core.parseWeight(s);}catch(IllegalArgumentException e){rejected=true;}
      ok(rejected,"rejeita peso invalido: "+s);
    }
    ok(Core.checkDate("2024-02-29"),"aceita ano bissexto");
    for(String s:new String[]{"2026-02-29","2026-11-31","2026-1-2","bad"})ok(!Core.checkDate(s),"rejeita data: "+s);
    double[] v=Core.nutrients(new double[]{128,2.5,28.1,.2,1.6},150);
    ok(Math.abs(v[0]-192)<.0001,"energia de 150g de arroz");
    ok(Math.abs(v[1]-3.75)<.0001,"proteina proporcional");
    ok(Core.waterAfter(0,-250)==0,"agua nunca negativa");
    ok(Core.waterAfter(250,250)==500,"agua incrementa");
    java.util.Map<String,Boolean> sessions=new java.util.HashMap<>();
    sessions.put("2026-11-09",true);sessions.put("2026-11-10",true);sessions.put("2026-11-11",true);sessions.put("2026-11-12",false);sessions.put("2026-11-13",true);
    ok(Core.countSessions(sessions,"2026-11-10","2026-11-12")==1,"contagem exclui anterior ao inicio, descanso e futuro");
    ok(Core.countSessions(sessions,"2026-11-12","2026-11-12")==0,"alterar inicio altera contagem do periodo");
    System.out.println("Core: "+n+" verificacoes passaram");
  }
}
