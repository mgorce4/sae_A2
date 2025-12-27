<script setup>
/* import */
import { ref } from 'vue'
import { status } from '../main'
import { DatePicker } from 'v-calendar'
import { onMounted } from 'vue'
import axios from 'axios'

/* constantes */

status.value = 'Administration'

/* get the date of the day*/
const date = new Date()

const list_semesters = [1, 2, 3, 4, 5, 6]

/* we can use selected_semester to get the semester*/
let selected_semester_sheets = ref(list_semesters[0])

let selected_semester_calendar = ref(list_semesters[0])

/* link with the API */

const sheets = ref([])

onMounted(async () => {
  /* get of the value for the resource sheets from the DB */
  axios.get('http://localhost:8080/api/resource-sheets').then(reponse => (sheets.value = reponse.data))
})

/* return a filterd array of resources depending on the semester in argument */
function filterSheetsBySemester(semester) {
  /* the filter function anebal to filter the array sheets depending on the semester with "sheet.ressource.semester === semester */
  return sheets.value.filter(sheet => sheet.resource.semester === semester)
}

function getSheetLabel(sheet) {
  return sheet.resource.label
}

function isNoSheetsForSemester(semester) {
  return filterSheetsBySemester(semester).length === 0
}

</script>

<template>
  <div id="main_div">
    <div id="sub_div_for_MCCC_and_calender">

      <div id="MCCC_div">
        <!-- link into MCCC page -->
        <button type="button" id="MCCC_button" onclick="document.location.href='#/mccc-select-form'">MCCC</button>
      </div>

      <div id="calender_div">
        <select name="semesters" class="semesters" v-model="selected_semester_calendar">
          <option v-for="index in list_semesters" :key="index" :value="index">
            S{{ index }}
          </option>
        </select>

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
        <p v-if="isNoSheetsForSemester(selected_semester_sheets)">Aucune fiche rendue pour ce semestre.</p>

        <div v-else class="ressource" v-for="sheet in filterSheetsBySemester(selected_semester_sheets)" :key="sheet.idRessourceSheet">
          <p>{{ getSheetLabel(sheet) }}</p>
          <input type="checkbox" />
        </div>

      </div>
    </div>
  </div>
</template>

<style>
/* -- main --*/

#main_div {
  display: flex;
  align-items: center;
  height: 450px;
  width: 100%;
  justify-content: center;
  gap: 100px;
  margin-top: 3.2vw;
}

#main_div > div {
  border-radius: 10px;
}

#main_div > div > div {
  /* -- for MCCC-div and calender-div -- */
  border-radius: 10px;
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
  color: #ffffff;
  font-size: 50px;
  background-color: #2c2c3b;
  border-radius: 10px;
  width: 100%;
  height: 120%;
}

#MCCC_button:hover {
  cursor : pointer;
}

/* -- calender -- */

#calender_div {
  background-color: #3d4375;
  color: #ffffff;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 5px;
}

#calendar {
  margin-top: 5px;
  display: flex;
  justify-content: center;
}

/* -- return sheets div -- */

#return_sheets_div {
  background-color: #3d4375;
  color: #ffffff;
  height: 100%;
  width: 35%;
  padding: 0 5px 5px 5px;
  overflow-y: scroll;
  font-size: 20px;
}

#return_sheets_div_header {
  position: sticky;
  top: 0;
  background-color: #3d4375;
}

#top {
  display: flex;
  justify-content: space-between;
}

#return_sheets_div::-webkit-scrollbar {
  width: 12px;
}

#return_sheets_div::-webkit-scrollbar-track {
  margin: 1em;
  background: rgb(42, 45, 86);
  box-shadow: inset 0 0 5px rgb(24, 26, 50);
  border-radius: 10px;
}

#return_sheets_div::-webkit-scrollbar-thumb {
  background: rgb(254, 254, 254);
  border-radius: 10px;
  border: 3px black solid;
}

.ressource {
  background-color: #8b8ea7;
  margin: 15px;
  padding: 5px;
  font-size: 25px;
  border-radius: 10px;
  justify-content: space-between;
  display: flex;
  height: 60px;
  align-items: center;
}

.semesters {
  background-color: #8b8ea7;
  width: 100%;
  height: 30px;
  text-align: center;
  color: #ffffff;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
  font-size: 15px;
}
</style>
