"""Cardápio inicial: dados por 100 g da TACO, 4ª ed., NEPA/Unicamp, 2011.
Valores: kcal, proteína, carboidratos totais, gorduras, fibras. Traços -> 0.
Medidas caseiras são aproximações; gramas da parte comestível são a referência.
"""
import json
from pathlib import Path

foods={}
def food(key,name,taco,group,kcal,p,c,f,fiber=0,meat=False):
    foods[key]={'name':name,'taco':taco,'group':group,'values':[kcal,p,c,f,fiber],'meat':meat}
food('rice','Arroz branco cozido',3,'cereal',128,2.5,28.1,.2,1.6)
food('oats','Aveia em flocos, seca',7,'cereal',394,13.9,66.6,8.5,9.1)
food('bread','Pão de forma integral',52,'cereal',253,9.4,49.9,3.7,6.9)
food('cuscuz','Cuscuz de milho cozido',533,'cereal',113,2.2,25.3,.7,2.1)
food('manioc','Mandioca cozida',129,'cereal',125,.6,30.1,.3,1.6)
food('banana','Banana-prata sem casca',182,'fruta',98,1.3,26,.1,2)
food('apple','Maçã Fuji com casca, sem miolo',222,'fruta',56,.3,15.2,0,1.3)
food('papaya','Mamão Formosa sem casca/sementes',225,'fruta',45,.8,11.6,.1,1.8)
food('orange','Laranja-pera sem casca',214,'fruta',37,1,8.9,.1,.8)
food('lettuce','Alface crespa crua',78,'vegetal',11,1.3,1.7,.2,1.8)
food('tomato','Tomate cru',157,'vegetal',15,1.1,3.1,.2,1.2)
food('carrot','Cenoura cozida',109,'vegetal',30,.8,6.7,.2,2.6)
food('oil','Azeite de oliva',260,'gordura',884,0,0,100)
food('chicken','Peito de frango sem pele, grelhado',410,'proteina',159,32,0,2.5,meat=True)
food('beef','Patinho sem gordura, grelhado',377,'proteina',219,35.9,0,7.3,meat=True)
food('egg','Ovo cozido, sem casca',488,'proteina',146,13.3,.6,9.5)
food('yogurt','Iogurte natural integral sem açúcar',448,'lacteo',51,4.1,1.9,3)
food('ricotta','Ricota',469,'lacteo',140,12.6,3.8,8.1)
food('beans','Feijão carioca cozido, com caldo',561,'leguminosa',76,4.8,13.6,.5,8.5)
food('lentils','Lentilha cozida',577,'leguminosa',93,6.3,16.3,.5,7.9)

def item(key,g,portion):return {'food':key,'g':g,'portion':portion}
def salad():return [item('lettuce',50,'1 prato de sobremesa, sem apertar'),item('tomato',150,'1 tomate médio a grande')]
def option(title,items,note=''):
    return {'title':title,'items':items,'note':note}
breakfast=[
 option('Cuscuz, ovos e fruta',[item('cuscuz',170,'1 xícara de chá, aproximadamente'),item('egg',100,'2 ovos médios'),item('yogurt',170,'1 pote de 170 g'),item('banana',100,'1 banana grande ou 2 pequenas')],'Ovos cozidos. Café ou chá sem açúcar, se desejar.'),
 option('Pão integral, ovos e mamão',[item('bread',75,'3 fatias de 25 g'),item('egg',100,'2 ovos médios'),item('yogurt',170,'1 pote de 170 g'),item('papaya',220,'1 fatia grande')]),
 option('Aveia com iogurte, ovo e maçã',[item('oats',55,'5 a 6 colheres de sopa rasas'),item('yogurt',250,'1 pote e mais cerca de 1/2 pote'),item('egg',50,'1 ovo médio'),item('apple',180,'1 maçã grande')],'Misture a aveia ao iogurte; coma o ovo à parte.')
]
lunch=[
 option('Arroz, feijão e frango',[item('rice',200,'8 colheres de sopa de 25 g'),item('beans',160,'2 conchas pequenas de 80 g'),item('chicken',150,'1 filé grande')]+salad()+[item('carrot',100,'4 colheres de sopa'),item('oil',10,'2 colheres de chá')],'O azeite representa o total de gordura adicionada na sua porção; distribua entre preparo e salada.'),
 option('Arroz, lentilha e patinho',[item('rice',180,'7 colheres de sopa, aproximadamente'),item('lentils',160,'2 conchas pequenas de 80 g'),item('beef',130,'1 bife médio a grande')]+salad()+[item('carrot',100,'4 colheres de sopa'),item('oil',5,'1 colher de chá')],'Use patinho sem gordura aparente. A porção de azeite é menor porque a carne já contém mais gordura.'),
 option('Mandioca, feijão e frango',[item('manioc',200,'4 pedaços médios de 50 g'),item('beans',160,'2 conchas pequenas de 80 g'),item('chicken',160,'1 filé grande')]+salad()+[item('carrot',100,'4 colheres de sopa'),item('oil',10,'2 colheres de chá')],'Mandioca e feijão já cozidos. Não somar arroz a esta opção.')
]
snack=[
 option('Iogurte, aveia e banana',[item('yogurt',200,'1 copo de 200 g'),item('oats',30,'3 colheres de sopa rasas'),item('banana',80,'1 banana média')]),
 option('Pão com ricota e laranja',[item('bread',50,'2 fatias de 25 g'),item('ricotta',80,'4 colheres de sopa, aproximadamente'),item('orange',150,'1 laranja média a grande')],'Amasse a ricota com ervas e um pouco de água, se desejar.'),
 option('Iogurte com maçã e aveia',[item('yogurt',170,'1 pote de 170 g'),item('oats',25,'2 a 3 colheres de sopa rasas'),item('apple',200,'1 maçã grande ou 2 pequenas')])
]
dinner=[
 option('Arroz, lentilha e ovos',[item('rice',150,'6 colheres de sopa de 25 g'),item('lentils',150,'cerca de 2 conchas pequenas'),item('egg',100,'2 ovos médios')]+salad()+[item('oil',10,'2 colheres de chá')],'Sem carne. O azeite já está incluído no total estimado da refeição.'),
 option('Cuscuz, feijão e ovos',[item('cuscuz',180,'1 xícara de chá bem servida'),item('beans',150,'cerca de 2 conchas pequenas'),item('egg',100,'2 ovos médios')]+salad()+[item('oil',10,'2 colheres de chá')]),
 option('Sanduíche de ricota e feijão à parte',[item('bread',100,'4 fatias de 25 g'),item('ricotta',120,'6 colheres de sopa, aproximadamente'),item('beans',100,'1 concha média')]+salad()+[item('oil',5,'1 colher de chá')],'Use o tomate e parte da alface nos sanduíches. O feijão é acompanhamento, não recheio obrigatório.')
]
days=[]
for idx,name in enumerate(['Segunda','Terça','Quarta','Quinta','Sexta','Sábado','Domingo']):
    meals=[]
    for label,time,opts in [('Café da manhã','Ao acordar',breakfast),('Almoço','No seu horário habitual',lunch),('Lanche','Entre almoço e jantar',snack),('Jantar','No seu horário habitual',dinner)]:
        # A dieta principal é estável; variar é opcional pelas duas alternativas.
        meals.append({'label':label,'time':time,'options':opts})
    days.append({'name':name,'meals':meals})

def step(name,dose,detail):return {'name':name,'dose':dose,'detail':detail}
def workout(kind,title,minutes,steps,effort='Leve · 2–3 de 10'):
    return {'kind':kind,'title':title,'minutes':minutes,'effort':effort,'steps':steps}
workouts=[]
for w in range(4):
    walk=[20,23,25,28][w]
    sets=[1,1,2,2][w]
    reps=[6,8,6,8][w]
    walksteps=[step('Comece devagar','5 min','Caminhe em terreno plano, regular e sem mudanças rápidas de direção.'),step('Caminhada confortável',f'{walk-10} min','Mantenha um ritmo em que consegue conversar. Se necessário, divida em blocos curtos.'),step('Desacelere','5 min','Diminua o passo. Observe o joelho durante a atividade e no dia seguinte.')]
    strength=[step('Aqueça com movimentos leves','4–5 min','Caminhe devagar pela casa, movimente ombros e tornozelos. Use apoio estável.'),step('Flexão na parede',f'{sets} série(s) de {reps} repetições','Mãos na parede na altura do peito. Dobre os cotovelos lentamente, mantendo o corpo alinhado. Aproxime os pés da parede para facilitar.'),step('Retração das escápulas',f'{sets} série(s) de {reps+2} repetições','Sentado, relaxe o pescoço e aproxime suavemente as escápulas por 2 segundos. Sem prender a respiração.'),step('Contração abdominal sentada',f'{sets} série(s) de 5 × 5 segundos','Sentado em cadeira firme, ative suavemente o abdômen e continue respirando. Relaxe entre as repetições.'),step('Sentar e levantar — opcional',f'{sets} série(s) de 5 repetições','Somente sem dor ou instabilidade: use cadeira alta e firme encostada na parede e apoio das mãos. Não agache fundo. Omita este exercício se o joelho incomodar.'),step('Pausas e finalização','60–90 s entre séries; 3 min ao final','Descanse mais se precisar. Termine com movimentos leves, sem alongamento forçado. Não busque exaustão.')]
    swim=[step('Adapte-se à água','5 min','Piscina com supervisão no local. Entre com calma e faça movimentos confortáveis.'),step('Nade leve com pausas',f'{[5,7,10,10][w]}–{[10,12,15,15][w]} min no total','Faça trechos curtos no estilo confortável, descansando 30–60 s ou mais. Use o tempo para técnica; não há meta de distância nesta fase.'),step('Cuide do joelho','Durante toda a sessão','Evite pernadas, impulsões e viradas que provoquem sintomas. Peça ao instrutor para adaptar o movimento. Sem apneia ou hiperventilação.'),step('Termine com calma','3–5 min','Reduza o ritmo e saia com apoio. O tempo inclui as pausas. Nunca treine sozinho.')]
    rest=workout('rest','Descanso e recuperação',0,[step('Recupere-se','Sem treino programado','Mantenha sua rotina normal, durma bem e observe se há dor, inchaço ou cansaço persistente. Não precisa compensar com outro treino.')], 'Recuperação')
    workouts.append([workout('walk','Caminhada de adaptação',walk,walksteps),workout('strength','Força leve em casa',[20,22,25,25][w],strength),rest,workout('walk','Caminhada de adaptação',walk,walksteps),workout('strength','Força leve em casa',[20,22,25,25][w],strength),workout('swim','Natação leve e técnica',[20,22,25,25][w],swim),rest])

plan={'version':1,'foods':foods,'days':days,'workouts':workouts,'nutritionNote':'Valores aproximados da TACO (NEPA/Unicamp, 4ª ed., 2011). Pesos de alimentos cozidos referem-se à parte pronta para comer; frutas e ovos sem casca. Medidas caseiras variam: os gramas são a referência. Marcas, preparo e óleo alteram os valores. As alternativas aproximam energia e proteína, mas não são nutricionalmente idênticas.','sources':[
 {'title':'TACO · NEPA/Unicamp, 4ª edição, 2011','url':'https://nepa.unicamp.br/publicacoes/tabela-taco-pdf/','detail':'Composição por 100 g. Cada alimento no cardápio tem seu número de identificação TACO. Valores de traços foram aproximados a zero.'},
 {'title':'Guia Alimentar · Ministério da Saúde','url':'https://bvsms.saude.gov.br/bvs/publicacoes/guia_alimentar_populacao_brasileira_2ed_atualizada.pdf','detail':'Base em alimentos in natura e minimamente processados, refeições e hábitos sustentáveis.'},
 {'title':'Atividade física · Ministério da Saúde','url':'https://www.gov.br/saude/pt-br/assuntos/saude-brasil/eu-quero-me-exercitar/documentos/pdf/guia_atividade_fisica_populacao_brasileira.pdf/@@download/file','detail':'Retorno gradual à atividade. O ciclo do aplicativo é uma proposta de adaptação; não é um protocolo oficial do TAF.'},
 {'title':'Retorno após lesão · Leeds Community Healthcare NHS','url':'https://leedscommunityhealthcare.nhs.uk/our-services-a-z/musculoskeletal-msk/return-to-activity-or-sport-after-injury/','detail':'Aumentar gradualmente conforme recuperação. Dor crescente, inchaço ou perda de função exigem interromper e procurar avaliação.'},
 {'title':'Alimentação e peso · NIDDK','url':'https://www.niddk.nih.gov/health-information/weight-management/adult-overweight-obesity/eating-physical-activity','detail':'Alimentação, regularidade e atividade para controle de peso; estimativas não garantem perda em prazo fixo.'},
 {'title':'CBMPE · exemplo de página da organizadora','url':'https://beta.institutoaocp.org.br/concursos/715','detail':'Conferir cargo, edital e retificações. O aplicativo não presume datas oficiais ou índices de aprovação.'}
]}
path=Path(__file__).resolve().parents[1]/'assets'/'legacy-plan.json'
path.write_text(json.dumps(plan,ensure_ascii=False,indent=2),encoding='utf-8')
print('Cardápio e quatro semanas gerados:',path)
