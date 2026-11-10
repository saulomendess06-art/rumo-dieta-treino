import test from 'node:test';import assert from 'node:assert/strict';import{readFileSync,existsSync}from'node:fs';
const read=p=>readFileSync(new URL('../dist/'+p,import.meta.url),'utf8');
test('documento acessível e manifesto standalone com ícones reais',()=>{
 const html=read('index.html');assert.match(html,/lang="pt-BR"/);assert.match(html,/viewport-fit=cover/);assert.match(html,/apple-touch-icon/);
 const m=JSON.parse(read('manifest.webmanifest'));assert.equal(m.display,'standalone');assert.equal(m.start_url,'./');
 for(const icon of m.icons)assert.ok(existsSync(new URL('../dist/'+icon.src,import.meta.url)));
});
test('guia iPhone inclui etapas manuais e alerta sobre dados locais',()=>{
 const app=read('app.js');for(const phrase of ['Safari','Compartilhar','Adicionar à Tela de Início','Abrir como App da Web','Editar Ações','backup'])assert.ok(app.includes(phrase),phrase);
});
test('todos os arquivos offline existem',()=>{
 const sw=read('sw.js');const files=JSON.parse(sw.match(/const FILES=(\[[^;]+\]);/)[1]);for(const f of files)assert.ok(existsSync(new URL('../dist/'+f.replace('./',''),import.meta.url)),f);
});
