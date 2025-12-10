<script setup>
import { ref } from 'vue';
import { status } from '../main'
import { DatePicker as DatePicker } from 'v-calendar'

status.value = 'Administration'

/* get the date of the day*/
const date = new Date();

const semesters = [1, 2, 3, 4, 5, 6];

/* we can use selected_semester to get the semester*/
const selected_semester_sheets = ref('S1');

const selected_semester_calendar = ref('S1');

import { onMounted } from 'vue'
import axios from 'axios'

const ressource_sheets = ref([])

onMounted(async () => {
  const res = await axios.get('http://localhost:8080/api/ressource-sheets')
  ressource_sheets.value = res.data
})

</script>

<template>

  <p>dashboard administration</p>

  <div id="main-div">
    <div id="sub-div-for-MCCC-and-calender">
      <div id="MCCC-div">
        <!-- link into MCCC page -->
        <a id="MCCC_button" href="#/mccc-select-form">MCCC</a>
      </div>

      <div id="calender-div">
        <!-- add a calender and the semester -->
        <select name="semesters" class="semesters" v-model="selected_semester_calendar">
          <option v-for="index in semesters" :key="index" :value="'S' + index">S{{ index }}</option>
        </select>

        <!-- for the calender -->

        <DatePicker id="calendar" v-model="date"></DatePicker>
      </div>
    </div>

    <div id="return-sheets-div">
      <div id="return-sheets-div-header">
        <div id="top">
          <p>Rendu des fiches</p>
          <img id="download" src="../../media/download.png" width="35" height="35" alt="download" />
        </div>

        <div id="semesters-div">
          <select name="semesters" class="semesters" v-model="selected_semester_sheets">
            <option v-for="index in semesters" :key="index" :value="'S' + index">S{{ index }}</option>
          </select>
        </div>
      </div>

      <div id="list-of-ressources">
        <div class="ressource" v-for="r in ressource_sheets" :key="r.idRessourceSheet">
          <p>{{r.name}}</p>
          <input type="checkbox">
        </div>

      </div>
    </div>
  </div>
</template>

<style>
/* -- main --*/

#main-div {
  display: flex;
  align-items: center;
  height: 450px;
  width: 100%;
  justify-content: center;
  gap: 100px;
}

#main-div > div {
  border-radius: 10px;
}

#main-div > div > div {
  /* -- for MCCC-div and calender-div -- */
  border-radius: 10px;
}

/* -- sub dic for MCCC and calender --*/

#sub-div-for-MCCC-and-calender {
  width: 30%;
}

/* -- MCCC -- */

#MCCC-div {
  text-align: center;
  height: 100px;
  margin-bottom: 50px;
  align-content: center;
}

#MCCC_button {
  text-decoration: none;
  color: #ffffff;
  font-size: 50px;
  background-color: #2c2c3b;
  border-radius: 10px;
  padding: 35px 65px 35px 65px;
}

/* -- calender -- */

#calender-div {
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

#return-sheets-div {
  background-color: #3d4375;
  color: #ffffff;
  height: 100%;
  width: 35%;
  padding: 0 5px 5px 5px;
  overflow-y: scroll;
  font-size: 20px;
}

#return-sheets-div-header {
  position: sticky;
  top: 0;
  background-color: #3d4375;
}

#top {
  display: flex;
  justify-content: space-between;
}

#return-sheets-div::-webkit-scrollbar {
  width: 12px;
}

#return-sheets-div::-webkit-scrollbar-track {
  margin: 1em;
  background: rgb(42,45,86);
  box-shadow: inset 0 0 5px rgb(24, 26, 50);
  border-radius: 10px;
}

#return-sheets-div::-webkit-scrollbar-thumb {
  background: rgb(254,254,254);
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
