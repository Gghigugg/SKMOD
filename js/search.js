const input=document.querySelector('#search');
const results=document.querySelector('#results');
const count=document.querySelector('#count');
const clear=document.querySelector('#clear');
const chips=[...document.querySelectorAll('[data-category]')];
let apps=[];let category='All';
const params=new URLSearchParams(location.search);if(input)input.value=(params.get('q')||'').trim();
function normalize(v){return String(v??'').toLocaleLowerCase().normalize('NFKD').replace(/[\u0300-\u036f]/g,'').trim()}
function escapeHtml(v){return String(v??'').replace(/[&<>\'"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]))}
function matchesCategory(a){if(category==='All')return true;if(category==='Trending')return Number(a.rating||0)>=4.7;if(category==='Latest')return true;return normalize(a.category)===normalize(category)}
function matchesQuery(a,q){if(!q)return true;return normalize([a.name,a.category,a.developer,a.version,a.description,a.modFeatures].join(' ')).includes(normalize(q))}
function card(a){const name=escapeHtml(a.name||'Unnamed App'),icon=escapeHtml(a.icon||'◈'),cat=escapeHtml(a.category||'Apps'),version=escapeHtml(a.version||'Latest'),size=escapeHtml(a.size||'—'),rating=escapeHtml(a.rating||'New'),url=`app-details.html?app=${encodeURIComponent(a.name||'')}`;return `<article class="result-card glass"><div class="app-symbol">${icon}</div><div class="app-copy"><h3>${name}</h3><p>${cat} · ${version} · ${size}</p><span>★ ${rating}</span></div><a href="${url}">View</a></article>`}
function render(){if(!results)return;const q=(input?.value||'').trim(),filtered=apps.filter(a=>matchesCategory(a)&&matchesQuery(a,q));results.innerHTML=filtered.length?filtered.map(card).join(''):`<div class="empty glass"><div>⌕</div><h3>No results found</h3><p>Try another app name, developer or category.</p></div>`;if(count)count.textContent=`${filtered.length} result${filtered.length===1?'':'s'}`}
async function init(){try{const r=await fetch('../data/apps.json',{cache:'no-store'});if(!r.ok)throw Error(`HTTP ${r.status}`);const data=await r.json();apps=Array.isArray(data)?data:[]}catch(e){console.error('SKMOD search data error:',e);apps=[];if(results)results.innerHTML='<div class="empty glass"><div>⚠</div><h3>Search is temporarily unavailable</h3><p>Please refresh and try again.</p></div>';if(count)count.textContent='0 results';return}render()}
input?.addEventListener('input',()=>{const q=input.value.trim(),url=new URL(location.href);if(q)url.searchParams.set('q',q);else url.searchParams.delete('q');history.replaceState({},'',url);render()});
clear?.addEventListener('click',()=>{if(!input)return;input.value='';const url=new URL(location.href);url.searchParams.delete('q');history.replaceState({},'',url);input.focus();render()});
chips.forEach(chip=>chip.addEventListener('click',()=>{chips.forEach(x=>x.classList.remove('active'));chip.classList.add('active');category=chip.dataset.category||'All';render()}));
init();
