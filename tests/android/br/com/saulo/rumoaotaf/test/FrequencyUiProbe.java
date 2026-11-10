package br.com.saulo.rumoaotaf.test;
import android.app.*;import android.content.*;import android.os.Bundle;import android.view.*;import android.widget.*;import br.com.saulo.rumoaotaf.*;import org.json.*;
public class FrequencyUiProbe extends Instrumentation {
 Activity activity;View view(String text){View v=HistoryProbe.find(activity.getWindow().getDecorView(),text);if(v==null)throw new AssertionError("campo ausente: "+text);return v;}
 View description(View v,String text){if(text.contentEquals(v.getContentDescription()==null?"":v.getContentDescription()))return v;if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++){View found=description(g.getChildAt(i),text);if(found!=null)return found;}}return null;}
 View field(String text){View found=description(activity.getWindow().getDecorView(),text);if(found==null)throw new AssertionError("campo sem descrição: "+text);return found;}
 void click(String text){runOnMainSync(()->view(text).performClick());waitForIdleSync();}
 void value(String label,String text){runOnMainSync(()->((EditText)field(label)).setText(text));}
 void select(String label,int index){runOnMainSync(()->((Spinner)field(label)).setSelection(index));}
 @Override public void onCreate(Bundle b){super.onCreate(b);start();}
 @Override public void onStart(){Bundle out=new Bundle();Store store=new Store(getTargetContext());String saved=store.backup();try{
 store.restore(Store.fresh().toString());Intent i=new Intent(getTargetContext(),MainActivity.class);i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);activity=startActivitySync(i);click("Começar");
 value("Peso atual em kg","80");value("Altura em cm","175");value("Idade em anos","30");select("Sexo para estimativa energética",1);click("Continuar");
 if(((Spinner)field("Quantas vezes quer treinar por semana?")).getSelectedItemPosition()!=4)throw new AssertionError("padrao deve ser cinco dias");
 select("Quantas vezes quer treinar por semana?",2);select("Qual é seu foco?",1);select("Atividade física no dia a dia",1);select("Experiência com exercícios",1);select("Onde vai treinar?",1);click("Continuar");select("Como está sua saúde para começar?",1);click("Conferir meus dados");click("Entendi: os dados ficam");click("Montar minha rotina");
 Store reopened=new Store(getTargetContext());if(reopened.state().getJSONObject("profile").getInt("frequency")!=3)throw new AssertionError("cadastro precisa salvar tres dias");
 out.putString("result","PASS Android onboarding selects and persists weekly frequency");finish(-1,out);
 }catch(Throwable e){out.putString("result","FAIL "+e);finish(0,out);}finally{if(activity!=null)runOnMainSync(()->activity.finish());store.restore(saved);}}
}
