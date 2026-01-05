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

const resourceSheetId = ref(getQueryParam('id'))

/* link with the API */
const resourceSheet = ref(null)
const resource = ref(null)
const ue = ref(null)
const institutions = ref(null)

// Function for return button
const goBack = () => {
  window.location.hash = '#/teacher_dashboard'
}

onMounted(async () => {
  /* get the specific resource sheet from the DB using the ID */
  if (resourceSheetId.value) {
    try {
      const response = await axios.get(`http://localhost:8080/api/resource-sheets/${resourceSheetId.value}/details`)
      resourceSheet.value = response.data
      console.log('ResourceSheet data with details:', resourceSheet.value)

      // Extract nested data directly from the resource sheet response
      if (resourceSheet.value.resource) {
        resource.value = resourceSheet.value.resource
        console.log('Resource data:', resource.value)

        // Extract UE from resource
        if (resource.value.ueCoefficient && resource.value.ueCoefficient.ue) {
          ue.value = resource.value.ueCoefficient.ue
          console.log('UE data:', ue.value)
        } else {
          console.log('No UE coefficient or UE found in resource')
        }
      } else {
        console.log('No resource found in resourceSheet')
      }

      // Extract institution from user
      if (resourceSheet.value.user && resourceSheet.value.user.institution) {
        institutions.value = resourceSheet.value.user.institution
        console.log('Institution data:', institutions.value)
      } else {
        console.log('No user or institution found in resourceSheet')
      }
    } catch (error) {
      console.error('Error fetching resource sheet:', error)
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
        panel.style.padding = "0 18px";
      } else {
        panel.style.padding = "2vw 18px";
        panel.style.maxHeight = panel.scrollHeight + "px";
      }
    });
  }

  // Auto-resize textareas (only those with specific classes)
  const textareas = document.querySelectorAll('#text_area_styled, .auto-resize-textarea');
  textareas.forEach(textarea => {
    const autoResize = () => {
      textarea.style.height = 'auto';
      textarea.style.height = (textarea.scrollHeight) + 'px';

      // Update parent panel height if inside an accordion
      const panel = textarea.closest('.panel');
      if (panel && panel.style.maxHeight) {
        panel.style.maxHeight = panel.scrollHeight + 'px';
      }
    };

    // Set initial height
    setTimeout(() => autoResize(), 0);

    // Add event listener for input
    textarea.addEventListener('input', autoResize);

    // Add event listener for when accordion opens
    const accordion = textarea.closest('#form')?.querySelector('.accordion');
    if (accordion) {
      accordion.addEventListener('click', () => {
        setTimeout(() => autoResize(), 250);
      });
    }
  });
})
</script>

<template>
  <div id="Ressource_Sheet">
    <div id="return_Arrow" @click="goBack">
      <button id="back_arrow">←</button>
      <p>Retour</p>
    </div>
    <div id="background_Form">
      <div class="header_Form">
        <p>Réf. UE : </p>
        <p>{{ ue?.label || '###' }}</p>
        <h2 class="title">{{ resource?.label || resourceSheet?.name || 'Nom de la ressource' }}</h2>
        <p>Dep : </p>
        <p>{{ institutions?.value || '###' }}</p>
      </div>
      <div class="ref_Section">
        <p>Réf. ressource : </p>
        <p>{{ resource?.label || '###' }}</p>
      </div>
      <div id="form">
        <button class="accordion" id="dark_Bar">Objectif de la ressource</button>
        <div class="panel">
          <textarea id="text_area_styled">Il faut manger beaucoup de pâtes pour être heureux</textarea>
        </div>
      </div>
      <div id="form">
        <button class="accordion" id="dark_Bar">Compétences *</button>
        <div class="panel">
          <textarea id="text_area_styled">zlkdjlzkez</textarea>
        </div>
      </div>
      <div>
        <p class="section_title">SAE concernée(s)* :</p>
        <label class="switch">
          <input type="checkbox">
          <span class="slider"></span>
          <span>SAE x</span>
        </label>
      </div>
      <div>
        <p class="subsection_title">Mots clés</p>
        <textarea class="auto-resize-textarea">mots clés...</textarea>
      </div>
      <div>
        <p class="subsection_title">Modalités de mise en oeuvre : </p>
        <textarea class="auto-resize-textarea">modalités...</textarea>
      </div>
      <div>
        <p class="section_title">Répartition de heures ( volume étudiant ) : </p>
        <p>CM</p>
        <textarea id="text_area_styled">1</textarea>
        <p>TD</p>
        <textarea id="text_area_styled">1</textarea>
        <p>TP</p>
        <textarea id="text_area_styled">1</textarea>
        <span>Le nombre total d'heure est .../...</span>
      </div>
      <div>
        <p class="section_title">Contenu pédagogique : </p>
      </div>
      <div>
        <p class="section_title">Suivi de la ressource / module</p>
        <div>
          <p>Retour de l'équipe pédagogique et des acteurs impactés</p>
          <textarea class="auto-resize-textarea"></textarea>
          <p>Retour des étudiants</p>
          <textarea class="auto-resize-textarea"></textarea>
          <p>Amélioration(s) à mettre en oeuvre</p>
          <textarea class="auto-resize-textarea"></textarea>
        </div>
      </div>
      <div>
        <button>Modifier</button>
        <button>Sauvegarder</button>
      </div>
    </div>
  </div>
</template>

<style>

#Ressource_Sheet{
  margin: 3vw 14vw;
  justify-content: center;
}

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

.panel {
  width: 90%;
  justify-self: center;
  padding: 0 18px;
  background-color: rgba(0,0,0,0.35);
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.2s ease-out, padding 0.2s ease-out;
  border-bottom-left-radius: 15px;
  border-bottom-right-radius: 15px;
  color: white;
  display: flex;
  flex-direction: column;
}

.panel p {
  margin-top: 0;
  padding-top: 1vw;
}

#return_Arrow{
  display: flex;
  align-items: center;
  cursor: pointer;
}

#return_Arrow > p{
  font-size: 1.5vw;
  font-weight: bold;
  color: black;
  margin-left: 1.5vw;
}

#back_arrow{
  font-size: 2vw;
  border: none;
  background-color: white;
  color: black;
  font-weight: bold;
}

#text_area_styled{
  width: 100%;
  min-height: 3em;
  border-radius: 15px;
  background-color: rgb(117, 117, 117);
  color: white;
  border: none;
  box-sizing: border-box;
  resize: none;
  padding: 1vw;
  overflow: hidden;
  overflow-wrap: break-word;
}

.auto-resize-textarea {
  overflow: hidden;
  resize: none;
  box-sizing: border-box;
  padding: 1vw;
  min-height: 3em;
  overflow-wrap: break-word;
}


#background_Form{
  height: auto;
  background-color: rgb(61, 67, 117);
  border-radius: 15px;
  overflow-x: hidden;
  overflow-y: auto;
  box-sizing: border-box;
  padding-bottom: 1vw;
  color: white;
}

.title{
  text-align: center;
  padding-top: 1vw;
  margin: 0;
  font-weight: lighter;
  font-size: 2.5vw;
}

#form{
  padding: 0 1vw;
  overflow: visible;
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

/* Header section styles*/
.header_Form{
  display: flex;
  gap: 1vw;
  padding: 1vw;
  justify-content: space-evenly;
}

.ref_Section {
  display: flex;
  gap: 0.5vw;
  padding: 0 2vw;
  margin-bottom: 1vw;
}

.ref_Section p {
  margin: 0;
}

/*main form styles*/
.section_title {
  font-size: 1.5vw;
  font-weight: bold;
  padding : 1vw 0 0.5vw 2vw;
}

.subsection_title {
  font-size: 1.2vw;
  font-weight: bold;
  padding : 0.5vw 0 0.5vw 2vw;
}

/*Toggle switch styles*/
.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 26px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
  border-radius: 26px;
}

.slider::before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}

/* ON state */
input:checked + .slider {
  background-color: #2C2C3B;
}

input:checked + .slider::before {
  transform: translateX(24px);
}
</style>
