<?php
// step_metronome.php - Степ-метроном на PHP (веб-сервер)
// Отдаёт страницу с HTML+JavaScript
?>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>🎵 StepMetronome - PHP</title>
    <style>
        *{box-sizing:border-box;margin:0;user-select:none;}body{background:#1e2a3a;display:flex;justify-content:center;align-items:center;min-height:100vh;font-family:'Segoe UI',system-ui;padding:20px;}
        .container{background:#2c3e50;border-radius:24px;padding:30px;box-shadow:0 20px 40px rgba(0,0,0,0.4);width:500px;text-align:center;}
        h1{color:#ecf0f1;font-size:28px;margin-bottom:10px;}
        .bpm-display{font-size:56px;font-weight:bold;color:#f1c40f;font-family:'Courier New',monospace;margin:10px 0;}
        .bpm-slider{width:100%;accent-color:#3498db;height:8px;border-radius:4px;}
        .bpm-buttons{display:flex;gap:8px;justify-content:center;margin:15px 0;}
        .bpm-buttons button{padding:6px 14px;border:none;border-radius:6px;background:#34495e;color:white;cursor:pointer;font-size:14px;}
        .bpm-buttons button:hover{background:#3d566e;}
        .indicator{width:120px;height:120px;margin:20px auto;border-radius:50%;background:#34495e;border:4px solid #7f8c8d;transition:background 0.1s;}
        .indicator.active-strong{background:#f1c40f;border-color:#f1c40f;}
        .indicator.active-weak{background:#2ecc71;border-color:#2ecc71;}
        .controls{display:flex;gap:15px;justify-content:center;margin:20px 0;}
        .controls button{padding:10px 30px;border:none;border-radius:30px;font-size:16px;font-weight:bold;cursor:pointer;transition:0.1s;}
        .btn-start{background:#2ecc71;color:white;}
        .btn-start.active{background:#f39c12;}
        .btn-stop{background:#e74c3c;color:white;}
        .btn-stop:disabled{opacity:0.4;cursor:not-allowed;}
        .status{color:#bdc3c7;font-size:14px;margin-top:10px;}
        .accent-check{color:white;margin-top:10px;display:flex;align-items:center;justify-content:center;gap:8px;}
        .accent-check input{width:18px;height:18px;cursor:pointer;}
    </style>
</head>
<body>
<div class="container">
    <h1>🎵 Степ-метроном</h1>
    <div class="bpm-display" id="bpmDisplay">120 BPM</div>
    <input type="range" class="bpm-slider" id="bpmSlider" min="30" max="240" value="120">
    <div class="bpm-buttons">
        <button id="bpmMinus10">-10</button>
        <button id="bpmMinus1">-1</button>
        <button id="bpmPlus1">+1</button>
        <button id="bpmPlus10">+10</button>
    </div>
    <div class="indicator" id="indicator"></div>
    <div class="controls">
        <button class="btn-start" id="startBtn">▶ Старт</button>
        <button class="btn-stop" id="stopBtn" disabled>⏹ Стоп</button>
    </div>
    <div class="accent-check">
        <label for="accentCheck">Акцент на первой доле</label>
        <input type="checkbox" id="accentCheck" checked>
    </div>
    <div class="status" id="statusMsg">Готов</div>
</div>
<script>
class Metronome {
constructor(){this.bpm=120;this.running=false;this.interval=60/this.bpm;this.timerId=null;this.beatCount=0;this.accent=true;this.audioCtx=null;
this.bpmDisplay=document.getElementById('bpmDisplay');this.bpmSlider=document.getElementById('bpmSlider');this.indicator=document.getElementById('indicator');
this.startBtn=document.getElementById('startBtn');this.stopBtn=document.getElementById('stopBtn');this.statusMsg=document.getElementById('statusMsg');
this.accentCheck=document.getElementById('accentCheck');this.initEvents();this.updateBpmDisplay();}
initEvents(){
this.bpmSlider.addEventListener('input',()=>{this.bpm=parseInt(this.bpmSlider.value);this.interval=60/this.bpm;this.updateBpmDisplay();});
document.getElementById('bpmMinus10').addEventListener('click',()=>this.changeBpm(-10));
document.getElementById('bpmMinus1').addEventListener('click',()=>this.changeBpm(-1));
document.getElementById('bpmPlus1').addEventListener('click',()=>this.changeBpm(1));
document.getElementById('bpmPlus10').addEventListener('click',()=>this.changeBpm(10));
this.startBtn.addEventListener('click',()=>this.toggleStartStop());
this.stopBtn.addEventListener('click',()=>this.stop());
this.accentCheck.addEventListener('change',()=>{this.accent=this.accentCheck.checked;});
document.addEventListener('keydown',(e)=>{if(e.key===' '){e.preventDefault();this.toggleStartStop();}if(e.key==='ArrowUp'){e.preventDefault();this.changeBpm(1);}if(e.key==='ArrowDown'){e.preventDefault();this.changeBpm(-1);}});
this.audioCtx=new(window.AudioContext||window.webkitAudioContext)();}
changeBpm(delta){let newBpm=Math.max(30,Math.min(240,this.bpm+delta));this.bpmSlider.value=newBpm;this.bpm=newBpm;this.interval=60/this.bpm;this.updateBpmDisplay();if(this.running){this.stop();this.start();}}
updateBpmDisplay(){this.bpmDisplay.textContent=this.bpm+' BPM';}
playSound(accent){try{const osc=this.audioCtx.createOscillator();const gain=this.audioCtx.createGain();osc.connect(gain);gain.connect(this.audioCtx.destination);osc.type='sine';osc.frequency.value=accent?880:660;gain.gain.setValueAtTime(0.3,this.audioCtx.currentTime);gain.gain.exponentialRampToValueAtTime(0.001,this.audioCtx.currentTime+0.08);osc.start();osc.stop(this.audioCtx.currentTime+0.08);}catch(e){}}
flashIndicator(accent){this.indicator.className='indicator';void this.indicator.offsetWidth;this.indicator.classList.add(accent?'active-strong':'active-weak');setTimeout(()=>{this.indicator.className='indicator';},100);}
beat(){const isAccent=this.accent&&(this.beatCount%4===0);this.flashIndicator(isAccent);this.playSound(isAccent);this.beatCount++;}
tick(){if(!this.running)return;this.beat();this.timerId=setTimeout(()=>this.tick(),this.interval*1000);}
start(){if(this.running)return;this.running=true;this.beatCount=0;this.startBtn.textContent='⏸ Пауза';this.startBtn.classList.add('active');this.stopBtn.disabled=false;this.statusMsg.textContent='Идёт...';this.tick();}
stop(){this.running=false;if(this.timerId){clearTimeout(this.timerId);this.timerId=null;}this.startBtn.textContent='▶ Старт';this.startBtn.classList.remove('active');this.stopBtn.disabled=true;this.statusMsg.textContent='Остановлено';this.indicator.className='indicator';}
toggleStartStop(){if(this.running){this.stop();}else{this.start();}}
}
document.addEventListener('DOMContentLoaded',()=>{new Metronome();});
</script>
</body>
</html>
