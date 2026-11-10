package br.com.saulo.rumoaotaf;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import java.util.*;

final class Ui {
 static final int BG=Color.rgb(244,246,249),INK=Color.rgb(25,47,62),MUTED=Color.rgb(95,113,127),GREEN=Color.rgb(22,122,104),NAVY=Color.rgb(18,58,77),LINE=Color.rgb(221,229,235),PALE=Color.rgb(230,245,239),AMBER=Color.rgb(129,83,28);
 final Context c;
 Ui(Context c){this.c=c;}
 int dp(float n){return Math.round(n*c.getResources().getDisplayMetrics().density);}
 GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
 GradientDrawable outline(int color,int stroke){GradientDrawable d=shape(color,14);d.setStroke(dp(1),stroke);return d;}
 LinearLayout column(){LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.VERTICAL);return l;}
 LinearLayout row(){LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
 TextView text(String value,int sp,int color,boolean bold){TextView t=new TextView(c);t.setText(value);t.setTextSize(sp);t.setTextColor(color);t.setFontFeatureSettings("kern");t.setIncludeFontPadding(false);t.setLineSpacing(dp(3),1);if(bold)t.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));return t;}
 void add(LinearLayout parent,View child){parent.addView(child,new LinearLayout.LayoutParams(-1,-2));}
 void space(LinearLayout parent,int h){View v=new View(c);parent.addView(v,new LinearLayout.LayoutParams(1,dp(h)));}
 LinearLayout card(LinearLayout parent){LinearLayout card=column();card.setPadding(dp(18),dp(18),dp(18),dp(18));card.setBackground(shape(Color.WHITE,20));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(14);parent.addView(card,p);return card;}
 TextView button(String label,Runnable action){return button(label,GREEN,Color.WHITE,action);}
 TextView button(String label,int bg,int fg,Runnable action){TextView t=text(label,14,fg,true);t.setGravity(Gravity.CENTER);t.setPadding(dp(14),dp(13),dp(14),dp(13));t.setMinHeight(dp(48));t.setBackground(shape(bg,13));t.setOnClickListener(v->action.run());t.setClickable(true);t.setFocusable(true);return t;}
 void section(LinearLayout p,String title,String note){space(p,5);add(p,text(title,19,INK,true));if(note!=null&&!note.isEmpty()){space(p,5);add(p,text(note,13,MUTED,false));}space(p,13);}
 void hint(LinearLayout p,String title,String body){LinearLayout box=column();box.setPadding(dp(15),dp(14),dp(15),dp(14));box.setBackground(shape(PALE,15));add(box,text(title,14,GREEN,true));space(box,6);add(box,text(body,13,INK,false));add(p,box);space(p,14);}
 View mark(String kind,int color){return new MarkView(c,kind,color);}
 class MarkView extends View{
  String kind;int color;Paint p=new Paint(3);
  MarkView(Context c,String k,int cl){super(c);kind=k;color=cl;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
  @Override protected void onDraw(Canvas c){super.onDraw(c);c.save();c.scale(getWidth()/40f,getHeight()/40f);p.setColor(color);p.setStrokeWidth(2.6f);p.setStyle(Paint.Style.STROKE);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);
   Path path=new Path();
   switch(kind){
    case "home":path.moveTo(7,18);path.lineTo(20,7);path.lineTo(33,18);path.moveTo(11,17);path.lineTo(11,33);path.lineTo(29,33);path.lineTo(29,17);path.moveTo(17,33);path.lineTo(17,23);path.lineTo(23,23);path.lineTo(23,33);break;
    case "food":c.drawOval(9,9,31,31,p);path.moveTo(4,8);path.lineTo(4,32);path.moveTo(35,8);path.lineTo(35,32);break;
    case "strength":path.moveTo(5,14);path.lineTo(5,26);path.moveTo(10,10);path.lineTo(10,30);path.moveTo(10,20);path.lineTo(30,20);path.moveTo(30,10);path.lineTo(30,30);path.moveTo(35,14);path.lineTo(35,26);break;
    case "chart":path.moveTo(6,7);path.lineTo(6,33);path.lineTo(34,33);path.moveTo(11,25);path.lineTo(18,19);path.lineTo(24,23);path.lineTo(33,11);break;
    case "swim":c.drawCircle(27,10,3,p);path.moveTo(8,18);path.lineTo(18,12);path.lineTo(27,20);path.moveTo(5,27);path.cubicTo(10,22,15,32,20,27);path.cubicTo(25,22,30,32,35,27);path.moveTo(5,34);path.cubicTo(10,29,15,39,20,34);path.cubicTo(25,29,30,39,35,34);break;
    case "rest":path.moveTo(26,6);path.cubicTo(7,6,6,35,26,34);path.cubicTo(12,25,15,15,26,6);break;
    default:c.drawCircle(23,7,3,p);path.moveTo(21,13);path.lineTo(17,23);path.lineTo(26,28);path.lineTo(30,35);path.moveTo(18,20);path.lineTo(11,15);path.moveTo(18,23);path.lineTo(12,33);path.moveTo(21,14);path.lineTo(29,19);break;
   }c.drawPath(path,p);c.restore();}
 }
 static class WeightChart extends View {
  final java.util.List<Double> values;final Paint p=new Paint(3);final float density;
  WeightChart(Context c,java.util.List<Double> v){super(c);values=v;density=c.getResources().getDisplayMetrics().density;setContentDescription("Gráfico de peso: "+v.size()+" registros, em ordem cronológica.");}
  @Override protected void onDraw(Canvas c){super.onDraw(c);float left=43*density,top=16*density,right=getWidth()-14*density,bottom=getHeight()-24*density;
   if(values.isEmpty())return;double min=Collections.min(values),max=Collections.max(values);double pad=Math.max(.5,(max-min)*.25);min-=pad;max+=pad;
   p.setStrokeWidth(density);p.setTextSize(10*getResources().getDisplayMetrics().scaledDensity);
   for(int i=0;i<3;i++){float y=top+(bottom-top)*i/2;p.setColor(LINE);c.drawLine(left,y,right,y,p);p.setColor(MUTED);c.drawText(String.format(new Locale("pt","BR"),"%.1f",max-(max-min)*i/2),1,y+4*density,p);}
   Path line=new Path();p.setColor(GREEN);p.setStrokeWidth(3*density);p.setStyle(Paint.Style.STROKE);
   for(int i=0;i<values.size();i++){float x=values.size()==1?(left+right)/2:left+(right-left)*i/(values.size()-1),y=bottom-(float)((values.get(i)-min)/(max-min))*(bottom-top);if(i==0)line.moveTo(x,y);else line.lineTo(x,y);}
   c.drawPath(line,p);p.setStyle(Paint.Style.FILL);
   for(int i=0;i<values.size();i++){float x=values.size()==1?(left+right)/2:left+(right-left)*i/(values.size()-1),y=bottom-(float)((values.get(i)-min)/(max-min))*(bottom-top);c.drawCircle(x,y,4*density,p);}
  }
 }
}
