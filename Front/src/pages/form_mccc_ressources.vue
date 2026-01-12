<script setup>
import { status } from '../main'
import { onMounted, ref, nextTick } from 'vue'
import axios from 'axios'

status.value = 'Administration'

let display_more_area = ref(false)

const resource_sheets = ref([])

const UEs = ref([])

const resource_name = ref('')

const access_rights = ref([])

const ue_list = ref([{id : 1, ue: '', coefficient: ''}])
const num_teacher_select = ref(1)

const access_right_teacher = 1

/*
* to show or hide the list of teachers
* use an array to manage multiple inputs
* to display
* ex : if the page has 3 teacher inputs
* we use an index to see if the list is to be shown or not
*/
const show_teacher_list = ref([false])

/* constant for the form */

const resource_label = ref('')
const apogee_code = ref('')
const terms = ref('')

const CM_initial_formation = ref()
const TD_initial_formation = ref()
const TP_initial_formation = ref()
const total_initial_formation = ref(0)

const CM_work_study = ref()
const TD_work_study = ref()
const TP_work_study = ref()
const total_work_study = ref(0)

/* list of lesson to use for the v-for */

const list_of_lesson = ['CM', 'TD', 'TP']

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

function getTotalInitialFormation() {
  return parseInt(CM_initial_formation.value) + parseInt(TD_initial_formation.value) + parseInt(TP_initial_formation.value)
}

function getTotalWorkStudyFormation() {
  return parseInt(CM_work_study.value) + parseInt(TD_work_study.value) + parseInt(TP_work_study.value)
}

/* allows to put event on each div selected */
function addTeacherEvents(div, index) {

  /* get the input and the list */
  const input = div.querySelector('.teacher')
  const list = div.querySelector('.show_teacher')

  input.addEventListener('focus', () => {
    show_teacher_list.value[index] = true
  })

  input.addEventListener('blur', () => {
      show_teacher_list.value[index] = false
  })

  list.addEventListener('mouseover', (event) => {
    if (event.target.classList && event.target.classList.contains('teacher_name')) {
      input.value = event.target.innerText
    }
  })

  list.addEventListener('click', (event) => {
    if (event.target.classList && event.target.classList.contains('teacher_name')) {
      input.value = event.target.innerText
      show_teacher_list.value[index] = false
    }
  })
}

onMounted(async () => {
  await Promise.all([
    axios
      .get('http://localhost:8080/api/v2/resource-sheets')
      .then((reponse) => (resource_sheets.value = reponse.data)),
    axios
      .get('http://localhost:8080/api/v2/mccc/ues')
      .then((response) => (UEs.value = response.data)),
    axios
      .get('http://localhost:8080/api/access-rights')
      .then((response) => (access_rights.value = response.data)),
  ])

  access_rights.value = access_rights.value.filter((ar) => ar.user.institution.idInstitution == localStorage.idInstitution).filter((ar) => ar.accessRight == access_right_teacher)

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
    total_initial_formation.value = getTotalInitialFormation()
    total_work_study.value = getTotalWorkStudyFormation()

    /* if the forms are empty or filled with non-numeric values set totals to 0 */

    if (areTotalNaN()) {
      total_initial_formation.value = 0
      total_work_study.value = 0
    }

    display_more_area.value = false

    let ues = document.querySelectorAll('#ue_select')
    let coefs = document.querySelectorAll('#coefficient')

    for (let i = 0; i < ue_list.value.length; i++) {
      ue_list.value[i].ue = ues[i].value
      ue_list.value[i].coefficient = coefs[i].value
    }

    if (ue_list.value.length > 1) {
      let first_ue = ue_list.value[0].ue

      for (let i = 0; i < ue_list.value.length; i++) {
        if (first_ue === ue_list.value[i].ue && i != 0) {
          document.getElementById("error_ue").innerHTML = "Une resource ne peut pas être affectée plusieurs fois à la même UE."
        }
      }
    }
  })

  document.getElementById('work_study_slider').addEventListener('click', () => {
    const inputs = document.querySelectorAll('.input_work_study')
    const checkbox = document.querySelector('#work_study_slider input[type="checkbox"]')

    inputs.forEach((input) => {
      if (checkbox.checked) {
        input.disabled = false
      } else {
        input.disabled = true
      }
    })
  })

  document.addEventListener('click', (event) => {

    if (event.target.id === 'button_ue_minus') {
      if (ue_list.value.length > 1) {
        /* find all UE divs */
        const ues = document.querySelectorAll('.ue_div')

        /* find which div contains the clicked button */
        let index_to_remove = -1
        ues.forEach((div, index) => {
          /* if the actual div is the target of the event the index is the index to remove */
          if (div.contains(event.target)) {
            index_to_remove = index
          }
        })

        /* remove the ue from the array at the good index */
        if (index_to_remove !== -1) {
          ue_list.value = ue_list.value.filter((_, index) => index !== index_to_remove)
        }
      }

    } else if (event.target.id === 'button_ue_plus' && getUEsByInstitution().length > ue_list.value.length) {
      /* generate new unique id */
      let id
      if (ue_list.value.length > 0) {
        /* get the max id and add 1 */
        id = Math.max(...ue_list.value.map(u => u.id)) + 1
      } else {
        /* else it's the first id */
        id = 1
      }
      ue_list.value.push({ id: id, ue: '', coefficient: '' })

    } else if (event.target.id === 'button_teacher_plus' && access_rights.value.length > num_teacher_select.value) {
      num_teacher_select.value += 1
      show_teacher_list.value.push(false)

      nextTick(() => {
        const containers = document.querySelectorAll('.teacher_select_container')
        /* get the last container added */
        const new_container = containers[containers.length - 1]
        addTeacherEvents(new_container, containers.length - 1)
      })
    } else if (event.target.id === 'button_teacher_cross' && num_teacher_select.value > 1) {
      event.target.parentElement.remove()
      show_teacher_list.value.pop()
    }
  })

  /* wait for the update of the DOM */
  await nextTick()

  const div_teacher_container = document.querySelectorAll('.teacher_select_container')
  div_teacher_container.forEach((div, index) => {
    /* add event to the new div */
    addTeacherEvents(div, index)
  })

})

function getUEsByInstitution() {
  return UEs.value.filter((ue) => ue.institutionId == localStorage.idInstitution)
}

function getResourcesBySemesterAndInstitution() {
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

        <a class="accordion" id="dark_bar" style="width: 97%" v-show="display_more_area" method="post" v-on:submit.prevent="">Ajout d'une ressource :</a>

        <div class="panel_resource">
          <div id="left">
            <div>
              <label for="resource_label">Intitulé de la ressource : </label>
              <input id="resource_label" type="text" class="input" v-model="resource_label" required />
            </div>

            <div>
              <label for="resource_name">Nom de la ressource : </label>
              <input id="resource_name" type="text" class="input" v-model="resource_name" required />
            </div>

            <div>
              <label for="apogee_code">Code apogée : </label>
              <input id="apogee_code" type="text" class="input" v-model="apogee_code" required />
            </div>

            <div>
              <label>Modalités : </label>
              <input type="text" class="input" v-model="terms" required />
            </div>

            <div>
              <p>Nombre d'heures (formation initiale) :</p>
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
                      <input type="text" class="input" v-model="TP_initial_formation" required />
                    </td>
                  </tr>
                </tbody>
              </table>

              <p>Nombre d'heures totales : {{ total_initial_formation }}</p>
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

                <p>Nombre d'heures (alternance) :</p>
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
                        <input type="text" class="input input_work_study" v-model="CM_work_study" required disabled/>
                      </td>
                      <td>
                        <input type="text" class="input input_work_study" v-model="TD_work_study" required disabled/>
                      </td>
                      <td>
                        <input type="text" class="input input_work_study" v-model="TP_work_study" required disabled/>
                      </td>
                    </tr>
                  </tbody>
                </table>

                <p>Nombre d'heures totales : {{ total_work_study }}</p>
              </div>
            </div>

            <div>
              <div id="right_bottom">
                <div class="component">
                  <label class="switch">
                    <input type="checkbox" />
                    <span class="slider"></span>
                  </label>

                  <p>Épreuve différente si multi‑compétences</p>
                </div>

                <div>
                  <div class="component" style="justify-content: center;">
                    <label for="ue_select">UE affectées : </label>

                    <button class="button_more" id="button_ue_plus">+</button>

                    <label for="coefficient" class="component" style="margin-top: 7px">Coefficient :</label>
                  </div>

                  <p v-if="getUEsByInstitution().length == 0">Aucune UE créée</p>

                  <div v-else>
                    <div v-for="ue in ue_list" :key="ue.id" class="component ue_div" style="margin-bottom: 1vw; margin-left: 5.9vw;">
                      <select id="ue_select" class="input">
                        <option v-for="ue in getUEsByInstitution()" :key="ue.ueNumber">
                          {{ ue.label }}
                        </option>
                      </select>
                      <button class="button_more" id="button_ue_minus">x</button>
                      <input id="coefficient" type="text" class="input" style="margin-top: 4px" v-model="ue.coefficient" required />
                    </div>

                    <p id="error_ue"></p>

                  </div>
                </div>

                <div style="margin-top: 5px">
                  <div class="component" style="justify-content: center">
                    <label for="teacher">Professeur(s) associé(s) : </label>
                    <button class="button_more" id="button_teacher_plus">+</button>
                  </div>

                  <div v-for="n in num_teacher_select" :key="n" class="component" style="justify-content: center">
                    <div class="teacher_select_container">
                      <input type="text" class="input teacher" required />

                      <div class="show_teacher" v-show="show_teacher_list[n - 1]">
                        <div v-if="access_rights.length > 0">
                          <div class="teacher_name" v-for="acr in access_rights" :key="acr">
                            {{acr.user.firstname}} {{acr.user.lastname}}
                          </div>
                        </div>
                        <p v-else >Aucun professeur ne peut être sélectionné</p>
                      </div>
                    </div>

                    <button class="button_more" id="button_teacher_cross">x</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div id="form_resources">
        <p v-if="getResourcesBySemesterAndInstitution().length > 0">Ressources créées :</p>
        <p v-else>Aucune ressource n'a été créée</p>

        <div v-for="resource in getResourcesBySemesterAndInstitution()" :key="resource.resourceId">
          <a class="accordion" id="dark_bar" style="width: 97%">{{ resource.resourceLabel }} {{ resource.resourceName }}</a>

          <div class="panel_resource">
            <div id="left_resource">
              <div class="container-fluid">
                <p>Code Apogee :</p>
                <input type="text" class="input" :value="resource.resourceApogeeCode" />
              </div>

              <div class="container-fluid">
                <p>Modalités :</p>
                <input type="text" class="input" :value="resource.terms" />
              </div>

              <p>Nombre d'heures (formation initiale) :</p>

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
                <p>Total : {{resource.hoursTeacher.total}}</p>
              </div>

              <p>Nombre d'heures (alternance) : </p>

              <div v-if="resource.hoursTeacherAltenrance == undefined">
                <p>Cette ressource n'a pas d'alternance</p>
              </div>

              <div v-else>
                <div class="container-fluid">
                  <p>CM :</p>
                  <input type="text" class="input" :value="resource.hoursTeacherAltenrance.cm" />
                </div>

                <div class="container-fluid">
                  <p>TD :</p>
                  <input type="text" class="input" :value="resource.hoursTeacherAltenrance.td" />
                </div>

                <div class="container-fluid">
                  <p>TP :</p>
                  <input type="text" class="input" :value="resource.hoursTeacherAltenrance.tp" />
                </div>

                <div class="container-fluid">
                  <p>Total : {{resource.hoursTeacher.total}}</p>
                </div>
              </div>

            </div>

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

              <div style="display: flex; gap: 10px; justify-content: center">
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
  width: 8vw;
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

#left_resource > div {
  display: flex;
  justify-content: center;
  align-items: center;
}

#right_resource {
  display: flex;
  flex-direction: column;
  width: 50%;
  padding: 10px;
  margin-bottom: 10px;
  font-size: 20px;
  border-left: var(--clickable-background-color) 3px solid;
}

#resources_list > p {
  color: var(--main-theme-secondary-color);
  font-size: 1.5vw;
  align-items: center;
  justify-content: center;
}

.show_teacher {
  background-color: rgba(0, 0, 0, 0.35);
  border-left: white 1px solid;
  border-bottom: white 1px solid;
  border-right: white 1px solid;
  border-bottom-left-radius: 5px;
  border-bottom-right-radius: 5px;
  max-height: 8vw;
  max-width: 8vw;
  overflow-y: auto;
  padding: 0.2vw;
}


.show_teacher::-webkit-scrollbar {
  width: 12px;
}

.show_teacher::-webkit-scrollbar-track {
  margin: 1em;
  background: var(--main-theme-secondary-background-color);
  box-shadow: inset 0 0 5px var(--sub-scrollbar-color);
  border-radius: 10px;
}

.show_teacher::-webkit-scrollbar-thumb {
  background: var(--main-theme-secondary-color);
  border-radius: 10px;
}

.teacher_name {
  cursor : pointer;
  background-color: rgba(117, 117, 117, 100);
  border-radius: 2px;
  padding: 0.3vw;
  margin: 0.3vw;
}

#error_ue {
  color: var(--error-color);
  width: 80%;
  text-align: center;
  margin-left: 3.5vw;
}
</style>
