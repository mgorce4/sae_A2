<script setup>
import axios from 'axios'
import { status } from '../main'
import { onMounted, ref, nextTick } from 'vue'

status.value = "Professeur"
/* link with the API */
const ressource_sheets = ref([])

onMounted(async () => {
  /* get of the value for the ressource sheets from the DB */
  try {
    const response = await axios.get('http://localhost:8080/api/ressource-sheets')
    ressource_sheets.value = response.data
  } catch (error) {
    console.error('Error fetching ressource sheets:', error)
  }

  // Wait for DOM to be fully rendered
  await nextTick()

  // Initialize accordion after DOM is ready
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
  <div id="RessourceSheet">
    <div id="returnArrow">
      <button  id="backArrow" onclick="document.location.href='#/teacher_dashboard'">←</button>
      <p>Retour</p>
    </div>
    <div id="backgroundForm">
      <div class="headerForm">
        <p>Réf. UE : </p>
        <p>###</p>
        <h2>nom de la ressource</h2>
        <p>Dep : </p>
        <p>###</p>
      </div>
      <div class="refSection">
        <p>Réf. ressource : </p>
        <p>###</p>
      </div>
      <div id="form">
        <a class="accordion" id="darkBar" style="display: flex;">Compétences *<button id="buttonMore">+</button></a>
        <div class="panel">
          <p>Lorem ipsum blablablablabalbalbalabalbalbalbalbalablabalbalbalablablablabalbalbalbalbalablablabalb</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
#RessourceSheet{
  margin: 3vw 14vw;
  justify-content: center;
}

#returnArrow{
  display: flex;
  align-items: center;
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

h2{
  color: white;
  text-align: center;
  padding-top: 1vw;
  margin: 0;
  font-weight: lighter;
  font-size: 2.5vw;
}

#form{
  padding: 0 1vw;
  overflow: hidden;
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

.headerForm{
  display: flex;
  gap: 1vw;
  padding: 1vw;
  color: white;
  justify-content: space-evenly;
}

.refSection {
  display: flex;
  gap: 0.5vw;
  color: white;
  padding: 0 2vw;
  margin-bottom: 1vw;
}

.refSection p {
  margin: 0;
}

</style>
