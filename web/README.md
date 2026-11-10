# Rumo Web 1.2.0

Versão instalável (PWA) do Rumo para iPhone, iPad, Android e computador. Site público: https://rumo-dieta-treino.ruddybatch.chatgpt.site

## No iPhone
1. Abra o site no Safari.
2. Toque em Compartilhar (em alguns layouts, no menu da página).
3. Escolha Adicionar à Tela de Início. Se não aparecer, procure Editar Ações.
4. Mantenha Abrir como App da Web ativado, se essa opção estiver disponível, e toque em Adicionar.
5. Abra o Rumo pelo ícone e cadastre seu perfil.

O site contém esse guia antes do cadastro e pelo botão Instalar no celular. Instale antes de preencher o perfil: o app da Tela de Início pode manter dados separados do Safari. Para levar seu diário, exporte um backup web em Perfil e restaure na instalação.

## Dados e offline
Sem conta, sem sincronização, sem envio de cadastro/diário a servidor. O armazenamento é local neste navegador e origem. Limpeza de dados, modo privado, remoção do app ou pressão de armazenamento podem causar perda; mantenha backup privado. O backup não é criptografado e tem formato separado do Android. A hospedagem recebe os acessos normais aos arquivos e seus metadados técnicos.

Aguarde Pronto para usar offline neste aparelho após o primeiro carregamento. O catálogo e a interface ficam em cache; o diário funciona sem conexão. Atualizações instalam em segundo plano: feche todas as abas/janelas do Rumo e abra novamente quando avisado. Os dados pessoais não entram no cache dos arquivos públicos.

## Desenvolvimento
Sem build e sem dependências para executar: sirva dist/ por HTTP (ex.: python -m http.server 4173 --directory dist) ou HTTPS. Service worker funciona em HTTPS ou localhost. Node 22+ para node --test tests/*.test.mjs.

O smoke test tests/browser.cjs usa Playwright, instalado separadamente para desenvolvimento. Configure CHROME_PATH se usar Chrome de sistema e PLAYWRIGHT_MODULE se o módulo não estiver no caminho padrão. Execute com o servidor local em 4173, ou RUMO_URL apontando para o site a validar.

Catálogo: dist/plan.json, cópia de assets/plan.json do Android. Motor determinístico: dist/engine.js. Estado e validação de backup: dist/store.js. As regras educativas e limites são os documentados em docs/modelo.md no repositório principal. Não existe prescrição clínica ou análise de saúde por IA.

## Fontes
- Apple: https://support.apple.com/pt-br/guide/iphone/iphea86e5236/ios
- WebKit e armazenamento: https://webkit.org/tracking-prevention/
- Política de armazenamento: https://webkit.org/blog/14403/updates-to-storage-policy/
