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
  <div id="Ressource_Sheet">
    <div id="return_Arrow">
      <button  id="backArrow" onclick="document.location.href='#/teacher_dashboard'">←</button>
      <p>Retour</p>
    </div>
    <div id="background_Form">
      <div class="header_Form">
        <p>Réf. UE : </p>
        <p>###</p>
        <h2>nom de la ressource</h2>
        <p>Dep : </p>
        <p>###</p>
      </div>
      <div class="ref_Section">
        <p>Réf. ressource : </p>
        <p>###</p>
      </div>
      <div id="form">
        <a class="accordion" id="dark_Bar" style="display: flex;">Compétences *</a>
        <div class="panel">

        </div>
      </div>
    </div>
  </div>
</template>

<style>

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
#Ressource_Sheet{
  margin: 3vw 14vw;
  justify-content: center;
}

#return_Arrow{
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

#return_Arrow > p{
  font-size: 1.5vw;
  font-weight: bold;
  color: black;
  margin-left: 1.5vw;
}

#background_Form{
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

#dark_Bar{
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

.header_Form{
  display: flex;
  gap: 1vw;
  padding: 1vw;
  color: white;
  justify-content: space-evenly;
}

.ref_Section {
  display: flex;
  gap: 0.5vw;
  color: white;
  padding: 0 2vw;
  margin-bottom: 1vw;
}

.ref_Section p {
  margin: 0;
}

</style>
