# Verificação — Rumo 1.3.0

## Correção reproduzida
O teste Android na versão anterior falhou em “objetivo muda selecao das receitas”. No motor antigo, a seleção devolvia o primeiro trio válido em ordem de catálogo. Na web, o teste do novo campo também identificou uma comparação entre número e string que mostrava 1 dia ao editar o perfil; corrigida e verificada no navegador.

## Evidência
- Web: 17 testes Node passaram, cobrindo cardápios de sete dias, perfis contrastantes, todas as frequências 1–6, descansos, força não consecutiva, sábado aquático, filtros/equivalência e migração.
- Navegador Chrome: 24 verificações passaram, incluindo frequência padrão/seleção/edição, semana exibida, explicação do treino, atualização de perfil, dados locais, backup, histórico, migração de dados anteriores, reload/gravação offline e layouts 320/390/1280 px.
- Android API 35: 646 verificações de personalização/migração, 775 verificações de perfil, 20 de backup, teste visual nativo de histórico e teste de cadastro de frequência passaram.
- Android: 23 verificações Core, 19 Planner e 5 de catálogo passaram. APK assinado com a mesma chave da versão anterior; chave fora do código público.
- Revisão independente: um problema no select da web, corrigido com teste de regressão. Sem outros achados importantes reportados.

O cache inclui todos os módulos novos e o motor anterior. O histórico conserva o motor de origem: o plano atualizado não reinterpreta a sessão já registrada. Não houve teste em iPhone físico/Safari; esse limite permanece.


## Correção web 1.3.1
O teste de atualização real revelou que cache.addAll podia reutilizar arquivos HTTP antigos, mesmo sob um novo nome de cache. A falha foi reproduzida com a versão pública 1.2.0 e com um servidor de teste de cache longo. Agora a instalação usa Request com cache reload, só ativa após o preparo completo e aplica a versão nova no reload. A regressão passou com os arquivos completos da versão antiga e no teste autocontido tests/cache-update.cjs. O APK Android não foi alterado por esta correção.
