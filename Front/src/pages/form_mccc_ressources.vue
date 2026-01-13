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
/* value link with the v-model */
const teachers_list = ref([{ id: 1, value: '' }])

const access_right_teacher = 1

/*
* to show or hide the list of teachers
* use an array to manage multiple inputs
* to display
* ex : if the page has 3 teacher inputs
* we use an index to see if the list is to be shown or not
*/
const show_teacher_list = ref([false])
const show_teacher = ref(false)

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

/* allows to put event on each div selected */
function addTeacherEvents(div) {

  /* get the input and the list */
  const input = div.querySelector('.teacher')
  const list = div.querySelector('.show_teacher')

  /* get the index of the div */
  const get_index = () => Array.from(document.querySelectorAll('.teacher_select_container')).indexOf(div)

  input.addEventListener('focus', () => {
    const index = get_index()
    if (index !== -1) {
      show_teacher_list.value[index] = true
    }
  })

  input.addEventListener('blur', () => {
    const index = get_index()
    if (index !== -1) {
      show_teacher_list.value[index] = false
    }
  })

  list.addEventListener('mouseover', (event) => {
    if (event.target.classList && event.target.classList.contains('teacher_name')) {
      input.value = event.target.innerText
      // keep the reactive source of truth in sync
      const index = get_index()
      if (index !== -1 && teachers_list.value[index]) {
        teachers_list.value[index].value = event.target.innerText
      }
    }
  })

  list.addEventListener('click', (event) => {
    if (event.target.classList && event.target.classList.contains('teacher_name')) {
      input.value = event.target.innerText
      const index = get_index()
      if (index !== -1 && teachers_list.value[index]) {
        teachers_list.value[index].value = event.target.innerText
      }
      if (index !== -1) {
        show_teacher_list.value[index] = false
      }
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

    /* display errors messages */

    /* reset all error messages */
    document.getElementById("error_resource_label").innerHTML = ""
    document.getElementById("error_resource_name").innerHTML = ""
    document.getElementById("error_apogee_code").innerHTML = ""
    document.getElementById("error_terms").innerHTML = ""
    document.getElementById("error_initial_formation").innerHTML = ""
    document.getElementById("error_work_study").innerHTML = ""
    document.getElementById("error_ue").innerHTML = ""
    document.getElementById("error_main_teacher").innerHTML = ""
    document.getElementById("error_teacher").innerHTML = ""

    /* variable for the messages */

    let ues = document.querySelectorAll('#ue_select')
    let coefs = document.querySelectorAll('#coefficient')
    let teachers = document.querySelectorAll('.teacher')

    /* get all inputs values */
    let resource_label_input = document.getElementById('resource_label').value
    let resource_name_input = document.getElementById('resource_name').value
    let apogee_code_input = document.getElementById('apogee_code').value
    let terms_input = document.getElementById('terms').value
    let CM_initial_formation_input = CM_initial_formation.value
    let TD_initial_formation_input = TD_initial_formation.value
    let TP_initial_formation_input = TP_initial_formation.value
    let CM_work_study_input = CM_work_study.value
    let TD_work_study_input = TD_work_study.value
    let TP_work_study_input = TP_work_study.value
    let main_teacher_input = document.getElementById('main_teacher').value

    /* display error messages if needed */

    if (resource_label_input === '') {
      document.getElementById("error_resource_label").innerHTML = "L'intitulé de la ressource est obligatoire"
    }

    if (resource_name_input === '') {
      document.getElementById("error_resource_name").innerHTML = "Le nom de la ressource est obligatoire"
    }

    if (apogee_code_input === '') {
      document.getElementById("error_apogee_code").innerHTML = "Le code apogée est obligatoire"
    }

    if (terms_input === '') {
      document.getElementById("error_terms").innerHTML = "Les modalités sont obligatoires"
    }

    if (CM_initial_formation_input === undefined && TD_initial_formation_input === undefined && TP_initial_formation_input === undefined) {
      document.getElementById("error_initial_formation").innerHTML = "Les heures de la formation innitiale sont obligatoire"
    }

    if (document.getElementById('work_study_slider').querySelector('input[type="checkbox"]').checked
        && CM_work_study_input === undefined && TD_work_study_input === undefined && TP_work_study_input === undefined) {
        document.getElementById("error_work_study").innerHTML = "Les heures de l'alternance sont obligatoire"
    }

    if (main_teacher_input === '') {
      document.getElementById("error_main_teacher").innerHTML = "Le professeur principal est obligatoire"
    }

    /* add ues and coefficent to the list */
    for (let i = 0; i < ue_list.value.length; i++) {
      ue_list.value[i].ue = ues[i].value
      ue_list.value[i].coefficient = coefs[i].value
    }

    for (let i = 0; i < ue_list.value.length; i++) {
      if (ue_list.value[i].coefficient === '') {
        document.getElementById("error_ue").innerHTML = "Le coefficient de chaque UE est obligatoire"
      }
    }

    if (ue_list.value.length > 1) {
      let first_ue = ue_list.value[0].ue

      for (let i = 0; i < ue_list.value.length; i++) {
        if (first_ue === ue_list.value[i].ue) {
          document.getElementById("error_ue").innerHTML = "Une resource ne peut pas être affectée plusieurs fois à la même UE"
        }
      }
    }

    if (teachers.length > 0) {
      let teacher_names = []

      teacher_names.push(document.getElementById('main_teacher').value)

      teachers.forEach((teacher) => {
        teacher_names.push(teacher.value)
      })

      for (let i = 0; i < teacher_names.length; i++) {

        if (teacher_names[i] === '') {
          document.getElementById("error_teacher").innerHTML += "Un ou plusieurs professeurs ne sont pas renseignés."
          return
        }

        for (let j = 0; j < teacher_names.length; j++) {

          if (isTeacherNamesEquals(i, j, teacher_names)) {
            document.getElementById("error_teacher").innerHTML += " Un professeur ne peut pas être sélectionné plusieurs fois pour la même ressource"
            return
          }
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

    } else if (event.target.id === 'button_teacher_plus' && access_rights.value.length > teachers_list.value.length) {
      // add a new teacher entry
      let id
      if (teachers_list.value.length > 0) {
        id = Math.max(...teachers_list.value.map(t => t.id)) + 1
      } else {
        id = 1
      }
      teachers_list.value.push({ id: id, value: '' })
      show_teacher_list.value.push(false)

      nextTick(() => {
        const containers = document.querySelectorAll('.teacher_select_container')
        /* attach events to the last added container */
        const new_container = containers[containers.length - 1]
        if (new_container) addTeacherEvents(new_container)
      })

    } else if (event.target.id === 'button_teacher_cross') {
      // remove the corresponding teacher entry (but keep at least one)
      const row = event.target.closest('.teacher_row')
      if (!row) return
      const rows = Array.from(document.querySelectorAll('.teacher_row'))
      const index_to_remove = rows.indexOf(row)

      if (index_to_remove !== -1 && teachers_list.value.length > 1) {
        teachers_list.value = teachers_list.value.filter((_, i) => i !== index_to_remove)
        show_teacher_list.value = show_teacher_list.value.filter((_, i) => i !== index_to_remove)
      }
     }
  })

  /* wait for the update of the DOM */
  await nextTick()

  const div_teacher_container = document.querySelectorAll('.teacher_select_container')
  div_teacher_container.forEach((div) => {
    /* add event to the new div */
    addTeacherEvents(div)
  })

  /* main teacher input */

  const main_teacher = document.getElementById('main_teacher')
  const list = document.querySelector('.show_teacher')

  main_teacher.addEventListener('focus', () => {
    show_teacher.value = true
  })

  main_teacher.addEventListener('blur', () => {
    show_teacher.value = false
  })

  list.addEventListener('mouseover', (event) => {
    if (event.target.classList && event.target.classList.contains('teacher_name')) {
      main_teacher.value = event.target.innerText
    }
  })

  list.addEventListener('click', (event) => {
    if (event.target.classList && event.target.classList.contains('teacher_name')) {
      main_teacher.value = event.target.innerText
      show_teacher.value = false
    }
  })
})

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

function getUEsByInstitution() {
  return UEs.value.filter((ue) => ue.institutionId == localStorage.idInstitution)
}

function getResourcesBySemesterAndInstitution() {
  return resource_sheets.value
    .filter((sheet) => sheet.semester == getQueryParam('id'))
    .filter((sheet) => sheet.institutionId == localStorage.idInstitution)
}

function getUEFromResource(resource) {
  let ues = []

  resource.ueReferences.map((ue) => {
    ues.push(ue.label)
  })

  return ues
}

function getCoefFromResource(resource) {
  let coefs = []

  resource.ueReferences.map((ue) => {
    coefs.push(ue.coefficient)
  })

  return coefs
}

function isTeacherNamesEquals(i, j, teacher_names) {
  return i!== j && teacher_names[i] === teacher_names[j]
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
            <p id="error_resource_label" class="error_message"></p>

            <div>
              <label for="resource_name">Nom de la ressource : </label>
              <input id="resource_name" type="text" class="input" v-model="resource_name" required />
            </div>
            <p id="error_resource_name" class="error_message"></p>

            <div>
              <label for="apogee_code">Code apogée : </label>
              <input id="apogee_code" type="text" class="input" v-model="apogee_code" required />
            </div>
            <p id="error_apogee_code" class="error_message"></p>

            <div>
              <label>Modalités : </label>
              <input id="terms" type="text" class="input" v-model="terms" required />
            </div>
            <p id="error_terms" class="error_message"></p>

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
            <p id="error_initial_formation" class="error_message"></p>

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
              <p id="error_work_study" class="error_message"></p>
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
                  </div>
                  <p id="error_ue" class="error_message"></p>
                </div>

                <div style="margin-top: 5px">

                  <div class="component" style="justify-content: center; flex-direction: column">
                    <label>Professeur principal de la ressource : </label>

                    <div>
                      <input type="text" class="input" id="main_teacher" required />

                      <div class="show_teacher" v-show="show_teacher">
                        <div v-if="access_rights.length > 0">
                          <div class="teacher_name" v-for="acr in access_rights" :key="acr">
                            {{acr.user.firstname}} {{acr.user.lastname}}
                          </div>
                        </div>
                        <p v-else >Aucun professeur ne peut être sélectionné</p>
                      </div>
                    </div>
                    <p id="error_main_teacher" class="error_message"></p>
                  </div>

                  <div class="component" style="justify-content: center">
                    <label for="teacher">Professeur(s) associé(s) : </label>
                    <button class="button_more" id="button_teacher_plus">+</button>
                  </div>

                  <div v-for="(teacher, t_index) in teachers_list" :key="teacher.id" class="component teacher_row" style="justify-content: center">
                    <div class="teacher_select_container">
                      <input type="text" class="input teacher" required v-model="teacher.value" />

                      <div class="show_teacher" v-show="show_teacher_list[t_index]">
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
                <p id="error_teacher" class="error_message"></p>
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

              <div id="UE_table">
                <table class="ueCoefficient">
                  <tr>
                    <td>U.E. affectée(s) : </td>
                    <td class="display_coef_label" v-for="Ue in getUEFromResource(resource)" :key="Ue">{{ Ue }}</td>
                  </tr>
                  <tr>
                    <td>Coefficient : </td>
                    <td class="display_coef_ue" v-for="coef in getCoefFromResource(resource)" :key="coef">{{ coef }}</td>
                  </tr>
                </table>
              </div>

              <div id="teacher_div">
                <p>Professeur(s) associé(s) :</p>
                <div v-for="teacher in resource.teachers" :key="teacher">
                  <input type="text" class="input" :value="teacher" />
                </div>
              </div>

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
  color: var(--main-theme-tertiary-color);
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
  display: flex;
}

.panel_resource > p {
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
  margin: 0 35px 0 35px;
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

.error_message {
  color: var(--error-color);
  width: 80%;
  text-align: center;
  margin-left: 3.5vw;
}

#teacher_div {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 4vw;
  padding-bottom: 4vw;
}
</style>
