<script setup>
/* import */
import { ref } from 'vue'
import { status, institutionLocation } from '../main'
import { onMounted } from 'vue'
import axios from 'axios'

/* constantes */

status.value = 'Administration'
institutionLocation.value = localStorage.institutionLocation



const list_semesters = [1, 2, 3, 4, 5, 6]

/* we can use selected_semester to get the semester*/
let selected_semester_sheets = ref(list_semesters[0])

/* link with the API */

const resource_sheets = ref([])
const firstDeliveryDate = ref('')
const secondDeliveryDate = ref('')
const deliveryDatesId = ref(null)
const selectedSheets = ref([]) // Pour stocker les IDs des fiches sélectionnées

onMounted(async () => {
    try {
        const response = await axios.get('http://localhost:8080/api/v2/resource-sheets')
        resource_sheets.value = response.data
    } catch (error) {
        console.error('Error loading resource sheets:', error)
        resource_sheets.value = []
    }

    // Load delivery dates for the current institution
    try {
        const institutionId = localStorage.idInstitution
        if (institutionId) {
            const datesResponse = await axios.get(`http://localhost:8080/api/final-delivery-dates/institution/${institutionId}`)
            if (datesResponse.data) {
                deliveryDatesId.value = datesResponse.data.idFinalDelivery
                firstDeliveryDate.value = datesResponse.data.firstDelivery || ''
                secondDeliveryDate.value = datesResponse.data.secondDelivery || ''
            }
        }
    } catch (error) {
        // 404 is normal if no dates exist yet for this institution
        if (error.response?.status !== 404) {
            console.error('Error loading delivery dates:', error)
        }
    }
})

function getResourcesForSemester(semester) {
  return resource_sheets.value.filter((sheet) => sheet.semester === semester)
                              .filter((sheet) => sheet.institutionId == localStorage.idInstitution)
}

async function saveDeliveryDates() {
  try {
    const institutionId = localStorage.idInstitution
    if (!institutionId) {
      console.error('Institution non trouvée')
      return
    }

    const deliveryDatesData = {
      firstDelivery: firstDeliveryDate.value,
      secondDelivery: secondDeliveryDate.value,
      institution: {
        idInstitution: parseInt(institutionId)
      }
    }

    // Use the save-by-institution endpoint which automatically handles create or update
    const response = await axios.post('http://localhost:8080/api/final-delivery-dates/save-by-institution', deliveryDatesData)
    deliveryDatesId.value = response.data.idFinalDelivery

    // Reload the page to show the updated dates
    window.location.reload()
  } catch (error) {
    console.error('Error saving delivery dates:', error)
  }
}

function toggleSheetSelection(sheetId) {
  const index = selectedSheets.value.indexOf(sheetId)
  if (index > -1) {
    selectedSheets.value.splice(index, 1)
  } else {
    selectedSheets.value.push(sheetId)
  }
}

function isSheetSelected(sheetId) {
  return selectedSheets.value.includes(sheetId)
}

async function downloadSheets() {
  if (selectedSheets.value.length === 0) {
    console.log('Aucune fiche sélectionnée')
    return
  }

  const userName = localStorage.username || 'user'

  try {
    // Download each selected sheet
    for (const sheetId of selectedSheets.value) {
      const sheet = resource_sheets.value.find(s => s.id === sheetId)
      if (sheet && sheet.resourceLabel) {
        // Call the PDF generation endpoint
        const response = await axios.get('http://localhost:8080/api/pdf/generate', {
          params: {
            resourceName: sheet.resourceLabel,
            userName: userName
          },
          responseType: 'blob'
        })

        // Create a download link
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `${sheet.resourceLabel}_ressource_sheet.pdf`)
        document.body.appendChild(link)
        link.click()
        link.remove()
        window.URL.revokeObjectURL(url)
      }
    }
  } catch (error) {
    console.error('Error downloading PDFs:', error)
  }
}

const goToRessourceSheetDisplay = (url, label) => {
  window.location.hash = `${url}?label=${label}`
}

</script>

<template>
  <div id="main_div">
    <div id="sub_div_for_MCCC_and_calender">
      <div id="MCCC_div">
        <!-- link into MCCC page -->
        <button type="button" id="MCCC_button" onclick="document.location.href='#/mccc-select-path'">MCCC</button>
      </div>
      <div id="date_selector_div">
        <p id="title_font">Dates de rendu des fiches ressources</p>
        <div id="inline">
          <p>Date de rendu du 1er semestre</p>
          <input type="date" v-model="firstDeliveryDate" name="first-delivery" :min="new Date().toISOString().split('T')[0]" />
        </div>
        <div id="inline">
          <p>Date de rendu du 2ème semestre</p>
          <input type="date" v-model="secondDeliveryDate" name="second-delivery" :min="new Date().toISOString().split('T')[0]" />
        </div>
        <button class="btn1" @click="saveDeliveryDates">Valider</button>
      </div>

    </div>

    <div id="return_sheets_div">
      <div id="return_sheets_div_header">
        <div id="top">
          <p>Rendu des fiches</p>
          <img id="download" src="../../media/download.webp" width="35" height="35" alt="download" @click="downloadSheets"/>
        </div>

        <div id="semesters_div">
          <select name="semesters" class="semesters" v-model="selected_semester_sheets">
            <option v-for="index in list_semesters" :key="index" :value="index">
              S{{ index }}
            </option>
          </select>
        </div>
      </div>

      <div id="list-of-ressources">
        <!-- usage of v-if and v-else to display a message if there is no sheet for the selected semester -->
        <p v-if="getResourcesForSemester(selected_semester_sheets).length === 0">
          Aucune fiche rendue pour ce semestre.
        </p>

        <div v-else class="ressource" v-for="sheet in getResourcesForSemester(selected_semester_sheets)" :key="sheet.id">
          <p>{{ sheet.resourceLabel }}</p>
          <div style="gap: 5px">
            <button class="btn1" style="width: 5vw" @click="goToRessourceSheetDisplay('#/resource-sheet-display', sheet.resourceLabel)">Voir</button>
            <input type="checkbox" :checked="isSheetSelected(sheet.id)" @change="toggleSheetSelection(sheet.id)" />
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<style>
/* -- main --*/

#top{
  margin-bottom: 1vw;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

#download {
  cursor: pointer;
  transition: transform 0.2s;
}

#download:hover {
  transform: scale(1.1);
}

#title_font{
  font-size: 1.5vw;
}

#main_div {
  display: flex;
  align-items: center;
  height: 100%;
  align-self: center;
  width: 100%;
  justify-content: center;
  gap: 100px;
  margin-top: 3.2vw;
}

#main_div > div {
  border-radius: 15px;
}

#main_div > div > div {
  /* -- for MCCC-div and calender-div -- */
  border-radius: 15px;
}

/* -- sub dic for MCCC and calender --*/

#sub_div_for_MCCC_and_calender {
  width: 30%;
}

/* -- MCCC -- */

#MCCC_div {
  text-align: center;
  height: 100px;
  margin-bottom: 10%;
  align-content: center;
}

#MCCC_button {
  color: var(--main-theme-secondary-color);
  font-size: 50px;
  background-color: var(--sub-section-background-color);
  border-radius: 15px;
  width: 100%;
  height: 120%;
}

#MCCC_button:hover {
  cursor: pointer;
}

/* -- calendar -- */

#date_selector_div {
  background-color: var(--main-theme-background-color);
  color: var(--main-theme-secondary-color);
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 1vw;
  text-align: center;
}

#inline{
  display: inline-flex;
  gap: 1vw;
  align-items: center;
  margin-bottom: 1vw;
  justify-content: center;
}

#inline p {
  text-align: center;
}

input[type="date"] {
  border-radius: 15px;
  background-color: var(--div-rect-background-color);
  color: var(--main-theme-secondary-color);
  border: none;
  padding: 0.5vw 1vw;
  font-size: 1vw;
  min-height: 2em;
  box-sizing: border-box;
}

input[type="date"]:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.3);
}

input[type="date"]::-webkit-calendar-picker-indicator {
  filter: invert(1);
  cursor: pointer;
}

/* -- return sheets div -- */

#return_sheets_div {
  background-color: var(--main-theme-background-color);
  color: var(--main-theme-secondary-color);
  height: 70vh; /* Hauteur fixe */
  max-height: 70vh; /* Hauteur maximale fixe */
  width: 35%;
  padding: 0 1vw 1vw 1vw;
  overflow-y: auto; /* Scrollbar uniquement si nécessaire */
  font-size: 2vw;
  display: flex;
  flex-direction: column;
}

#return_sheets_div_header {
  position: sticky;
  top: 0;

  background-color: var(--main-theme-background-color);
  z-index: 10;
  padding-bottom: 1vw;
}

#top {
  display: flex;
  justify-content: space-between;
}

#list-of-ressources {
  flex: 1;
  overflow-y: auto; /* Scrollbar uniquement sur le contenu */
  padding-right: 1vw; /* Espace à droite de la scrollbar */
}

#list-of-ressources::-webkit-scrollbar {
  width: 0.8vw;
}

#list-of-ressources::-webkit-scrollbar-track {
  margin: 1vw 0.5vw 1vw 0; /* Marge à droite */
  background: var(--main-theme-secondary-background-color);
  box-shadow: inset 0 0 1vw var(--sub-scrollbar-color);
  border-radius: 15px;
}

#list-of-ressources::-webkit-scrollbar-thumb {
  background: var(--main-theme-secondary-color);
  border-radius: 15px;
  border: 0.2vw var(--main-theme-tertiary-color) solid;
}

/* Remove scrollbar from parent div */
#return_sheets_div::-webkit-scrollbar {
  display: none;
}

.ressource {
  background-color: var(--sub-div-background-color);
  margin: 1vw;
  padding: 5px;
  font-size: 2vw;
  border-radius: 15px;
  justify-content: space-between;
  display: flex;
  height: 4vw;
  align-items: center;
}

.semesters {
  background-color: var(--sub-div-background-color);
  width: 100%;
  height: 3vw;
  text-align: center;
  color: var(--main-theme-secondary-color);
  border-top-left-radius: 15px;
  border-top-right-radius: 15px;
  font-size: 15px;
}
</style>
