# Regras e limites do gerador

O gerador é local e determinístico. Não chama IA nem interpreta clinicamente textos livres. O resultado é educativo e precisa de ajuste profissional conforme o caso.

## Perfil e energia

Cadastro de adultos de 18 a 100 anos, altura de 130 a 220 cm e peso de 30 a 350 kg. As faixas são limites de entrada do aplicativo, não critérios de saúde. A estimativa alimentar automática usa a equação de Mifflin–St Jeor para adultos até 78 anos, com sexo informado e sem situações que exijam avaliação individual.

Repouso estimado, em kcal/dia: `10 × peso(kg) + 6,25 × altura(cm) − 5 × idade + coeficiente`. Coeficientes: +5 para masculino e −161 para feminino. O parâmetro é opcional; se não informado, não é inventado.

Fatores operacionais de atividade: 1,2 (pouco ativo), 1,375 (leve), 1,55 (moderado), 1,725 (muito ativo). Perda de peso: redução de 15% da manutenção estimada. Manter/TAF: manutenção. Força/massa: acréscimo de 5%, até 150 kcal. Arredondamento da referência para 50 kcal.

Faixa automática: mínimo operacional de 1.400 kcal no parâmetro feminino e 1.600 no masculino, máximo de 3.400 kcal. Não há limitação silenciosa: fora da faixa, informar necessidade de ajuste profissional. Esses limites não são uma garantia de adequação nutricional individual.

Sem meta automática em baixo peso (relação peso/altura abaixo de IMC 18,5), condição clínica relevante, gestação/amamentação, nota de saúde não contemplada ou restrição alimentar livre. A descrição livre não é tratada como condição resolvida por um algoritmo. Não são geradas dietas clínicas ou pediátricas.

## Alimentos e alternativas

Composição por 100 g da TACO/Unicamp, com preparação identificada e atribuição. Os rótulos de produtos e os métodos de preparo podem diferir. Traços da tabela são aproximados a zero.

O filtro de padrão alimentar e alérgenos ocorre antes da seleção. Aveia comum recebe a marca de glúten, sem presumir certificação. Leite/lactose, ovo e soja possuem tags explícitas. Veganismo exclui carnes, ovos e laticínios; vegetarianismo exclui carnes. A preferência por carne apenas no almoço também é considerada.

Referência de distribuição energética por objetivo: perder/manter 25/35/15/25%; força 25/30/20/25%; TAF 25/35/20/20%. Ajustar quantidades com unidades de ovos/fatias e porções em gramas; rejeitar ingrediente acima de 500 g por refeição. As três opções de cada refeição ficam próximas em energia (±10%) e proteína (±20% ou 3 g), em relação à principal. Os totais são aproximados; outros nutrientes podem diferir. Se não houver três opções, não preencher com ingrediente proibido: mostrar a limitação e pedir ajuste profissional.

Não há certificação de ausência de alérgenos nem controle de contaminação cruzada. Filtros não substituem leitura de rótulos ou orientação sobre alergias/doença celíaca. Não são exigidos suplementos.

## Atividades

Frequência escolhida de 1 a 6 atividades semanais, em quatro semanas de base com avanço manual. O padrão inicial continua 5. Os dias são distribuídos para incluir descanso e impedir força em dias consecutivos; natação ocupa o sábado quando escolhida. Com 1–2 sessões pode haver caminhada complementar no dia de força; isso não equivale a uma preparação completa. Iniciantes recebem duração curta e força leve; experiência regular pode aumentar volume confortável. O objetivo de força concentra mais sessões de força. Academia usa exemplos em aparelhos com orientação de ajuste; casa usa movimentos básicos sem equipamentos obrigatórios.

Histórico articular sem dor leva a baixo impacto e evita o exercício de sentar/levantar na sequência de força. Condição/sintoma que exige avaliação ou nota de saúde não contemplada impede montar exercícios automaticamente. Isso não é reabilitação.

Natação só é incluída quando a pessoa declara domínio, acesso e vontade de usá-la. Sem isso, há alternativa terrestre; não representa preparação aquática completa para TAF. Não se presumem índices ou datas oficiais de concursos. O esforço e a recuperação orientam repetição da semana.

## Histórico

Cada perfil gerado tem uma revisão. Datas anteriores conservam o perfil associado. Ao editar o cadastro, as marcações alimentares anteriores de hoje ficam no histórico de versões e as escolhas atuais são reiniciadas. Peso e água permanecem.

Treinos registrados guardam perfil e semana próprios. Editar uma anotação não muda esse contexto; as sugestões atuais e o registro salvo são apresentados separadamente, inclusive quando o novo perfil exige orientação clínica. Atualizações da versão 1.0.0 migram os registros e pedem revisão do cadastro.


## Motor 2: seleção e composição semanal

Na versão 1.3.0 as receitas compatíveis são escaladas e pontuadas antes da seleção, em vez de usar a ordem do catálogo. Referências operacionais de composição para pontuação (proteína/carboidrato como fração da energia): perder 0,23/0,48; manter 0,18/0,50; força 0,25/0,45; TAF 0,20/0,55. Fibras recebem preferência até a referência de 14 g/1000 kcal. Esses números são critérios de ranking, não metas nutricionais garantidas ou prescrição individual. Pontuação não determina que um ingrediente seja clinicamente apropriado.

A seleção considera distância dessas referências, diferença energética e reutilização da preparação principal em dias anteriores. A rotação é determinística e limitada pelo catálogo compatível; não usa nome, identificação ou sorteio. Alternativas continuam dentro de ±10% em energia e ±20% ou 3 g em proteína, com preferência por variedade de ingredientes. Restrições estreitas podem reduzir a variedade ou impedir gerar o trio. Não se promete uma dieta exclusiva para cada pessoa.

Experiência e atividade atual determinam níveis de volume. Histórico articular, idade a partir de 65 anos ou relação peso/altura com IMC a partir de 35 levam ao início conservador; isso é uma escolha de prudência do produto, sem diagnóstico ou reabilitação. Academia usa aparelhos com orientação inclusive para iniciantes; casa usa apoios firmes. Objetivos mudam prioridade, sequência, exercícios de base e, quando apropriado, alternância de caminhada confortável. Não se presume capacidade para corrida nem se cria treinamento máximo de TAF.

As versões de motor ficam no perfil. Na atualização, perfis anteriores são marcados como motor 1; o ativo recebe uma revisão motor 2 e frequência inicial 5 editável. A revisão arquiva refeições anteriores de hoje e mantém água, peso e referência do treino salvo. Assim, atualizar a lógica não reinterpreta sessões antigas.

Referências gerais para implementação: [NIDDK — início gradual](https://www.niddk.nih.gov/health-information/weight-management/tips-get-active/tips-starting-physical-activity), [ACSM 2026 — resistência conforme objetivo e capacidade](https://acsm.org/resistance-training-guidelines-update-2026/). As durações, doses e pontuações concretas são regras operacionais do app, sem validação clínica do produto.
