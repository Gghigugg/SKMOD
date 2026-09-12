const toggles=[...document.querySelectorAll('[data-setting]')];
const defaults={notifications:true,dark:true};
function key(name){return `skmod_${name}`}
function read(name){const value=localStorage.getItem(key(name));return value===null?defaults[name]:value==='true'}
function setToggle(btn,state){btn.classList.toggle('active',state);btn.setAttribute('aria-pressed',String(state))}
function render(){toggles.forEach(btn=>setToggle(btn,read(btn.dataset.setting)))}
toggles.forEach(btn=>btn.addEventListener('click',()=>{const name=btn.dataset.setting;localStorage.setItem(key(name),String(!read(name)));render()}));
document.querySelector('#reset-settings')?.addEventListener('click',()=>{Object.keys(defaults).forEach(name=>localStorage.removeItem(key(name)));render()});
render();