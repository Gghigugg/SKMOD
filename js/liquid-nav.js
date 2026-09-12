(() => {
  const nav = document.querySelector('.bottom-nav');
  if (!nav) return;
  const items = [...nav.querySelectorAll('a')];
  if (!items.length) return;
  let indicator = nav.querySelector('.liquid-indicator');
  if (!indicator) { indicator = document.createElement('div'); indicator.className = 'liquid-indicator'; indicator.setAttribute('aria-hidden','true'); nav.appendChild(indicator); }

  const activeIndex = Math.max(0, items.findIndex(a => a.classList.contains('active') || a.getAttribute('aria-current') === 'page'));
  let currentIndex = activeIndex;
  let dragging = false;
  let moved = false;
  let pointerId = null;
  let dragStartX = 0;
  let startLeft = 0;
  let lastX = 0;
  let lastTime = 0;

  const metrics = index => {
    const n = nav.getBoundingClientRect();
    const r = items[index].getBoundingClientRect();
    return { n, left:r.left-n.left, width:r.width };
  };

  const place = (index, animate=true) => {
    currentIndex = Math.max(0, Math.min(items.length-1, index));
    const r = metrics(currentIndex);
    indicator.style.transition = animate ? '' : 'none';
    indicator.style.left = `${r.left}px`;
    indicator.style.width = `${r.width}px`;
    indicator.style.transform = 'translate3d(0,0,0) scaleX(1) scaleY(1)';
    indicator.style.borderRadius = '';
    if (!animate) requestAnimationFrame(() => indicator.style.transition = '');
  };

  const nearest = x => {
    const n = nav.getBoundingClientRect();
    let best = 0, distance = Infinity;
    items.forEach((item,i) => {
      const r = item.getBoundingClientRect();
      const center = r.left - n.left + r.width/2;
      const d = Math.abs(x-center);
      if (d < distance) { distance=d; best=i; }
    });
    return best;
  };

  const finish = index => {
    dragging = false;
    indicator.classList.remove('dragging');
    const r = metrics(index);
    indicator.style.transition = 'left .5s cubic-bezier(.18,.88,.2,1),width .25s ease,transform .28s ease,border-radius .22s ease';
    indicator.style.left = `${r.left}px`;
    indicator.style.width = `${r.width}px`;
    indicator.style.transform = 'translate3d(0,0,0) scaleX(1) scaleY(1)';
    indicator.style.borderRadius = '';
    currentIndex = index;
    const href = items[index].getAttribute('href');
    if (href) setTimeout(() => { window.location.href = href; }, 180);
  };

  indicator.addEventListener('pointerdown', e => {
    if (e.pointerType === 'mouse' && e.button !== 0) return;
    dragging = true;
    moved = false;
    pointerId = e.pointerId;
    indicator.classList.add('dragging');
    try { indicator.setPointerCapture(e.pointerId); } catch(_) {}
    dragStartX = e.clientX;
    lastX = e.clientX;
    lastTime = performance.now();
    startLeft = parseFloat(indicator.style.left) || metrics(currentIndex).left;
    indicator.style.transition = 'none';
    e.preventDefault();
    e.stopPropagation();
  }, {passive:false});

  indicator.addEventListener('pointermove', e => {
    if (!dragging || e.pointerId !== pointerId) return;
    const n = nav.getBoundingClientRect();
    const r = metrics(currentIndex);
    const dx = e.clientX - dragStartX;
    const min = 5;
    const max = n.width - r.width - 5;
    const next = Math.max(min, Math.min(max, startLeft + dx));
    const now = performance.now();
    const dt = Math.max(8, now-lastTime);
    const velocity = Math.min(2.5, Math.abs(e.clientX-lastX)/(dt/16.67));
    const stretch = Math.min(1.18, 1 + velocity*.045 + Math.abs(dx)/Math.max(1,n.width)*.12);
    const squash = Math.max(.82, 2-stretch);
    indicator.style.left = `${next}px`;
    indicator.style.transform = `translate3d(0,0,0) scaleX(${stretch}) scaleY(${squash})`;
    indicator.style.borderRadius = `${20 + Math.min(12, Math.abs(dx)/7)}px`;
    moved = Math.abs(dx) > 5;
    lastX = e.clientX;
    lastTime = now;
    e.preventDefault();
  }, {passive:false});

  const release = e => {
    if (!dragging || (pointerId !== null && e.pointerId !== pointerId)) return;
    const r = indicator.getBoundingClientRect();
    const n = nav.getBoundingClientRect();
    const target = nearest(r.left-n.left+r.width/2);
    try { indicator.releasePointerCapture(e.pointerId); } catch(_) {}
    pointerId = null;
    finish(target);
    e.preventDefault();
  };

  indicator.addEventListener('pointerup', release, {passive:false});
  indicator.addEventListener('pointercancel', release, {passive:false});
  indicator.addEventListener('lostpointercapture', () => {
    if (!dragging) return;
    const r = indicator.getBoundingClientRect();
    const n = nav.getBoundingClientRect();
    finish(nearest(r.left-n.left+r.width/2));
    pointerId = null;
  });

  items.forEach((item,index) => item.addEventListener('click', e => {
    if (dragging || moved) {
      e.preventDefault();
      moved = false;
      return;
    }
    place(index,true);
  }));

  window.addEventListener('resize', () => place(currentIndex,false));
  requestAnimationFrame(() => place(currentIndex,false));
})();