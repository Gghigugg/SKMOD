const GALLERIES={
"OffxVauLt":{title:"Collection 001",media:[
{type:"image",src:"gallery/OffxVauLt/01.jpg"},{type:"image",src:"gallery/OffxVauLt/02.jpg"},{type:"video",src:"gallery/OffxVauLt/03.mp4"}]},
"AccessBySomesh":{title:"Collection 002",media:[
{type:"image",src:"gallery/AccessBySomesh/01.jpg"},{type:"video",src:"gallery/AccessBySomesh/02.mp4"}]},
"HaCkeR-sOmesh":{title:"Collection 003",media:[
{type:"image",src:"gallery/HaCkeR-sOmesh/01.jpg"}]},
"OffxVauLt-001":{title:"Offx Collection",media:[]},
"AccessBySomesh-001":{title:"Somesh Collection",media:[]},
"HaCkeR-sOmesh-001":{title:"Private Collection",media:[]}
};
const $=id=>document.getElementById(id), screens=["intro","home","loading","gallery","error","end"];
const show=id=>{screens.forEach(x=>$(x).classList.toggle("active",x===id));scrollTo(0,0)};
setTimeout(()=>show("home"),2600);
let current=null,index=0,touchX=0,timer=null;
$("form").addEventListener("submit",e=>{e.preventDefault();const raw=$("code").value.trim(),key=Object.keys(GALLERIES).find(k=>k.toLowerCase()===raw.toLowerCase());if(!key||!GALLERIES[key].media.length){show("error");return}current=GALLERIES[key];$("identity").value="";$("device").value="";$("code").value="";show("loading");const steps=[["Connecting to Vault...",12],["Verifying Code...",31],["Preparing Gallery...",55],["Loading Photos...",78],["Opening Vault...",100]];let s=0,p=0;clearInterval(timer);timer=setInterval(()=>{const [label,target]=steps[s];$("loadingText").textContent=label;p=Math.min(target,p+Math.max(1,Math.ceil((target-p)/4)));$("bar").style.width=p+"%";$("pct").textContent=p+"%";if(p>=target)s++;if(s>=steps.length){clearInterval(timer);setTimeout(render,250)}},70)});
function render(){$("galleryTitle").textContent=current.title;$("count").textContent="Photos & Videos • "+current.media.length;const grid=$("grid");grid.innerHTML="";current.media.forEach((m,i)=>{const b=document.createElement("button");b.className="card";b.type="button";if(m.type==="video"){const v=document.createElement("video");v.src=m.src;v.muted=true;v.preload="metadata";b.append(v);const p=document.createElement("span");p.className="play";p.textContent="▶";b.append(p)}else{const img=document.createElement("img");img.src=m.src;img.loading=i<6?"eager":"lazy";img.alt="Gallery item "+(i+1);b.append(img)}b.onclick=()=>openViewer(i);grid.append(b)});show("gallery")}
function openViewer(i){index=i;updateViewer();$("viewer").classList.add("open");$("viewer").setAttribute("aria-hidden","false")}
function closeViewer(){$("viewer").classList.remove("open");$("viewer").setAttribute("aria-hidden","true")}
function updateViewer(){const m=current.media[index],img=$("vimg");img.style.display=m.type==="image"?"block":"none";img.src=m.type==="image"?m.src:"";$("vcounter").textContent=(index+1)+" / "+current.media.length}
async function downloadCurrent(){const m=current?.media[index];if(!m)return;const a=document.createElement("a");a.href=m.src;a.download=m.src.split("/").pop()||"offxvault-media";a.rel="noopener";document.body.appendChild(a);a.click();a.remove()}
function move(d){index=(index+d+current.media.length)%current.media.length;updateViewer()}
$("retry").onclick=()=>show("home");$("back").onclick=()=>{current=null;show("home")};$("finish").onclick=()=>{current=null;show("end")};$("home").onclick=()=>show("home");$("close").onclick=closeViewer;$("download").onclick=downloadCurrent;$("prev").onclick=()=>move(-1);$("next").onclick=()=>move(1);$("viewer").addEventListener("click",e=>{if(e.target===$("viewer"))closeViewer()});document.addEventListener("keydown",e=>{if(!$("viewer").classList.contains("open"))return;if(e.key==="Escape")closeViewer();if(e.key==="ArrowLeft")move(-1);if(e.key==="ArrowRight")move(1)});$("viewer").addEventListener("touchstart",e=>touchX=e.changedTouches[0].clientX,{passive:true});$("viewer").addEventListener("touchend",e=>{const dx=e.changedTouches[0].clientX-touchX;if(Math.abs(dx)>45)move(dx<0?1:-1)},{passive:true});