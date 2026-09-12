const input=document.querySelector('#search');
const results=document.querySelector('#results');
const count=document.querySelector('#count');
const clear=document.querySelector('#clear');
const chips=[...document.querySelectorAll('[data-category]')];
let apps=[];let category='All';
const params=new URLSearchParams(location.search);if(input)input.value=params.get('q')||'';
function card(a){return `<article class="result-card glass"><div class="app-symbol">${a.icon||'◈'}</div><div class="app-copy"><h3>${a.name}</h3><p>${a.category||'Apps'} · ${a.version||'Latest'} · ${a.size||'—'}</p><span>★ ${a.rating||'New'}</span></div><a href="app-details.html?app=${encodeURIComponent(a.name)}">View</a></article>`}
function render(){const q=(input?.value||'').trim().toLowerCase();const filtered=apps.filter(a=>(category==='All'||a.category===category)&&(!q||`${a.name} ${a.category||''} ${a.developer||''}`.toLowerCase().includes(q)));results.innerHTML=filtered.length?filtered.map(card).join(''):`<div class="empty glass"><div>⌕</div><h3>No results found</h3><p>Try another app name or category.</p></div>`;if(count)count.textContent=`${filtered.length} result${filtered.length===1?'':'s'}`}
async function init(){try{const r=await fetch('../data/apps.json',{cache:'no-store'});if(!r.ok)throw Error();apps=await r.json()}catch(e){apps=[]}render()}
input?.addEventListener('input',render);clear?.addEventListener('click',()=>{input.value='';input.focus();history.replaceState({},'',location.pathname);render()});chips.forEach(c=>c.addEventListener('click',()=>{chips.forEach(x=>x.classList.remove('active'));c.classList.add('active');category=c.dataset.category;render()}));init();