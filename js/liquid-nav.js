(() => {
  const nav = document.querySelector('.bottom-nav');
  if (!nav) return;
  const items = [...nav.querySelectorAll('a')];
  if (!items.length) return;

  let indicator = nav.querySelector('.liquid-indicator');
  if (!indicator) {
    indicator = document.createElement('div');
    indicator.className = 'liquid-indicator';
    indicator.setAttribute('aria-hidden', 'true');
    nav.appendChild(indicator);
  }

  const activeIndex = Math.max(0, items.findIndex(a => a.classList.contains('active') || a.getAttribute('aria-current') === 'page'));
  let currentIndex = activeIndex;
  let dragStartX = 0;
  let startLeft = 0;
  let dragging = false;
  let moved = false;

  const metrics = () => {
    const navRect = nav.getBoundingClientRect();
    const first = items[0].getBoundingClientRect();
    const current = items[currentIndex].getBoundingClientRect();
    return {
      navRect,
      left: current.left - navRect.left,
      width: current.width,
      firstLeft: first.left - navRect.left
    };
  };

  const place = (index, animate = true) => {
    currentIndex = Math.max(0, Math.min(items.length - 1, index));
    const r = metrics();
    indicator.style.transition = animate ? '' : 'none';
    indicator.style.left = `${r.left}px`;
    indicator.style.width = `${r.width}px`;
    indicator.style.transform = 'translateZ(0) scaleX(1)';
    requestAnimationFrame(() => {
      if (!animate) indicator.style.transition = '';
    });
  };

  const nearest = x => {
    const navRect = nav.getBoundingClientRect();
    let best = 0;
    let distance = Infinity;
    items.forEach((item, index) => {
      const rect = item.getBoundingClientRect();
      const center = rect.left - navRect.left + rect.width / 2;
      const d = Math.abs(x - center);
      if (d < distance) { distance = d; best = index; }
    });
    return best;
  };

  const finish = index => {
    dragging = false;
    moved = true;
    indicator.classList.remove('dragging');
    place(index, true);
    const href = items[index].getAttribute('href');
    if (href) setTimeout(() => { window.location.href = href; }, 90);
  };

  indicator.addEventListener('pointerdown', event => {
    dragging = true;
    moved = false;
    indicator.classList.add('dragging');
    indicator.setPointerCapture?.(event.pointerId);
    dragStartX = event.clientX;
    startLeft = parseFloat(indicator.style.left) || metrics().left;
    event.preventDefault();
  });

  indicator.addEventListener('pointermove', event => {
    if (!dragging) return;
    const navRect = nav.getBoundingClientRect();
    const r = metrics();
    const min = 6;
    const max = navRect.width - r.width - 6;
    const nextLeft = Math.max(min, Math.min(max, startLeft + event.clientX - dragStartX));
    indicator.style.transition = 'none';
    indicator.style.left = `${nextLeft}px`;
    indicator.style.transform = 'translateZ(0) scaleX(1.12)';
    moved = true;
  });

  const release = event => {
    if (!dragging) return;
    const navRect = nav.getBoundingClientRect();
    const rect = indicator.getBoundingClientRect();
    const center = rect.left - navRect.left + rect.width / 2;
    finish(nearest(center));
    try { indicator.releasePointerCapture?.(event.pointerId); } catch (_) {}
  };

  indicator.addEventListener('pointerup', release);
  indicator.addEventListener('pointercancel', release);
  indicator.addEventListener('lostpointercapture', () => {
    if (dragging) finish(nearest(indicator.getBoundingClientRect().left - nav.getBoundingClientRect().left + indicator.getBoundingClientRect().width / 2));
  });

  items.forEach((item, index) => {
    item.addEventListener('click', event => {
      if (dragging || moved) {
        event.preventDefault();
        moved = false;
        return;
      }
      place(index, true);
    });
  });

  window.addEventListener('resize', () => place(currentIndex, false));
  requestAnimationFrame(() => place(currentIndex, false));
})();