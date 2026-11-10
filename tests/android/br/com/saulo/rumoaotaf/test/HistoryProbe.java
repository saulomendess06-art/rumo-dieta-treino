package br.com.saulo.rumoaotaf.test;
import android.app.*;import android.content.*;import android.os.Bundle;import android.view.*;import android.widget.*;import org.json.*;import br.com.saulo.rumoaotaf.*;
public class HistoryProbe extends Instrumentation {
 static View find(View v,String text){if(v instanceof TextView&&((TextView)v).getText().toString().contains(text))return v;if(text.contentEquals(v.getContentDescription()==null?"":v.getContentDescription()))return v;if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++){View f=find(g.getChildAt(i),text);if(f!=null)return f;}}return null;}
 @Override public void onCreate(Bundle b){super.onCreate(b);start();}
 @Override public void onStart(){Bundle out=new Bundle();Store store=new Store(getTargetContext());String old=store.backup();Activity activity=null;try{
  store.restore(Store.fresh().toString());JSONObject p=ProfileProbe.fixture();store.setProfile(p);String date=java.time.LocalDate.now().toString();JSONObject d=store.day(date);d.put("done",true).put("effort",3).put("note","Anotação de sessão antiga").put("week",0);store.saveDay(date,d);
  p.put("health","clinical");store.setProfile(p);store.state().put("week",3);store.save();
  Intent intent=new Intent(getTargetContext(),MainActivity.class);intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);activity=startActivitySync(intent);final Activity a=activity;
  runOnMainSync(()->find(a.getWindow().getDecorView(),"Treinos").performClick());waitForIdleSync();
  if(find(a.getWindow().getDecorView(),"Anotação de sessão antiga")==null)throw new AssertionError("registro de treino deve continuar visivel depois de mudar perfil e semana");
  String originalProfile=d.getString("trainingProfileId");Store reopened=new Store(getTargetContext());reopened.saveTraining(date,true,4,false,"Anotação corrigida");JSONObject edited=reopened.day(date);if(edited.getInt("week")!=0||!edited.getString("trainingProfileId").equals(originalProfile))throw new AssertionError("editar anotacao nao pode mudar perfil/semana originais");
  out.putString("result","PASS historical training visible and context preserved on edit");finish(-1,out);
 }catch(Throwable e){out.putString("result","FAIL "+e);finish(0,out);}finally{if(activity!=null){final Activity a=activity;runOnMainSync(()->a.finish());}store.restore(old);}}
}
