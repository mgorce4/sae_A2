<script setup>
import { status } from '../main'
import { onMounted, ref, computed } from 'vue'
import axios from 'axios'

status.value = 'Administration'

/* constant for the form */

const resource_label = ref('')
const apogee_code = ref('')

const CM_initial_formation = ref()
const TD_initial_formation = ref()
const TP_initial_formation = ref()
const Project_initial_formation = ref()
const total_initial_formation = ref(0)

const CM_work_study = ref()
const TD_work_study = ref()
const TP_work_study = ref()
const Project_work_study = ref()
const total_work_study = ref(0)

const teacher = ref('')
const coefficient = ref()

/* get from the pn*/
const total_pn_initial_formation = ref(0)
const total_pn_work_study = ref(0)

/* list of lesson to use for the v-for */

const list_of_lesson = ['CM', 'TD', 'TP', 'Projet']

const UEs = ref([])

function areTotalNaN() {
  return isNaN(total_initial_formation.value) && isNaN(total_work_study.value)
}

onMounted(() => {

  axios.get('http://localhost:8080/api/ues').then(response => (UEs.value = response.data))

  const acc = document.getElementsByClassName('accordion')

  for (let i = 0; i < acc.length; i++) {
    acc[i].addEventListener('click', function () {
      this.classList.toggle('active')
      const panel = this.nextElementSibling
      if (panel.style.maxHeight) {
        panel.style.maxHeight = null
      } else {
        panel.style.maxHeight = panel.scrollHeight + 'px'
      }
    })
  }

  document.getElementById('save').addEventListener('click', () => {
    total_initial_formation.value = parseInt(CM_initial_formation.value) + parseInt(TD_initial_formation.value) + parseInt(TP_initial_formation.value) + parseInt(Project_initial_formation.value)
    total_work_study.value = parseInt(CM_work_study.value) + parseInt(TD_work_study.value) + parseInt(TP_work_study.value) + parseInt(Project_work_study.value)

    /* if the forms are empty or filled with non-numeric values set totals to 0 */

    if (areTotalNaN()) {
      total_initial_formation.value = 0
      total_work_study.value = 0
    }
  })

})
</script>

<template>
  <div id="ressource">
    <div id="return_arrow">
      <button id="back_arrow" onclick="document.location.href='#/mccc-select-form'">←</button>
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

        <form class="panel_ressources">
          <div id="left">
            <div>
              <label>Intitule de la ressource : </label>
              <input type="text" class="input" v-model="resource_label" required/>
            </div>

            <div>
              <label>Code apogée : </label>
              <input type="text" class="input" v-model="apogee_code" required/>
            </div>

            <div>
              <p>Nombres d'heures (formation initial) :</p>
              <tr>
                <th v-for="lesson in list_of_lesson" :key="lesson">
                  {{ lesson }}
                </th>
              </tr>

              <tr>
                <th>
                  <input type="text" class="input" v-model="CM_initial_formation" required />
                </th>
                <th>
                  <input type="text" class="input" v-model="TD_initial_formation" required />
                </th>
                <th>
                  <input type="text" class="input" v-model="TP_initial_formation" required />
                </th>
                <th>
                  <input type="text" class="input" v-model="Project_initial_formation" required />
                </th>
              </tr>

              <p>Nombre d'heures total : {{ total_initial_formation }} / {{ total_pn_initial_formation }}</p>
            </div>

            <div id="btn">
              <input class="btn1" type="reset" value="Annuler" />
              <input class="btn1" type="submit" value="Sauvegarder" id="save"/>
            </div>
          </div>

          <div id="right">

            <div id="work_study">
              <div class="component">
                <label class="switch">
                  <input type="checkbox" />
                  <span class="slider"></span>
                </label>

                <p>Nombres d'heures (alternance) :</p>
              </div>

              <div id="work_study_hours">
                <tr>
                  <th v-for="lesson in list_of_lesson" :key="lesson">
                    {{ lesson }}
                  </th>
                </tr>

                <tr>
                  <th>
                    <input type="text" class="input" v-model="CM_work_study" required />
                  </th>
                  <th>
                    <input type="text" class="input" v-model="TD_work_study" required />
                  </th>
                  <th>
                    <input type="text" class="input" v-model="TP_work_study" required />
                  </th>
                  <th>
                    <input type="text" class="input" v-model="Project_work_study" required />
                  </th>
                </tr>

                <p>Nombre d'heures total : {{ total_work_study }} / {{total_pn_work_study}}</p>
              </div>
            </div>

            <div>
              <div id="right_bottom">
                <div class="component">
                  <label class="switch">
                    <input type="checkbox" />
                    <span class="slider"></span>
                  </label>

                  <p>Epeurve différente si multi-compétences</p>
                </div>

                <div class="component">
                  <label>UE affectées : </label>

                  <select class="input">
                    <option v-for="UE in UEs" :key="UE.ueNumber" :value="UE.label">{{UE.label}}</option>
                  </select>

                  <!-- button to add UE -->
                  <button id="button_more">+</button>
                </div>

                <div class="component">
                  <label>Coefficient : </label>
                  <input type="text" class="input" v-model="coefficient" required/>
                </div>

                <div class="component">
                  <label>Professeur(s) associé(s) : </label>
                  <input type="text" class="input" v-model="teacher" required/>

                  <button id="button_more">+</button>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style>
#ressource {
  margin: 3vw 14vw;
  justify-content: center;
}

#return_arrow {
  display: flex;
  align-items: center;
}

#return_arrow > p {
  font-size: 1.5vw;
  font-weight: bold;
  color: black;
  margin-left: 1.5vw;
}

#background_form {
  height: auto;
  background-color: rgb(61, 67, 117);
  border-radius: 15px;
  overflow-x: hidden;
  overflow-y: hidden;
  box-sizing: border-box;
  padding-bottom: 17px;
}

#header_ressource {
  background-color: rgb(44, 49, 88);
  height: auto;
  border-radius: 10px;
  margin: 1vw;
}

#title {
  color: white;
  text-align: center;
  padding-top: 0.5vw;
  padding-bottom: 0.5vw;
  font-weight: lighter;
  font-size: 2.3vw;
}

.accordion,
#dark_bar > p {
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

#form {
  padding: 0 1vw;
  overflow: hidden;
}

#form::-webkit-scrollbar {
  width: 12px;
}

#form::-webkit-scrollbar-track {
  margin: 1em;
  background: rgb(42, 45, 86);
  box-shadow: inset 0 0 5px rgb(24, 26, 50);
  border-radius: 10px;
}

#form::-webkit-scrollbar-thumb {
  background: rgb(254, 254, 254);
  border-radius: 10px;
}

.panel_ressources {
  width: 90%;
  max-height: 0;
  justify-self: center;
  padding: 0 18px;
  background-color: rgba(0, 0, 0, 0.35);
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
  background-color: rgb(117, 117, 117, 100);
  color: #ffffff;
  width: 100px;
}

#work_study {
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

.component {
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
