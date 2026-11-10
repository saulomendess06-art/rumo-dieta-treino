"""Catálogo genérico. Executar após make_plan.py; sem perfis ou registros pessoais."""
import json
from pathlib import Path
root=Path(__file__).resolve().parents[1]
p=json.loads((root/'assets/legacy-plan.json').read_text(encoding='utf-8'))
f=p['foods']
f['tofu']={'name':'Tofu','taco':584,'group':'proteina','values':[64,6.6,2.1,4,.8],'meat':False}
f['soymilk']={'name':'Bebida de soja natural','taco':582,'group':'lacteo_vegetal','values':[39,2.4,4.3,1.6,.4],'meat':False}
for key,value in f.items():
    value['allergens']={'bread':['gluten'],'oats':['gluten'],'egg':['egg'],'yogurt':['milk','lactose'],'ricotta':['milk','lactose'],'tofu':['soy'],'soymilk':['soy']}.get(key,[])
units={'rice':(25,'colher(es) de sopa'),'oats':(10,'colher(es) de sopa rasa(s)'),'bread':(25,'fatia(s)'),'cuscuz':(25,'colher(es) de sopa'),'manioc':(50,'pedaço(s) médio(s)'),'banana':(80,'banana(s) média(s)'),'apple':(150,'maçã(s) média(s)'),'papaya':(150,'fatia(s) média(s)'),'orange':(130,'laranja(s) média(s)'),'lettuce':(25,'punhado(s)'),'tomato':(100,'tomate(s) pequeno(s)'),'carrot':(25,'colher(es) de sopa'),'oil':(5,'colher(es) de chá'),'chicken':(100,'filé(s) médio(s)'),'beef':(100,'bife(s) médio(s)'),'egg':(50,'ovo(s) médio(s)'),'yogurt':(170,'pote(s) de 170 g'),'ricotta':(20,'colher(es) de sopa'),'beans':(80,'concha(s) pequena(s)'),'lentils':(80,'concha(s) pequena(s)'),'tofu':(50,'fatia(s) média(s)'),'soymilk':(200,'copo(s) de 200 ml')}
for key,(g,label) in units.items():f[key]['unitGrams']=g;f[key]['unitLabel']=label
recipes=[[dict(o) for o in meal['options']] for meal in p['days'][0]['meals']]
def add(slot,title,parts,note=''):
    recipes[slot].append({'title':title,'items':[{'food':key,'g':g,'portion':''} for key,g in parts],'note':note})
salad=[('lettuce',50),('tomato',150)]
add(0,'Pão, tofu e banana',[('bread',75),('tofu',150),('banana',100),('oats',20)],'Tofu pode ser amassado com ervas. Aveia e fruta à parte.')
add(0,'Cuscuz com tofu e mamão',[('cuscuz',150),('tofu',180),('papaya',150)])
add(0,'Arroz com tofu e laranja',[('rice',140),('tofu',180),('orange',180)])
add(0,'Mandioca com tofu e banana',[('manioc',130),('tofu',180),('banana',80)])
add(0,'Aveia, bebida de soja e banana',[('oats',60),('soymilk',250),('banana',100)],'Mingau sem açúcar adicionado. Confira o rótulo da bebida de soja.')
add(0,'Pão com pasta de lentilha e banana',[('bread',80),('lentils',100),('banana',100),('oil',5)],'Amasse a lentilha já cozida, tempere com ervas e use no pão.')
add(0,'Cuscuz com feijão e mamão',[('cuscuz',160),('beans',100),('papaya',150),('oil',5)])
add(0,'Arroz com lentilha e laranja',[('rice',160),('lentils',100),('orange',150),('oil',5)])
add(0,'Mandioca, lentilha e banana',[('manioc',150),('lentils',150),('banana',80),('oil',5)])
add(0,'Cuscuz, ovos e banana',[('cuscuz',190),('egg',100),('banana',100),('oil',5)])
add(0,'Arroz, ovos e mamão',[('rice',170),('egg',100),('papaya',150),('oil',5)])
add(0,'Mandioca, ovos e laranja',[('manioc',170),('egg',100),('orange',150),('oil',5)])
for slot in [1,3]:
    add(slot,'Arroz, lentilha e tofu',[('rice',180),('lentils',180),('tofu',200)]+salad+[('oil',10)])
    add(slot,'Cuscuz, feijão e tofu',[('cuscuz',180),('beans',180),('tofu',200)]+salad+[('oil',10)])
    add(slot,'Mandioca, lentilha e tofu',[('manioc',160),('lentils',180),('tofu',200)]+salad+[('oil',10)])
    add(slot,'Arroz com duas leguminosas',[('rice',150),('lentils',250),('beans',150)]+salad+[('oil',10)])
    add(slot,'Cuscuz com lentilha e feijão',[('cuscuz',180),('lentils',250),('beans',150)]+salad+[('oil',10)])
    add(slot,'Mandioca, lentilha e feijão',[('manioc',160),('lentils',250),('beans',150)]+salad+[('oil',10)])
    add(slot,'Arroz, feijão e ovos',[('rice',180),('beans',180),('egg',150)]+salad+[('oil',5)])
    add(slot,'Cuscuz, lentilha e ovos',[('cuscuz',180),('lentils',180),('egg',150)]+salad+[('oil',5)])
    add(slot,'Mandioca, feijão e ovos',[('manioc',180),('beans',180),('egg',150)]+salad+[('oil',5)])
add(2,'Bebida de soja com banana e aveia',[('soymilk',250),('banana',100),('oats',25)])
add(2,'Pão com tofu e maçã',[('bread',50),('tofu',100),('apple',150)])
add(2,'Cuscuz com tofu e mamão',[('cuscuz',100),('tofu',150),('papaya',150)])
add(2,'Mandioca com tofu e laranja',[('manioc',90),('tofu',150),('orange',150)])
add(2,'Pão com lentilha e banana',[('bread',50),('lentils',100),('banana',80)])
add(2,'Cuscuz com lentilha e maçã',[('cuscuz',100),('lentils',100),('apple',150)])
add(2,'Mandioca com feijão e mamão',[('manioc',100),('beans',130),('papaya',150)])
add(2,'Arroz com lentilha e laranja',[('rice',90),('lentils',110),('orange',150)])
add(2,'Ovo, banana e aveia',[('egg',50),('banana',100),('oats',30)])
add(2,'Cuscuz, ovo e mamão',[('cuscuz',110),('egg',50),('papaya',180)])
add(2,'Mandioca, ovo e laranja',[('manioc',110),('egg',50),('orange',170)])
p['recipes']=recipes
p['sources'].extend([
 {'title':'Mifflin–St Jeor, 1990','url':'https://pubmed.ncbi.nlm.nih.gov/2305711/','detail':'Equação de estimativa de repouso em adultos. Fatores de atividade, arredondamento e limites são escolhas operacionais do aplicativo, sem validação clínica do produto.'},
 {'title':'Limites do planejamento de peso · NIDDK','url':'https://www.niddk.nih.gov/health-information/weight-management/body-weight-planner','detail':'Planejamento de peso para adultos, excluindo gestação e amamentação. O aplicativo não oferece dieta clínica.'},
 {'title':'Começar atividade física · CDC','url':'https://www.cdc.gov/healthy-weight-growth/physical-activity/getting-started.html','detail':'Começar gradualmente, considerando condição atual e orientação profissional quando houver problemas de saúde.'}
])
(root/'assets/plan.json').write_text(json.dumps(p,ensure_ascii=False,indent=2),encoding='utf-8')
print('Catálogo público:',sum(map(len,recipes)),'preparações,',len(f),'alimentos TACO')
