<script setup>
import { status } from '../main'
import { onMounted, ref, nextTick } from 'vue'
import axios from 'axios'

status.value = 'Administration'

let display_more_area = ref(false)

const resource_sheets = ref([])

const UEs = ref([])

const resource_name = ref('')

const num_ue_select = ref(1)
const num_coefficient_select = ref(1)

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

/* list of lesson to use for the v-for */

const list_of_lesson = ['CM', 'TD', 'TP', 'Projet']

function areTotalNaN() {
  return isNaN(total_initial_formation.value) && isNaN(total_work_study.value)
}

const getQueryParam = (param) => {
  const hash = window.location.hash
  const queryString = hash.split('?')[1]
  if (!queryString) return null
  const params = new URLSearchParams(queryString)
  return params.get(param)
}

onMounted(async () => {
  await Promise.all([
    axios
      .get('http://localhost:8080/api/v2/resource-sheets')
      .then((reponse) => (resource_sheets.value = reponse.data)),
    axios
      .get('http://localhost:8080/api/v2/mccc/ues')
      .then((response) => (UEs.value = response.data)),
  ])

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

  document.getElementById('work_study_slider').addEventListener('click', () => {
    const inputs = document.querySelectorAll('.input_work_study')
    const checkbox = document.querySelector('#work_study_slider input[type="checkbox"]')

    inputs.forEach((input) => {
      if (checkbox.checked) {
        input.disabled = true
      } else {
        input.disabled = false
      }
    })
  })

  document.querySelector('#button_ue_plus').addEventListener('click', () => {
    /* limit the number of UE select to the number of UEs available */
    if (getUEsForInstitution().length > num_ue_select.value) {
      num_ue_select.value += 1
      num_coefficient_select.value += 1
    }
  })

  document.querySelector('#button_ue_minus').addEventListener('click', () => {
    if (num_ue_select.value > 1) {
      num_ue_select.value -= 1
      num_coefficient_select.value -= 1
    }
  })
})

function getUEsForInstitution() {
  return UEs.value.filter((ue) => ue.institutionId == localStorage.idInstitution)
}

function getResourcesForSemester() {
  return resource_sheets.value
    .filter((sheet) => sheet.semester == getQueryParam('id'))
    .filter((sheet) => sheet.institutionId == localStorage.idInstitution)
}
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

        <a
          class="accordion"
          id="dark_bar"
          style="width: 97%"
          v-show="display_more_area"
          method="post"
          v-on:submit.prevent=""
          >Ajout d'une ressource :</a
        >

        <div   class="panel_resource">
          <div id="left">
            <div>
              <label for="resource_label">Intitule de la ressource : </label>
              <input
                id="resource_label"
                type="text"
                class="input"
                v-model="resource_label"
                required
              />
            </div>

            <div>
              <label for="resource_name">Nom de la ressource : </label>
              <input
                id="resource_name"
                type="text"
                class="input"
                v-model="resource_name"
                required
              />
            </div>

            <div>
              <label for="apogee_code">Code apogée : </label>
              <input id="apogee_code" type="text" class="input" v-model="apogee_code" required />
            </div>

            <div>
              <p>Nombres d'heures (formation initial) :</p>
              <table class="hours_table">
                <thead>
                  <tr>
                    <th v-for="lesson in list_of_lesson" :key="lesson">
                      {{ lesson }}
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>
                      <input type="text" class="input" v-model="CM_initial_formation" required />
                    </td>
                    <td>
                      <input type="text" class="input" v-model="TD_initial_formation" required />
                    </td>
                    <td>
                    </td>
                    <td>
                      <input type="text" class="input" v-model="Project_initial_formation" />
                    </td>
                  </tr>
                </tbody>
              </table>

              <p>Nombre d'heures total : {{ total_initial_formation }}</p>
            </div>

            <div id="btn">
              <input class="btn1" type="reset" value="Annuler" />
              <input class="btn1" type="submit" value="Sauvegarder" id="save" />
            </div>
          </div>

          <div id="right">
            <div id="work_study">
              <div class="component">
                <label class="switch" id="work_study_slider">
                  <input type="checkbox" />
                  <span class="slider"></span>
                </label>

                <p>Nombres d'heures (alternance) :</p>
              </div>

              <div id="work_study_hours">
                <table class="hours_table">
                  <thead>
                    <tr>
                      <th v-for="lesson in list_of_lesson" :key="lesson">
                        {{ lesson }}
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>
                        <input type="text" class="input input_work_study" v-model="CM_work_study" required />
                      </td>
                      <td>
                        <input type="text" class="input input_work_study" v-model="TD_work_study" required />
                      </td>
                      <td>
                        <input type="text" class="input input_work_study" v-model="TP_work_study" required />
                      </td>
                      <td>
                        <input type="text" class="input input_work_study" v-model="Project_work_study" required />
                      </td>
                    </tr>
                  </tbody>
                </table>

                <p>Nombre d'heures total : {{ total_work_study }}</p>
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

                <div style="display: flex">
                  <div style="width: 50%">
                    <div class="component spb" style="width: 80%">
                      <label for="ue_select">UE affectées : </label>

                      <button class="button_more" id="button_ue_plus">+</button>
                    </div>

                    <p v-if="getUEsForInstitution().length == 0">Aucune UE créée</p>

                    <div v-else>
                      <div v-for="n in num_ue_select" :key="n" class="component" style="margin-bottom: 23px">
                        <select id="ue_select" class="input">
                          <option v-for="ue in getUEsForInstitution()" :key="ue.ueNumber">
                            {{ ue.label }}
                          </option>
                        </select>
                      </div>
                    </div>

                  </div>

                  <div style="width: 50%">
                    <label for="coefficient" class="component" style="margin-top: 7px">Coefficient : </label>

                    <p v-if="getUEsForInstitution().length == 0">Aucune UE créée</p>

                    <div v-else v-for="n in num_coefficient_select" :key="n" class="spb component" style="width: 80%">
                      <input id="coefficient" type="text" class="input" v-model="coefficient" style="margin-top: 4px" required/>
                      <button class="button_more" id="button_ue_minus">x</button>
                    </div>
                  </div>
                </div>

                <div class="component" style="margin-top: 5px">
                  <label for="teacher">Professeur(s) associé(s) : </label>
                  <input id="teacher" type="text" class="input" v-model="teacher" required />

                  <button id="button_more">+</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div id="form_resources">
        <p v-if="getResourcesForSemester().length > 0">Ressources créées :</p>
        <p v-else>Aucune ressource n'a été crée</p>

        <div v-for="resource in getResourcesForSemester()" :key="resource.resourceId">
          <a class="accordion" id="dark_bar" style="width: 97%"
            >{{ resource.resourceLabel }} {{ resource.resourceName }}</a
          >

          <div class="panel_resource">
            <div id="left_resource">
              <div class="container-fluid">
                <p>Code Apogee :</p>
                <input type="text" class="input" :value="resource.resourceApogeeCode" />
              </div>

              <p>Nombre d'heure (formation initial) :</p>

              <div class="container-fluid">
                <p>CM :</p>
                <input type="text" class="input" :value="resource.hoursTeacher.cm" />
              </div>

              <div class="container-fluid">
                <p>TD :</p>
                <input type="text" class="input" :value="resource.hoursTeacher.td" />
              </div>

              <div class="container-fluid">
                <p>TP :</p>
                <input type="text" class="input" :value="resource.hoursTeacher.tp" />
              </div>

              <div class="container-fluid">
                <p>SAE :</p>
                <input type="text" class="input" />
              </div>

              <div class="container-fluid">
                <p>Total :</p>
                <input type="text" class="input" :value="resource.hoursTeacher.total" />
              </div>
            </div>

            <div class="vertical-line"></div>

            <div id="right_resource">
              <div class="container-fluid">
                <p>UE(s) affectée(s) :</p>
                <div v-for="ue in resource.ueReferences" :key="ue.ueNumber">
                  <input type="text" class="input" :value="ue.label" />
                </div>
              </div>

              <div class="container-fluid">
                <p>Coefficient(s) :</p>
                <div v-for="ue in resource.ueReferences" :key="ue.ueNumber">
                  <input type="text" class="input" :value="ue.coefficient" />
                </div>
              </div>

              <div class="container-fluid">
                <p>Professeur(s) associé(s) :</p>
                <div v-for="teacher in resource.teachers" :key="teacher">
                  <input type="text" class="input" :value="teacher" />
                </div>
              </div>

              <br />
              <br />

              <div style="display: flex; gap: 10px">
                <input class="btn1" type="button" value="Supprimer" />
                <br />
                <input class="btn1" type="button" value="Modifier" />
              </div>
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
  color: var(--main-theme-terciary-color);
  margin-left: 1.5vw;
}

#background_form {
  height: auto;
  background-color: var(--main-theme-background-color);
  border-radius: 15px;
  overflow-x: hidden;
  overflow-y: hidden;
  box-sizing: border-box;
  padding-bottom: 17px;
}

#header_ressource {
  background-color: var(--main-theme-secondary-background-color);
  height: auto;
  border-radius: 10px;
  margin: 1vw;
}

#title {
  color: var(--main-theme-secondary-color);
  text-align: center;
  padding-top: 0.5vw;
  padding-bottom: 0.5vw;
  font-weight: lighter;
  font-size: 2.3vw;
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
  background: var(--main-theme-secondary-background-color);
  box-shadow: inset 0 0 5px var(--sub-scrollbar-color);
  border-radius: 10px;
}

#form::-webkit-scrollbar-thumb {
  background: var(--main-theme-secondary-color);
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
  color: var(--main-theme-secondary-color);
  margin-top: 0;
  display: flex;
}

.panel_resource > p {
  margin-top: 0;
  padding-top: 1vw;
}

.input {
  border-radius: 5px;
  background-color: rgba(117, 117, 117, 100);
  color: var(--main-theme-secondary-color);
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
  color: var(--main-theme-secondary-color);
  font-size: 1.5vw;
}

#left_resource {
  display: flex;
  flex-direction: column;
  width: 50%;
}

#resources_list {
  background-color: var(--main-theme-background-color);
  border-radius: 15px;
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
  border-left: 3px solid var(--clickable-background-color);
  display: inline-block;
  height: 330px;
  margin-top: 20px;
}

#right_resource {
  display: flex;
  flex-direction: column;
  width: 50%;
  padding: 10px;
  margin-bottom: 10px;
  font-size: 20px;
}

#resources_list > p {
  color: var(--main-theme-secondary-color);
  font-size: 1.5vw;
  align-items: center;
  justify-content: center;
}
</style>
