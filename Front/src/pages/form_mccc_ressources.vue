<script setup>
import { status } from '../main'
import { onMounted, ref } from 'vue'

status.value = "Administration"

onMounted(() => {
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

/* hours */

const CM = ref('')
const TD = ref('')
const TP = ref('')
const Projet = ref('')

/* list of lesson to use for the v-for */

const list_of_lesson = ["CM", "TD", "TP", "Projet"]

</script>

<template>
  <div id="ressource">

    <div id="return_arrow">
      <button  id="back_arrow" onclick="document.location.href='#/mccc-select-form'">←</button>
      <p>Retour</p>
    </div>

    <div id="background_form">
      <div id="form">

        <div id="header_ressource">
          <p id="title">Ressources</p>
        </div>

        <div id="dark_bar">
          <h2>Ajouter une ressource</h2>
          <button id="button_more">+</button>
        </div>

        <a class="accordion" id="dark_bar">Ajout d'une ressource :</a>

        <div class="panel_ressources">
          <div id="left">

            <div>
              <label>Intitule de la ressource : </label>
              <input type="text" class="input">
            </div>

            <div>
              <label>Code apogée : </label>
              <input type="text" class="input">
            </div>

            <div>
              <p>Nombres d'heures (formation initial) : </p>
              <tr>
                <th v-for="lesson in list_of_lesson" :key="lesson">
                  {{lesson}}
                </th>
              </tr>

              <tr>
                <th>
                  <input type="text" class="input" v-model="CM" required>
                </th>
                <th>
                  <input type="text" class="input" v-model="TD" required>
                </th>
                <th>
                  <input type="text" class="input" v-model="TP" required>
                </th>
                <th>
                  <input type="text" class="input" v-model="Projet" required>
                </th>
              </tr>

              <p>Nombre d'heures total : .../...</p>
            </div>

            <div id="btn">
              <input class="btn1" type="reset" value="Annuler">
              <input class="btn1" type="submit" value="Sauvegarder">
            </div>
          </div>

          <div id="right">
            <div id="alternace">
              <div class="bottom_component">
                <label class="switch">
                  <input type="checkbox">
                  <span class="slider"></span>
                </label>

                <p>Nombres d'heures (formation initial) : </p>
              </div>

              <tr>
                <th v-for="lesson in list_of_lesson" :key="lesson">
                  {{lesson}}
                </th>
              </tr>

              <tr>
                <th>
                  <input type="text" class="input" v-model="CM" required>
                </th>
                <th>
                  <input type="text" class="input" v-model="TD" required>
                </th>
                <th>
                  <input type="text" class="input" v-model="TP" required>
                </th>
                <th>
                  <input type="text" class="input" v-model="Projet" required>
                </th>
              </tr>

              <p>Nombre d'heures total : .../...</p>
            </div>

            <div>


              <div id="right_bottom">

                <div class="bottom_component">
                  <label class="switch">
                    <input type="checkbox">
                    <span class="slider"></span>
                  </label>

                  <p>Epeurve différente si multi-compétences</p>
                </div>

                <div class="bottom_component">
                  <label for="UEs">UE affectées : </label>

                  <!-- exemple of UE -->

                  <select name="UEs" class="input">
                    <option value="UE1">UE1</option>
                    <option value="UE2">UE2</option>
                    <option value="UE3">UE3</option>
                  </select>

                  <!-- button to add UE -->
                  <button id="button_more">+</button>
                </div>

                <div class="bottom_component">
                  <label for="coefficient">Coefficient : </label>

                  <!-- exemple of coefficient -->

                  <input type="text" class="input">
                </div>

                <div class="bottom_component">
                  <label for="prof">Professeur(s) associé(s) : </label>
                  <input type="text" class="input" placeholder="Nom du professeur">
                  <input type="search" class="input">

                  <button id="button_more">+</button>
                </div>

              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style>

#ressource{
  margin: 3vw 14vw;
  justify-content: center;
}

#return_arrow{
  display: flex;
  align-items: center;
}

#return_arrow > p{
  font-size: 1.5vw;
  font-weight: bold;
  color: black;
  margin-left: 1.5vw;
}

#background_form{
  height: auto;
  background-color: rgb(61, 67, 117);
  border-radius: 15px;
  overflow-x: hidden;
  overflow-y: hidden;
  box-sizing: border-box;
  padding-bottom: 17px;
}

#header_ressource{
  background-color: rgb(44,49,88);
  height: auto;
  border-radius: 10px;
  margin: 1vw;
}

#title{
  color: white;
  text-align: center;
  padding-top: 0.5vw;
  padding-bottom: 0.5vw;
  font-weight: lighter;
  font-size: 2.3vw;
}

.accordion, #dark_bar >p{
  margin: 0vw;
  font-weight: lighter;
  font-size: 1.05vw;
}

.accordion {
  cursor: pointer;
  position: relative;
}

#dark_bar {
  width: 97%;
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

#form{
  padding: 0 1vw;
  overflow: hidden;
}

#form::-webkit-scrollbar {
  width: 12px;
}

#form::-webkit-scrollbar-track {
  margin: 1em;
  background: rgb(42,45,86);
  box-shadow: inset 0 0 5px rgb(24, 26, 50);
  border-radius: 10px;
}

#form::-webkit-scrollbar-thumb {
  background: rgb(254,254,254);
  border-radius: 10px;
}

.panel_ressources {
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
  display: flex;
}

.panel_ressources p {
  margin-top: 0;
  padding-top: 1vw;
}

.input {
  border: black 1px solid;
  border-radius: 5px;
  background-color: rgb(117,117,117,100);
  color: #FFFFFF;
  width: 100px;
}

#alternace {
  background-color: rgb(82, 92, 167);
  border-radius: 10px;
  padding: 5px;
}

#right_bottom {
  display: flex;
  flex-direction: column;
}

#left {
  display: flex;
  flex-direction: column;
  width: 50%;
  padding-right: 1vw;
  margin-top: 20px;
  gap: 15px;
}

#right {
  display: flex;
  flex-direction: column;
  width: 50%;
  padding-left: 1vw;
  margin-top: 20px;
  gap: 5px;
}

#right_bottom {
  display: flex;
  padding-top: 10px;
}

.bottom_component {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

#btn {
  width: 100%;
  height: 25%;
  align-items: start;
  align-content: center;
}

#btn > input {
  margin: 0px 35px 0px 35px;
}

</style>
