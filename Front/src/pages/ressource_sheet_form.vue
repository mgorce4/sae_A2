<script setup>
import axios from 'axios'
import { status } from '../main'
import { onMounted, ref, nextTick } from 'vue'

status.value = "Professeur"

/* Extract ID from hash URL parameters */
const getQueryParam = (param) => {
  const hash = window.location.hash
  const queryString = hash.split('?')[1]
  if (!queryString) return null
  const params = new URLSearchParams(queryString)
  return params.get(param)
}

const ressourceSheetId = ref(getQueryParam('id'))

/* link with the API */
const ressourceSheet = ref(null)
const ressource = ref(null)
const ue = ref(null)

onMounted(async () => {
  /* get the specific ressource sheet from the DB using the ID */
  if (ressourceSheetId.value) {
    try {
      const response = await axios.get(`http://localhost:8080/api/ressource-sheets/${ressourceSheetId.value}`)
      ressourceSheet.value = response.data

      // If the ressource sheet has a linked ressource, fetch its details
      if (ressourceSheet.value.ressource) {
        const ressourceResponse = await axios.get(`http://localhost:8080/api/ressources/${ressourceSheet.value.ressource.idRessource}`)
        ressource.value = ressourceResponse.data

        // If the ressource has a UE coefficient, fetch the UE details
        if (ressource.value.ueCoefficient && ressource.value.ueCoefficient.ue) {
          const ueResponse = await axios.get(`http://localhost:8080/api/eu/${ressource.value.ueCoefficient.ue.ueNumber}`)
          ue.value = ueResponse.data
        }
      }
    } catch (error) {
      console.error('Error fetching ressource sheet:', error)
    }
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
        <p>{{ ue?.label || '###' }}</p>
        <p class="title" >{{ ressourceSheet?.name || 'Nom de la ressource sheet' }}</p>
        <p>Dep : </p>
        <p>{{ ressource?.apogeeCode || '###' }}</p>
      </div>
      <div class="ref_Section">
        <p>Réf. ressource : </p>
        <p>{{ ressource?.label || '###' }}</p>
      </div>
      <div id="form">
        <button class="accordion" id="dark_Bar">Compétences *</button>
        <div class="panel">
          <p>{{ ressourceSheet?.competence || 'Aucune compétence renseignée' }}</p>
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
  border: none;
  outline: none;
  text-align: left;
  width: 100%;
  font-family: inherit;
  font-size: inherit;
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
  padding-bottom: 1vw;
}

.title{
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
