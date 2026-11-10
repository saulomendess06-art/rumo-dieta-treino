const CACHE='rumo-web-1.3.2';
const FILES=["./","./index.html","./style.css","./app.js","./engine.js","./engine-core.js","./engine-v1.js","./personalized.js","./training.js","./store.js","./plan.json","./manifest.webmanifest","./icon.svg","./icon-180.png","./icon-192.png","./icon-512.png"];
self.addEventListener('install',event=>event.waitUntil((async()=>{
 const entries=await Promise.all(FILES.map(async file=>{
  const canonical=new URL(file,self.location.href),versioned=new URL(canonical);versioned.searchParams.set('rumo-release','1.3.2');
  const response=await fetch(new Request(versioned,{cache:'reload'}));
  if(!response.ok)throw Error('Recurso indisponível: '+file);
  if((file==='./'||file==='./index.html')&&!(await response.clone().text()).includes('Rumo Web 1.3.2'))throw Error('Publicação ainda propagando. Tente atualizar novamente.');
  return [canonical.href,response];
 }));
 const cache=await caches.open(CACHE);try{await Promise.all(entries.map(([url,response])=>cache.put(url,response)));}catch(error){await caches.delete(CACHE);throw error;}
 await self.skipWaiting();
})()));
self.addEventListener('activate',event=>event.waitUntil((async()=>{for(const key of await caches.keys())if(key.startsWith('rumo-web-')&&key!==CACHE)await caches.delete(key);await self.clients.claim();})()));
self.addEventListener('fetch',event=>{
 const url=new URL(event.request.url);if(event.request.method!=='GET'||url.origin!==self.location.origin)return;
 event.respondWith((async()=>{const cache=await caches.open(CACHE);const hit=await cache.match(event.request);if(hit)return hit;try{return await fetch(event.request);}catch(error){if(event.request.mode==='navigate'){const page=await cache.match('./index.html');if(page)return page;}throw error;}})());
});

self.addEventListener('message',event=>{if(event.data?.type==='RUMO_VERSION'&&event.ports[0])event.ports[0].postMessage({version:'1.3.2',cache:CACHE});});
