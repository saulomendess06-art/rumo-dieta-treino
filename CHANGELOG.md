# Atualizações

## 1.3.2 Web — confirmação da versão instalada

- Recursos offline são buscados com identificação de versão na URL e validados antes da ativação, incluindo o HTML esperado.
- Verificação explícita da versão do service worker elimina a confusão entre troca de controlador e conclusão da atualização.
- Android continua na versão 1.3.0.

## 1.3.1 Web — atualização do cache

- A instalação do novo cache recarrega os arquivos pela rede, evitando copiar HTML e módulos antigos do cache HTTP do navegador.
- Troca de versão somente depois de preparar todos os arquivos; recarregar a página aplica a versão nova.
- Regressão verificada partindo de um cache 1.2.0 com validade longa. Android permanece 1.3.0.

## 1.3.0 — Personalização e frequência semanal

- Corrigida a seleção que devolvia sempre as primeiras receitas compatíveis. Objetivo e composição participam da escolha, com variedade nos sete dias.
- Frequência de 1 a 6 treinos escolhida no cadastro, com dias de descanso e natação no sábado quando selecionada.
- Sessões variam por objetivo, experiência, atividade atual, local e começo conservador quando indicado.
- Explicação das adaptações na alimentação e nos treinos.
- Motores anteriores preservados para reconstruir os registros históricos sem alterar seus exercícios ou refeições.
- Correção disponível no site e APK Android 1.3.0.

## 1.2.0 — Rumo Web para iPhone e outros navegadores

- Site instalável (PWA) com apresentação e guia para adicionar à Tela de Início do iPhone.
- Cadastro, sugestões alimentares com alternativas, rotina de treino e diário local.
- Modo offline depois do preparo inicial, ícones e navegação adaptados ao celular.
- Backup web com validação, registros históricos por revisão de perfil e consulta dos próximos dias.
- Código e testes em web/. O aplicativo Android continua na versão 1.1.0.

Site: https://rumo-dieta-treino.ruddybatch.chatgpt.site

## 1.1.0 — Versão pública Android

Cadastro local, sugestões adaptadas, alternativas por refeição e preservação do histórico.
