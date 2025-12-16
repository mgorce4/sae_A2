<script setup>
    import { status } from '../main'
    import { onMounted } from 'vue'
    
    status.value = "Administration"
    
    onMounted(() => {
        const acc = document.getElementsByClassName("accordion");
        for (let i = 0; i < acc.length; i++) {
            acc[i].addEventListener("click", function() {
                this.classList.toggle("active");
                const panel = this.nextElementSibling;
                if (panel.style.maxHeight) {
                    panel.style.maxHeight = null;
                } else {
                    panel.style.maxHeight = panel.scrollHeight + "px";
                }
            });
        }
    })
</script>

<template>
    <div id="UE"> 
        <div id="returnArrow">
            <button  id="backArrow" onclick="document.location.href='#/mccc-select-form'">←</button>
            <p>Retour</p>
        </div>
        <div id="backgroundForm">
            <div id="form">
                <div id="headerUE">
                    <p id="title">Unités d’enseignement</p>
                </div>
                <div id="darkBar">
                    <h2>Ajouter une UE</h2>
                    <button id="buttonMore">+</button>
                </div>
                <a class="accordion" id="darkBar">Ajout d'une UE :</a>
                <div class="panel">
                    <div>
                        <p>Numéro de l'UE</p>
                        <v-text-field></v-text-field>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style>
#UE{
  margin: 3vw 14vw;
  justify-content: center;
}

#returnArrow{
    display: flex;
    align-items: center;
}

#returnArrow > p{
    font-size: 1.5vw;
    font-weight: bold;
    color: black;
    margin-left: 1.5vw;
}

#backgroundForm{
  height: auto;
  background-color: rgb(61, 67, 117);
  border-radius: 15px;
  overflow-x: hidden;
  overflow-y: hidden;
  box-sizing: border-box;
}

#headerUE{
    background-color: rgb(44,49,88); 
    height: auto;
    border-radius: 10px;
    margin: 1vw;
}

#title{
    color: white;
    text-align: center;
    padding-top: 0.5vw;
    padding-bottom: 0.5vw;
    font-weight: lighter;
    font-size: 2.3vw;
}

.accordion, h2{
    margin: 0vw;
    font-weight: lighter;
    font-size: 1.05vw;
}

.accordion {
    cursor: pointer;
    position: relative;
    padding-right: 2.5vw;
}

.accordion::after {
    content: '^';
    position: absolute;
    right: 1vw;
    transition: transform 0.3s ease;
    font-size: 0.9vw;
}

.accordion.active::after {
    transform: rotate(180deg);
}

#form{
  padding: 0 1vw;
  overflow: hidden;
}

#form::-webkit-scrollbar {
    width: 12px;
}

#form::-webkit-scrollbar-track {
    margin: 1em;
    background: rgb(42,45,86);
    box-shadow: inset 0 0 5px rgb(24, 26, 50);
    border-radius: 10px;
}

#form::-webkit-scrollbar-thumb {
    background: rgb(254,254,254);
    border-radius: 10px;
}

#darkBar{
    color: white;
    height: auto;
    border-radius: 10px;
    margin: 1vw 0 0 0;
    padding: 1vw;
    background-color: rgb(32,32,32);
    display: flex;
    justify-content: space-between;
    align-items: center;
}

#buttonMore{
    border: 2px solid white;
    background-color: rgb(32,32,32);
    height: 2vw;
    width: 2vw;
    border-radius: 50%;
    font-size: 1.2vw;
    font-weight: bold;
    color: white;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    transition: all 0.3s ease;
}

#buttonMore:hover {
  background-color: rgb(50,50,50);
  transform: scale(1.1);
}

.panel {
  width: 90%;
  justify-self: center;
  padding: 0 18px;
  background-color: rgba(0,0,0,0.35);
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.2s ease-out;
  border-bottom-left-radius: 15px;
  border-bottom-right-radius: 15px;
  color: white;
  margin-top: 0;
}

.panel p {
  margin-top: 0;
  padding-top: 1vw;
}

</style>