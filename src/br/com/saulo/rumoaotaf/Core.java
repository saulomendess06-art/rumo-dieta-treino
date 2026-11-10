package br.com.saulo.rumoaotaf;
import java.time.LocalDate;
public final class Core {
 public static int dayIndex(LocalDate d){return d.getDayOfWeek().getValue()-1;}
 public static double parseWeight(String s){
   if(s==null || !s.trim().matches("[0-9]{1,3}([.,][0-9]{1,2})?"))throw new IllegalArgumentException("Informe um peso entre 30 e 350 kg.");
   double v=Double.parseDouble(s.trim().replace(',','.'));
   if(!Double.isFinite(v)||v<30||v>350)throw new IllegalArgumentException("Informe um peso entre 30 e 350 kg.");
   return v;
 }
 public static boolean checkDate(String s){
   if(s==null || !s.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"))return false;
   try{LocalDate d=LocalDate.parse(s);return d.getYear()>=2000 && d.getYear()<=2100;}catch(Exception e){return false;}
 }
 public static double[] nutrients(double[] values,double grams){double[] result=new double[5];for(int i=0;i<5;i++)result[i]=values[i]*grams/100;return result;}
 public static int waterAfter(int previous,int change){return (int)Math.max(0,Math.min(20000,(long)previous+change));}
 public static int countSessions(java.util.Map<String,Boolean> sessions,String start,String end){
  int count=0;for(java.util.Map.Entry<String,Boolean> e:sessions.entrySet()){
   String key=e.getKey();if(!Boolean.TRUE.equals(e.getValue())||!checkDate(key)||key.compareTo(start)<0||key.compareTo(end)>0)continue;
   int day=dayIndex(LocalDate.parse(key));if(day!=2&&day!=6)count++;
  }return count;
 }
}
