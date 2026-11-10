# Privacidade — Rumo · Dieta e treino

Versão 1.1.0, 10 de novembro de 2026.

## Dados que você informa

O cadastro pode conter apelido, idade, peso, altura, sexo opcional, atividade física, objetivo, experiência, local de treino, preferências, restrições alimentares e informações de saúde. O acompanhamento guarda peso, água, refeições e registros de exercícios por data.

## Onde ficam e para que servem

Os dados ficam no armazenamento privado do aplicativo neste aparelho. São usados para gerar sugestões locais e mostrar o acompanhamento. Não há conta, servidor, anúncios, análise de uso, envio ao desenvolvedor ou painel com perfis de outras pessoas. O aplicativo não solicita permissão de internet.

## Arquivos e cópias

Exportar um backup usa o seletor de documentos do Android e só acontece quando você escolhe essa ação. O arquivo JSON contém suas informações pessoais, inclusive saúde, e não é criptografado pelo aplicativo. O local escolhido pode pertencer a outro serviço de armazenamento; nesse caso, o tratamento feito por esse serviço está sujeito às suas configurações e políticas. Guarde a cópia em local privado.

Restaurar valida o arquivo e pede confirmação antes de substituir registros. O limite é 8 MB. O backup automático do Android está desativado no manifesto do aplicativo.

## Editar e remover

Você pode editar o cadastro e corrigir/excluir pesagens. Alterar o perfil preserva os registros antigos e as versões das refeições. Limpar os dados ou desinstalar pelo Android remove o armazenamento local, mas não elimina cópias que você exportou. Exclua essas cópias nos locais onde as salvou, se desejar.

## Código público

O repositório contém código, catálogo e perfis fictícios de teste. Ele não recebe nem publica automaticamente os cadastros feitos no aplicativo. Chaves e credenciais não integram o código público.


## Versão web (1.2.0)

O cadastro e o diário ficam no localStorage deste navegador e origem. Não são enviados ao servidor da hospedagem. O service worker armazena somente os arquivos públicos da interface e catálogo. A hospedagem recebe requisições de arquivos e pode registrar IP e metadados técnicos do acesso. Não há análise de uso, anúncios ou fontes remotas.

O app instalado no iPhone pode ter armazenamento separado do Safari. Não há sincronização com o APK Android ou entre navegadores. Limpeza de dados, remoção do app, navegação privada e políticas de armazenamento podem provocar perda do diário. Exporte backups para um local privado; o arquivo inclui dados de saúde e não é criptografado. O formato de backup web é separado do Android. A restauração exige confirmação e substitui o estado local somente após validação.
