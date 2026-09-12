(() => {
  const nav = document.querySelector('.bottom-nav');
  if (!nav) return;
  const items = [...nav.querySelectorAll('a')];
  if (!items.length) return;
  let indicator = nav.querySelector('.liquid-indicator');
  if (!indicator) { indicator = document.createElement('div'); indicator.className = 'liquid-indicator'; indicator.setAttribute('aria-hidden','true'); nav.appendChild(indicator); }
  const activeIndex = Math.max(0, items.findIndex(a => a.classList.contains('active') || a.getAttribute('aria-current') === 'page'));
  let currentIndex = activeIndex, dragging = false, moved = false, dragStartX = 0, startLeft = 0;
  const metrics = () => { const n=nav.getBoundingClientRect(), r=items[currentIndex].getBoundingClientRect(); return {n,left:r.left-n.left,width:r.width}; };
  const place = (index, animate=true) => { currentIndex=Math.max(0,Math.min(items.length-1,index)); const r=metrics(); indicator.style.transition=animate?'':'none'; indicator.style.left=`${r.left}px`; indicator.style.width=`${r.width}px`; indicator.style.transform='translateZ(0) scaleX(1)'; indicator.style.borderRadius=''; if(!animate) requestAnimationFrame(()=>indicator.style.transition=''); };
  const nearest = x => { const n=nav.getBoundingClientRect(); let best=0,distance=Infinity; items.forEach((item,i)=>{const r=item.getBoundingClientRect(),c=r.left-n.left+r.width/2,d=Math.abs(x-c);if(d<distance){distance=d;best=i;}}); return best; };
  const finish = index => { dragging=false; indicator.classList.remove('dragging'); place(index,true); const href=items[index].getAttribute('href'); if(href) setTimeout(()=>{window.location.href=href;},120); };
  indicator.addEventListener('pointerdown',e=>{dragging=true;moved=false;indicator.classList.add('dragging');indicator.setPointerCapture?.(e.pointerId);dragStartX=e.clientX;startLeft=parseFloat(indicator.style.left)||metrics().left;indicator.style.transform='translateZ(0) scaleX(1.03)';e.preventDefault();});
  indicator.addEventListener('pointermove',e=>{if(!dragging)return;const n=nav.getBoundingClientRect(),r=metrics(),min=6,max=n.width-r.width-6,dx=e.clientX-dragStartX,next=Math.max(min,Math.min(max,startLeft+dx));indicator.style.transition='none';indicator.style.left=`${next}px`;const velocity=Math.min(1.35,Math.max(1,1+Math.abs(dx)/(Math.max(1,n.width))*0.9));indicator.style.transform=`translateZ(0) scaleX(${velocity}) scaleY(${Math.max(.94,2-velocity)})`;indicator.style.borderRadius=`${20+Math.min(8,Math.abs(dx)/10)}px`;moved=true;});
  const release=e=>{if(!dragging)return;const r=indicator.getBoundingClientRect(),n=nav.getBoundingClientRect();finish(nearest(r.left-n.left+r.width/2));try{indicator.releasePointerCapture?.(e.pointerId);}catch(_) {}};
  indicator.addEventListener('pointerup',release);indicator.addEventListener('pointercancel',release);indicator.addEventListener('lostpointercapture',()=>{if(dragging){const r=indicator.getBoundingClientRect(),n=nav.getBoundingClientRect();finish(nearest(r.left-n.left+r.width/2));}});
  items.forEach((item,index)=>item.addEventListener('click',e=>{if(dragging||moved){e.preventDefault();moved=false;return;}place(index,true);}));
  window.addEventListener('resize',()=>place(currentIndex,false));
  requestAnimationFrame(()=>place(currentIndex,false));
})();