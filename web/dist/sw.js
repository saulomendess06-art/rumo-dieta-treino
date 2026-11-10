const CACHE='rumo-web-1.3.0';
const FILES=["./","./index.html","./style.css","./app.js","./engine.js","./engine-core.js","./engine-v1.js","./personalized.js","./training.js","./store.js","./plan.json","./manifest.webmanifest","./icon.svg","./icon-180.png","./icon-192.png","./icon-512.png"];
self.addEventListener('install',event=>event.waitUntil(caches.open(CACHE).then(cache=>cache.addAll(FILES)).then(()=>self.skipWaiting())));
self.addEventListener('activate',event=>event.waitUntil((async()=>{for(const key of await caches.keys())if(key.startsWith('rumo-web-')&&key!==CACHE)await caches.delete(key);await self.clients.claim();})()));
self.addEventListener('fetch',event=>{
 const url=new URL(event.request.url);if(event.request.method!=='GET'||url.origin!==self.location.origin)return;
 event.respondWith((async()=>{const cache=await caches.open(CACHE);const hit=await cache.match(event.request);if(hit)return hit;try{return await fetch(event.request);}catch(error){if(event.request.mode==='navigate'){const page=await cache.match('./index.html');if(page)return page;}throw error;}})());
});
