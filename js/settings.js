const toggles=[...document.querySelectorAll('[data-setting]')];
const defaults={notifications:true,dark:true,glassBlur:24};
function key(name){return `skmod_${name}`}
function read(name){const value=localStorage.getItem(key(name));return value===null?defaults[name]:value==='true'}
function readBlur(){const value=Number(localStorage.getItem(key('glassBlur')));return Number.isFinite(value)?Math.min(50,Math.max(0,value)):defaults.glassBlur}
function setToggle(btn,state){btn.classList.toggle('active',state);btn.setAttribute('aria-pressed',String(state))}
function applyAppearance(){const dark=read('dark');document.documentElement.dataset.skmodTheme=dark?'dark':'light';document.body.classList.toggle('skmod-light',!dark)}
function applyGlassBlur(value=readBlur()){document.documentElement.style.setProperty('--skmod-glass-blur',`${value}px`);const input=document.querySelector('#glass-blur');const label=document.querySelector('#glass-blur-value');if(input)input.value=value;if(label)label.textContent=`${value}px`}
function render(){toggles.forEach(btn=>setToggle(btn,read(btn.dataset.setting)));applyAppearance();applyGlassBlur()}
toggles.forEach(btn=>btn.addEventListener('click',()=>{const name=btn.dataset.setting;localStorage.setItem(key(name),String(!read(name)));render()}));
document.querySelector('#glass-blur')?.addEventListener('input',e=>{const value=Number(e.target.value);localStorage.setItem(key('glassBlur'),String(value));applyGlassBlur(value)});
document.querySelector('#reset-settings')?.addEventListener('click',()=>{Object.keys(defaults).forEach(name=>localStorage.removeItem(key(name)));render()});
const style=document.createElement('style');style.textContent='.skmod-light{background:#f4f5f8!important;color:#101218!important}.skmod-light .glass{background:rgba(255,255,255,.72)!important;border-color:rgba(0,0,0,.08)!important;box-shadow:0 20px 60px rgba(0,0,0,.08)!important}.skmod-light p,.skmod-light .setting-copy span,.skmod-light .settings-hero>span{color:#5c6473!important}.skmod-light .settings-page h1,.skmod-light .settings-page h2,.skmod-light .setting-copy strong,.skmod-light .settings-top strong{color:#101218}.skmod-light a{color:#101218}';document.head.appendChild(style);render();