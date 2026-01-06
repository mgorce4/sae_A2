<script setup>
import { status } from '../main'
import { onMounted, ref, nextTick, computed } from 'vue'
import axios from 'axios'

status.value = 'Administration'

let display_more_area = ref(false)

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

const hours_per_student = ref([])

const resources = ref([])

function areTotalNaN() {
  return isNaN(total_initial_formation.value) && isNaN(total_work_study.value)
}

onMounted(async () => {
  /* usage of await to wait for the data to be fetched before adding event listeners */

  await Promise.all([
    axios
      .get('http://localhost:8080/api/ue-coefficients')
      .then((response) => (UEs.value = response.data)),
    axios
      .get('http://localhost:8080/api/hours-per-student')
      .then((response) => (hours_per_student.value = response.data)),
    axios
      .get('http://localhost:8080/api/resources')
      .then((response) => (resources.value = response.data))
  ])

  /* get the resources from hours_per_student to have the resources with their hours */
  for (let i = 0; i < hours_per_student.value.length; i++) {
    resources.value.push(hours_per_student.value[i].resource)
    resources.value[i].cm = hours_per_student.value[i].cm
    resources.value[i].td = hours_per_student.value[i].td
    resources.value[i].tp = hours_per_student.value[i].tp
    resources.value[i].total =
      hours_per_student.value[i].cm + hours_per_student.value[i].td + hours_per_student.value[i].tp
  }

  await nextTick()

  document.querySelectorAll('.accordion').forEach((acc) => {
    acc.addEventListener('click', function () {
      this.classList.toggle('active')
      const panel = this.nextElementSibling
      if (panel.style.maxHeight) {
        panel.style.maxHeight = null
      } else {
        panel.style.maxHeight = '100%'
      }
    })
  })

  document.getElementById('save').addEventListener('click', () => {
    total_initial_formation.value =
      parseInt(CM_initial_formation.value) +
      parseInt(TD_initial_formation.value) +
      parseInt(TP_initial_formation.value) +
      parseInt(Project_initial_formation.value)
    total_work_study.value =
      parseInt(CM_work_study.value) +
      parseInt(TD_work_study.value) +
      parseInt(TP_work_study.value) +
      parseInt(Project_work_study.value)

    /* if the forms are empty or filled with non-numeric values set totals to 0 */

    if (areTotalNaN()) {
      total_initial_formation.value = 0
      total_work_study.value = 0
    }

    display_more_area.value = false
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
          <button id="button_more" v-on:click="display_more_area = true">+</button>
        </div>

        <a class="accordion" id="dark_bar" v-show="display_more_area" method="post" v-on:submit.prevent="">Ajout d'une ressource :</a>

        <form class="panel_resource">
          <div id="left">
            <div>
              <label>Intitule de la ressource : </label>
              <input type="text" class="input" v-model="resource_label" required />
            </div>

            <div>
              <label>Code apogée : </label>
              <input type="text" class="input" v-model="apogee_code" required />
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

              <p>
                Nombre d'heures total : {{ total_initial_formation }} /
                {{ total_pn_initial_formation }}
              </p>
            </div>

            <div id="btn">
              <input class="btn1" type="reset" value="Annuler" />
              <input class="btn1" type="submit" value="Sauvegarder" id="save" />
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

                <p>Nombre d'heures total : {{ total_work_study }} / {{ total_pn_work_study }}</p>
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
                    <option v-for="UE in UEs" :key="UE.ueNumber" :value="UE.ue.label">
                      {{ UE.ue.label }}
                    </option>
                  </select>

                  <!-- button to add UE -->
                  <button id="button_more">+</button>
                </div>

                <div class="component">
                  <label>Coefficient : </label>
                  <input type="text" class="input" v-model="coefficient" required />
                </div>

                <div class="component">
                  <label>Professeur(s) associé(s) : </label>
                  <input type="text" class="input" v-model="teacher" required />

                  <button id="button_more">+</button>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>
      <div id="form_resources">
        <p v-if="resources.length > 0">Ressources créées :</p>
        <p v-else>Aucune ressource n'a été crée</p>

        <div v-for="resource in resources" :key="resource.idResource">
          <a class="accordion" id="dark_bar">{{ resource.label }} {{ resource.name }}</a>

          <div class="panel_resource">
            <div id="left_resource">
              <div class="container-fluid">
                <p>Code Apogee :</p>
                <input type="text" class="input" :value="resource.apogeeCode" />
              </div>

              <p>Nombre d'heure (formation initial) :</p>

              <div class="container-fluid">
                <p>CM :</p>
                <input type="text" class="input" :value="resource.cm" />
              </div>

              <div class="container-fluid">
                <p>TD :</p>
                <input type="text" class="input" :value="resource.td" />
              </div>

              <div class="container-fluid">
                <p>TP :</p>
                <input type="text" class="input" :value="resource.tp" />
              </div>

              <div class="container-fluid">
                <p>SAE :</p>
                <input type="text" class="input" />
              </div>

              <div class="container-fluid">
                <p>Total :</p>
                <input type="text" class="input" :value="resource.total" />
              </div>
            </div>

            <div class="vertical-line"></div>

            <div id="right_resource">
              <div class="container-fluid">
                <p>UE(s) affectée(s) :</p>
                <input type="text" class="input" :value="resource.label" />
              </div>

              <div class="container-fluid">
                <p>Coefficient(s) :</p>
                <input type="text" class="input" :value="resource.coefficient" />
              </div>

              <div class="container-fluid">
                <p>Professeur(s) associé(s) :</p>
              </div>

              <br />
              <br />

              <input class="btn1" value="Supprimer" />
              <br />
              <input class="btn1" value="Modifier" />
            </div>
          </div>
        </div>
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

#dark_bar {
  width: 95%;
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

.panel_resource {
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

.panel_resource > p {
  margin-top: 0;
  padding-top: 1vw;
}

.input {
  border-radius: 5px;
  background-color: rgb(117, 117, 117, 100);
  color: #ffffff;
  width: 100px;
  text-align: center;
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

#form_resources {
  padding: 0 1vw;
}

#form_resources > p {
  color: white;
  font-size: 1.5vw;
}

#left_resource {
  display: flex;
  flex-direction: column;
  width: 50%;
  padding: 10px;
  justify-content: center;
  align-items: center;
}

#left_resource > div {
  display: flex;
  justify-content: center;
  align-items: center;
}

.vertical-line {
  border-left: 3px solid #242222;
  display: inline-block;
  height: 330px;
  margin-top: 20px;
}

#right_resource {
  display: flex;
  flex-direction: column;
  width: 50%;
  padding: 10px;
  align-items: center;
  justify-content: center;
}
</style>
