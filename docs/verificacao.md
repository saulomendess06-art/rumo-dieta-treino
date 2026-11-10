# Verificação — versão 1.1.0

10 de novembro de 2026. Testes em emulador dedicado Android 15, tela 320×640, paisagem 640×320 e fonte ampliada a 130%.

- APK compilado e assinatura v2/v3 verificada. Instalação de atualização sobre a versão anterior concluída, com o mesmo identificador e chave.
- 23 verificações de domínio e 19 do motor numérico passaram.
- 5 testes de integridade do catálogo passaram.
- Instrumentação Android: 793 verificações de perfis, filtros, alternativas, porções, condições clínicas, migração e edição passaram.
- 20 verificações de backup, limites, dados inválidos e preservação passaram.
- Regressão de histórico: um treino antigo continua visível e mantém perfil/semana ao editar anotação após mudar o cadastro e a semana selecionada. O teste falhou antes da correção e passou depois.
- Teste completo pela interface: 15 verificações passaram, incluindo instalação nova sem perfil pessoal, cadastro, revisão, cálculo, geração, refeições, água, peso decimal, treino, mudança para perfil clínico, preservação dos registros, reinício e fonte ampliada.
- Rotação para paisagem e retorno conferidos, com navegação e pesagem preservadas.
- Revisão independente de código realizada. O problema material encontrado no contexto dos treinos foi corrigido e coberto pela regressão.
- Catálogos, código e arquivos preparados para publicação foram auditados para remover informações pessoais da versão anterior, chaves, credenciais e caminhos locais. O repositório começa com histórico limpo.

O aplicativo não passou por teste em celular físico nem por validação clínica individual das sugestões. Os perfis usados nos testes são fictícios. Instalação nova contém cadastro e histórico vazios; informações reais são fornecidas pelo usuário no próprio aparelho.
