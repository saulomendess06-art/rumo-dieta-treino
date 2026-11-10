const {chromium}=require(process.env.PLAYWRIGHT_MODULE||'playwright');
const assert=require('node:assert/strict'),fs=require('node:fs'),path=require('node:path'),http=require('node:http');
(async()=>{
 let old=true;const dist=path.resolve(__dirname,'../dist');
 const fixture={
  'index.html':'<!doctype html><html><body><h1>Versão anterior</h1><footer>Rumo Web 1.2.0</footer><script src="./app.js"></script></body></html>',
  'app.js':"navigator.serviceWorker.register('./sw.js')",
  'sw.js':"const C='rumo-web-1.2.0';self.addEventListener('install',e=>e.waitUntil(caches.open(C).then(c=>c.addAll(['./','./index.html','./app.js']))));self.addEventListener('activate',e=>e.waitUntil(self.clients.claim()));self.addEventListener('fetch',e=>e.respondWith(caches.open(C).then(async c=>await c.match(e.request)||fetch(e.request))));"
 };
 const types={'.html':'text/html','.js':'text/javascript','.css':'text/css','.json':'application/json','.webmanifest':'application/manifest+json','.svg':'image/svg+xml','.png':'image/png'};
 const server=http.createServer((req,res)=>{const file=new URL(req.url,'http://localhost').pathname.slice(1)||'index.html';const target=path.resolve(dist,file);const data=old?fixture[file]:target.startsWith(dist+path.sep)&&fs.existsSync(target)?fs.readFileSync(target):null;if(!data){res.writeHead(404);res.end();return;}res.writeHead(200,{'Content-Type':types[path.extname(file)]||'application/octet-stream','Cache-Control':file==='sw.js'?'no-cache':'public, max-age=3600'});res.end(data);});
 await new Promise(r=>server.listen(0,'127.0.0.1',r));const browser=await chromium.launch({headless:true,executablePath:process.env.CHROME_PATH||undefined});
 try{const context=await browser.newContext(),page=await context.newPage();await page.goto('http://127.0.0.1:'+server.address().port+'/');await page.waitForFunction(()=>navigator.serviceWorker.controller);await page.reload();assert.match(await page.locator('footer').innerText(),/1\.2\.0/);
 old=false;await page.evaluate(async()=>{window.changed=false;navigator.serviceWorker.addEventListener('controllerchange',()=>window.changed=true);const r=await navigator.serviceWorker.getRegistration();await r.update();});await page.waitForFunction(()=>window.changed);
 const cached=await page.evaluate(async()=>{const c=await caches.open('rumo-web-1.3.2');return (await c.match(new URL('./index.html',location.href))).text();});assert.match(cached,/Rumo Web 1\.3\.2/);
 await page.reload();await page.getByRole('button',{name:/Começar meu plano/}).waitFor();assert.match(await page.locator('footer').innerText(),/1\.3\.2/);console.log('PASS atualização com cache HTTP antigo entrega HTML e módulos novos no reload.');
 }finally{await browser.close();await new Promise(r=>server.close(r));}
})().catch(e=>{console.error(e);process.exitCode=1;});
