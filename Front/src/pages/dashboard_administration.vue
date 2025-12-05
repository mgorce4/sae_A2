<script setup>
import { ref } from 'vue';
import { status } from '../main'
import { DatePicker as DatePicker } from 'v-calendar'

status.value = 'Administration'

const date = new Date();

</script>

<template>
  <p>dashboard administration</p>

  <div id="main-div">
    <div id="sub-div-for-MCCC-and-calender">
      <div id="MCCC-div">
        <!-- link into MCCC page -->
        <a href="#">MCCC</a>
      </div>

      <div id="calender-div">
        <!-- add a calender and the semester -->
        <select name="semesters" class="semesters">
          <option value="S1">S1</option>
          <option value="S2">S2</option>
          <option value="S3">S3</option>
          <option value="S4">S4</option>
          <option value="S5">S5</option>
          <option value="S6">S6</option>
        </select>

        <!-- for the calender -->

        <DatePicker id="calendar" v-model="date"></DatePicker>
      </div>
    </div>

    <div id="return-sheets-div">
      <div id="return-sheets-div-header">
        <p>Rendu des fiches</p>
        <img id="download" src="../../media/download.png" width="35" height="35" alt="download" />
      </div>

      <div id="semesters-div">
        <select name="semesters" class="semesters">
          <option value="S1">S1</option>
          <option value="S2">S2</option>
          <option value="S3">S3</option>
          <option value="S4">S4</option>
          <option value="S5">S5</option>
          <option value="S6">S6</option>
        </select>
      </div>

      <div id="list-of-ressources">
        <!-- exemple of ressources -->
        <div class="ressource">
          <p>R1.01</p>
          <input type="checkbox" />
        </div>
        <div class="ressource">
          <p>R1.02</p>
          <input type="checkbox" />
        </div>
        <div class="ressource">
          <p>R1.03</p>
          <input type="checkbox" />
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
  width: 25%;
}

/* -- MCCC -- */

#MCCC-div {
  background-color: #2c2c3b;
  color: #ffffff;
  text-align: center;
  height: 100px;
  width: 70%;
  margin-bottom: 50px;
  margin-left: 15%;
  align-content: center;
}

#MCCC-div > a {
  text-decoration: none;
  color: #ffffff;
  font-size: 50px;
}

/* -- calender -- */

#calender-div {
  background-color: #3d4375;
  color: #ffffff;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

#calendar {
  display: flex;
  justify-content: center;
}

/* -- return sheets div -- */

#return-sheets-div-header {
  display: flex;
}

#download {
  position: relative;
  left: 55%;
}

#return-sheets-div {
  background-color: #3d4375;
  color: #ffffff;
  height: 100%;
  width: 25%;
  padding: 5px;
  overflow-y: scroll;
}

#list-of-ressources {


}

#list-of-ressources > div {
  display: flex
}

#list-of-ressources > div > input {
  position: relative;
  left: 75%;
}

.ressource {
  background-color: #8b8ea7;
  margin: 15px;
  padding: 5px;
  font-size: 20px;
  border-radius: 10px;
}

.semesters {
  background-color: #8b8ea7;
  width: 100%;
  height: 30px;
  text-align: center;
  color: #ffffff;
  border-radius: 10px;
  font-size: 15px;
}
</style>
