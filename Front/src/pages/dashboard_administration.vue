<script setup>
    import { status } from '../main'

    status.value = "Administration"
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

        <div id="calender"></div>

      </div>
    </div>

    <div id="return-sheets-div">
      <div id="return-sheets-div-header">
        <p>Rendu des fiches</p>
        <img id="download" src="../../media/download.png" width="35" height="35" alt="download">
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
          <input type="checkbox">
        </div>
        <div class="ressource">
          <p>R1.02</p>
          <input type="checkbox">
        </div>
        <div class="ressource">
          <p>R1.03</p>
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
  width: 25%;
}

/* -- MCCC -- */

#MCCC-div {
  background-color: #2C2C3B;
  color: #FFFFFF;
  text-align: center;
  height: 100px;
  width: 70%;
  margin-bottom: 100px;
  margin-left: 15%;
  align-content: center;
}

#MCCC-div > a {
  text-decoration: none;
  color: #FFFFFF;
  font-size: 50px;
}

/* -- calender -- */

#calender-div {
  background-color: #3D4375;
  color: #FFFFFF;
  height: 250px;
  width: 100%;
}

#calender {
  background-color: #FFFFFF;
  height: 85%;
  width: 50%;
  margin: auto;
  border: black 2px solid;
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
  background-color: #3D4375;
  color: #FFFFFF;
  height: 100%;
  width: 25%;
  padding: 5px;
}

#list-of-ressources > div {
  display: flex;
}

#list-of-ressources > div > input {
  position: relative;
  left: 75%;
}

.ressource {
  background-color: #8B8EA7;
  margin: 15px;
  padding: 5px;
  font-size: 20px;
  border-radius: 10px;
}

.semesters {
  background-color: #8B8EA7;
  width: 100%;
  height: 30px;
  text-align: center;
  color: #FFFFFF;
  border-radius: 10px;
  font-size: 15px;
}

</style>
