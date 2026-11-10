"""Teste E2E em AVD dedicado. Apaga somente dados de teste no emulator-5580."""
import subprocess,time,xml.etree.ElementTree as ET,sys,re,pathlib
ADB=sys.argv[1];SERIAL='emulator-5580';BASE=pathlib.Path(__file__).resolve().parents[1]
def adb(*args):return subprocess.check_output([ADB,'-s',SERIAL,*args],text=True,encoding='utf-8').strip()
def tree():
    adb('shell','uiautomator','dump','/sdcard/public-test.xml')
    return ET.fromstring(adb('shell','cat','/sdcard/public-test.xml'))
def node(label,partial=False):
    nodes=list(tree().iter('node'));label=label.casefold()
    for key in ['content-desc','text']:
        for n in nodes:
            text=n.attrib.get(key,'').casefold()
            if text==label or partial and label in text:return n
def tap(label,search=False,partial=False):
    for _ in range(30 if search else 1):
        n=node(label,partial)
        if n is not None:
            b=list(map(int,re.findall(r'\d+',n.attrib['bounds'])))
            if b[3]-b[1]>=(12 if n.attrib.get('clickable')=='true' else 6):adb('shell','input','tap',str((b[0]+b[2])//2),str((b[1]+b[3])//2));time.sleep(.3);return
        adb('shell','input','swipe','160','460','160','300','500')
    raise AssertionError('Controle ausente: '+label)
def fill(label,text):
    tap(label,True);adb('shell','input','text',text)
    if 'mInputShown=true' in adb('shell','dumpsys','input_method'):adb('shell','input','keyevent','4')
def select(label,value):tap(label,True);tap(value,True)
def has(label):return node(label,True) is not None
def check(b,label):assert b,label;print('PASS:',label,flush=True)
def photo(name):
    adb('shell','screencap','-p','/sdcard/'+name+'.png');adb('pull','/sdcard/'+name+'.png',str(BASE/'build'/(name+'.png')))
adb('shell','pm','clear','br.com.saulo.rumoaotaf')
adb('shell','settings','put','system','show_ime_with_hard_keyboard','0')
for setting in ['window_animation_scale','transition_animation_scale','animator_duration_scale']:adb('shell','settings','put','global',setting,'0')
adb('shell','am','start','-W','-n','br.com.saulo.rumoaotaf/.MainActivity');time.sleep(1)
check(has('Um plano que começa'),'apresentacao em instalacao nova');photo('welcome')
tap('Começar');check(has('Vamos conhecer você'),'cadastro pessoal sem valores do criador')
fill('Como quer ser chamado?','Alex');fill('Peso atual em kg','80');fill('Altura em cm','175');fill('Idade em anos','30')
select('Sexo para estimativa energética','Masculino');tap('Continuar')
select('Qual é seu foco?','Perder peso');select('Atividade física no dia a dia','Pouco ativo / sedentário');select('Experiência com exercícios','Começando ou voltando agora');select('Onde vai treinar?','Em casa');tap('Continuar')
tap('Sem glúten',True);tap('Sem lactose',True);select('Como está sua saúde para começar?','Nenhuma condição ou sintoma conhecido');tap('Conferir meus dados')
check(has('Seu ponto de partida'),'revisao antes de gerar');check(has('1800 kcal'),'estimativa muda com dados e objetivo')
tap('Entendi: os dados ficam neste celular e as sugestões não substituem orientação profissional.',True);tap('Montar minha rotina')
check(has('Sua rotina, no seu ritmo'),'cadastro gera acompanhamento');photo('public-home')
tap('Alimentação');tap('Café da manhã: alternativa 2',True);tap('Marcar esta refeição',True)
tap('Hoje');tap('Alimentação do dia',True);check(has('1 de 4 refeições marcadas'),'refeicao personalizada registrada')
tap('+ 250 ml',True);check(has('250 ml registrados'),'hidratacao do novo perfil')
tap('Evolução');tap('Registrar peso',partial=True);fill('Peso em kg','79,5');tap('Salvar peso');check(has('79,5 kg'),'peso decimal no perfil publico')
tap('Treinos');check(has('Seu plano de treinos'),'rotina gerada no aplicativo');photo('public-training')
tap('Registrar treino',True);tap('Concluí a sessão planejada');tap('Salvar registro');tap('Treinos');tap('Sessão concluída',True,True);check(has('Sessão concluída'),'registro separado das sugestoes atuais')
tap('Evolução');tap('Editar perfil e gerar novo plano',True);tap('Continuar');tap('Continuar');select('Como está sua saúde para começar?','Condição ou sintoma que precisa de avaliação');tap('Conferir meus dados');tap('Entendi: os dados ficam neste celular e as sugestões não substituem orientação profissional.',True);tap('Montar minha rotina')
tap('Alimentação');check(has('Orientação individual para seu perfil'),'condicao clinica suspende dieta automatica');photo('clinical-guidance')
tap('Treinos');tap('Sessão concluída',True,True);check(has('Sessão concluída'),'perfil clinico preserva treino anterior')
tap('Evolução');check(has('79,5 kg'),'editar cadastro preserva pesagem')
adb('shell','am','force-stop','br.com.saulo.rumoaotaf');adb('shell','am','start','-W','-n','br.com.saulo.rumoaotaf/.MainActivity');time.sleep(1)
tap('Evolução');check(has('79,5 kg'),'cadastro e registros persistem apos reiniciar')
adb('shell','settings','put','system','font_scale','1.3');time.sleep(1);check(all(node(t) is not None for t in ['Hoje','Alimentação','Treinos','Evolução']),'navegacao com fonte ampliada');photo('public-large-font');adb('shell','settings','put','system','font_scale','1.0')
print('PUBLIC SMOKE COMPLETO',flush=True)
