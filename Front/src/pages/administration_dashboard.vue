<script setup>
/* import */
import { ref } from 'vue'
import { status, institutionLocation } from '../main'
import { DatePicker } from 'v-calendar'
import { onMounted } from 'vue'
import axios from 'axios'

/* constantes */

status.value = 'Administration'
institutionLocation.value = localStorage.institutionLocation

/* get the date of the day*/
const date = new Date()

const list_semesters = [1, 2, 3, 4, 5, 6]

/* we can use selected_semester to get the semester*/
let selected_semester_sheets = ref(list_semesters[0])

/* link with the API */

const resource_sheets = ref([])

onMounted(async () => {
    try {
        const response = await axios.get('http://localhost:8080/api/v2/resource-sheets')
        resource_sheets.value = response.data
    } catch (error) {
        console.error('Error loading resource sheets:', error)
        resource_sheets.value = []
    }
})

function getResourcesForSemester(semester) {
  return resource_sheets.value.filter((sheet) => sheet.semester === semester)
                              .filter((sheet) => sheet.institutionId == localStorage.idInstitution)
}

</script>

<template>
  <div id="main_div">
    <div id="sub_div_for_MCCC_and_calender">
      <div id="MCCC_div">
        <!-- link into MCCC page -->
        <button type="button" id="MCCC_button" onclick="document.location.href='#/mccc-select-path'">MCCC</button>
      </div>

      <div id="calender_div">
        <!-- for the calender -->

        <DatePicker id="calendar" v-model="date"></DatePicker>
      </div>
    </div>

    <div id="return_sheets_div">
      <div id="return_sheets_div_header">
        <div id="top">
          <p>Rendu des fiches</p>
          <img id="download" src="../../media/download.png" width="35" height="35" alt="download" />
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
          <input type="checkbox" />
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

/* -- calender -- */

#calender_div {
  background-color: var(--main-theme-background-color);
  color: var(--main-theme-secondary-color);
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 1vw;
}

#calendar {
  margin-top: 1vw;
  display: flex;
  justify-content: center;
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
  border: 0.2vw var(--main-theme-terciary-color) solid;
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
