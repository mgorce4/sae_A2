<script setup>
import { status } from '../main'
import { onMounted, ref, nextTick, watch, computed } from 'vue'
import axios from 'axios'

status.value = 'Administration'



let display_more_area = ref(false)
let is_modifying = ref(false)
let resource_id_to_modify = ref(null)
let checkboxAlternanceStatus = ref(false)

const resource_sheets = ref([])
const resources = ref([])
const saes = ref([])

const UEs = ref([])

const resource_name = ref('')

const access_rights = ref([])

const path = ref([])

const ue_list = ref([{id : 1, ue: '', coefficient: ''}])
/* value link with the v-model */
const teachers_list = ref([{ id: 1, value: '' }])
const main_teachers_list = ref([{ id: 1, value: '' }])

// Errors object for validation
const errors = ref({
  label: false,
  name: false,
  apogeeCode: false,
  terms: false,
  hours: false,
  alternanceHours: false,
  ueCoefficients: false,
  mainTeacher : false,
    teacher : false
})

// Watch checkboxAlternanceStatus to clear alternance hours when unchecked
watch(checkboxAlternanceStatus, (newValue) => {
  if (!newValue) {
    CM_work_study.value = undefined
    TD_work_study.value = undefined
    TP_work_study.value = undefined
  }
})
// Extract ID from hash URL parameters
const getQueryParam = (param) => {
    const hash = window.location.hash
    const queryString = hash.split('?')[1]
    if (!queryString) return null
    const params = new URLSearchParams(queryString)
    return params.get(param)
}

const pathId = ref(null)
const semesterNumber = ref(null)
const institutionId = ref(null)


const access_right_teacher = 1

// UE filtrées par semestre (comme pour les SAE)
const filteredUeTableV2 = computed(() => {
    return UEs.value.filter(
        ue => ue.semester == semesterNumber.value
    )
})

/*
* to show or hide the list of teachers
* use an array to manage multiple inputs
* to display
* ex : if the page has 3 teacher inputs
* we use an index to see if the list is to be shown or not
*/
const show_teacher_list = ref([false])
const show_main_teacher_list = ref([false])

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

function addMainTeacherEvents(div) {

    /* get the input and the list */
    const input = div.querySelector('.main_teacher')
    const list = div.querySelector('.show_main_teacher')

    /* get the index of the div */
    const get_index = () => Array.from(document.querySelectorAll('.main_teacher_select_container')).indexOf(div)

    input.addEventListener('focus', () => {
        const index = get_index()
        if (index !== -1) {
            show_main_teacher_list.value[index] = true
        }
    })

    input.addEventListener('blur', () => {
        const index = get_index()
        if (index !== -1) {
            show_main_teacher_list.value[index] = false
        }
    })

    list.addEventListener('mouseover', (event) => {
        if (event.target.classList && event.target.classList.contains('main_teacher_name')) {
            input.value = event.target.innerText
            // keep the reactive source of truth in sync
            const index = get_index()
            if (index !== -1 && main_teachers_list.value[index]) {
                main_teachers_list.value[index].value = event.target.innerText
            }
        }
    })

    list.addEventListener('click', (event) => {
        if (event.target.classList && event.target.classList.contains('main_teacher_name')) {
            input.value = event.target.innerText
            const index = get_index()
            if (index !== -1 && main_teachers_list.value[index]) {
                main_teachers_list.value[index].value = event.target.innerText
            }
            if (index !== -1) {
                show_main_teacher_list.value[index] = false
            }
        }
    })
}

// Fonction pour modifier une ressource
async function modifyResource(resource) {
    console.log('=== MODIFICATION RESSOURCE ===', resource)

    // D'abord ouvrir le formulaire et passer en mode modification
    is_modifying.value = true
    resource_id_to_modify.value = resource.resourceId
    display_more_area.value = true

  // Attendre que le DOM soit mis à jour
  await nextTick()

  // Charger les données de la ressource dans le formulaire
  resource_label.value = resource.resourceLabel || ''
  resource_name.value = resource.resourceName || ''
  apogee_code.value = resource.resourceApogeeCode || ''
  terms.value = resource.terms || ''

  // Charger les heures (il faut récupérer depuis l'API car resource n'a pas toutes les données)
  try {
    const hoursResponse = await axios.get(`http://localhost:8080/api/hours-per-student/resource/${resource.resourceId}`)
    const hours = hoursResponse.data

    // Trouver les heures formation initiale (has_alternance = false)
    const hoursInitial = hours.find(h => h.hasAlternance === false)
    if (hoursInitial) {
      CM_initial_formation.value = hoursInitial.cm
      TD_initial_formation.value = hoursInitial.td
      TP_initial_formation.value = hoursInitial.tp
    }

    // Trouver les heures alternance (has_alternance = true)
    const hoursAlt = hours.find(h => h.hasAlternance === true)
    if (hoursAlt) {
      checkboxAlternanceStatus.value = true
      CM_work_study.value = hoursAlt.cm
      TD_work_study.value = hoursAlt.td
      TP_work_study.value = hoursAlt.tp
    } else {
      checkboxAlternanceStatus.value = false
      CM_work_study.value = undefined
      TD_work_study.value = undefined
      TP_work_study.value = undefined
    }

    // Charger le professeur principal (maintenant que le DOM est prêt)
    const mainTeacherResponse = await axios.get(`http://localhost:8080/api/main-teachers-for-resource/resource/${resource.resourceId}`)
    if (mainTeacherResponse.data && mainTeacherResponse.data.length > 0) {
      const mainTeacher = mainTeacherResponse.data[0]
      const mainTeacherInput = document.getElementById('main_teacher')
      if (mainTeacherInput) {
        mainTeacherInput.value = `${mainTeacher.user.firstname} ${mainTeacher.user.lastname}`
      }
    } else {
      const mainTeacherInput = document.getElementById('main_teacher')
      if (mainTeacherInput) {
        mainTeacherInput.value = ''
      }
    }

    // Charger les professeurs associés
    const teachersResponse = await axios.get(`http://localhost:8080/api/teachers-for-resource/resource/${resource.resourceId}`)
    if (teachersResponse.data && teachersResponse.data.length > 0) {
      teachers_list.value = teachersResponse.data.map((t, index) => ({
        id: index + 1,
        value: `${t.user.firstname} ${t.user.lastname}`
      }))
      // Réinitialiser show_teacher_list avec la même taille
      show_teacher_list.value = teachersResponse.data.map(() => false)
    } else {
      teachers_list.value = [{ id: 1, value: '' }]
      show_teacher_list.value = [false]
    }

    // Charger les coefficients UE
    const coeffResponse = await axios.get(`http://localhost:8080/api/ue-coefficients/resource/${resource.resourceId}`)
    if (coeffResponse.data && coeffResponse.data.length > 0) {
      ue_list.value = coeffResponse.data.map((coeff, index) => ({
        id: index + 1,
        ue: coeff.ue.label,
        coefficient: coeff.coefficient
      }))
    } else {
      ue_list.value = [{ id: 1, ue: '', coefficient: '' }]
    }

    // Attendre une frame supplémentaire puis réattacher les événements et scroller
    await nextTick()

    // Réattacher les événements pour les professeurs associés
    const containers = document.querySelectorAll('.teacher_select_container')
    containers.forEach(container => {
      addTeacherEvents(container)
    })

    // Scroll vers le formulaire
    document.getElementById('dark_bar').scrollIntoView({ behavior: 'smooth' })

  } catch (error) {
    console.error('❌ Erreur lors du chargement de la ressource:', error)
    alert('Erreur lors du chargement de la ressource pour modification')
  }
}

// Fonction pour supprimer une ressource
async function deleteResource(resourceId) {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette ressource ?')) {
    return
  }

  try {
    console.log('=== SUPPRESSION RESSOURCE ===', resourceId)

    // Le backend gère tout automatiquement
    await axios.delete(`http://localhost:8080/api/v2/mccc/resources/${resourceId}`)

    console.log('✅ Ressource supprimée')

    // Reload resource sheets
    const reloadResponse = await axios.get('http://localhost:8080/api/v2/resource-sheets')
    resource_sheets.value = reloadResponse.data

    alert('Ressource supprimée avec succès !')

  } catch (error) {
    console.error('❌ Erreur lors de la suppression:', error)
    alert('Erreur lors de la suppression de la ressource: ' + (error.response?.data || error.message))
  }
}

// Helper function to safely set error message
function setErrorMessage(elementId, message) {
  const element = document.getElementById(elementId)
  if (element) {
    element.innerHTML = message
  } else {
    console.warn(`Error element not found: ${elementId}`)
  }
}

// Fonction de sauvegarde de ressource - Comme saveSae
async function saveResource() {

  /* display errors messages */

  console.log('=== SAUVEGARDE RESSOURCE ===')

  // Reset errors
  Object.keys(errors.value).forEach(key => errors.value[key] = false)
  document.querySelectorAll('.error_message').forEach(el => el.innerHTML = '')

  // Validation
  let hasErrors = false

  if (!resource_label.value) {
    errors.value.label = true
    setErrorMessage("error_resource_label", "L'intitulé de la ressource est obligatoire")
    hasErrors = true
  }
  if (!resource_name.value) {
    errors.value.name = true
    setErrorMessage("error_resource_name", "Le nom de la ressource est obligatoire")
    hasErrors = true
  }
  if (!apogee_code.value) {
    errors.value.apogeeCode = true
    setErrorMessage("error_apogee_code", "Le code apogée est obligatoire")
    hasErrors = true
  }
  if (terms.value === '') {
    errors.value.terms = true
    setErrorMessage("error_terms", "Les modalités sont obligatoires")
    hasErrors = true
  }
  if (!CM_initial_formation.value && !TD_initial_formation.value && !TP_initial_formation.value) {
    errors.value.hours = true
    setErrorMessage("error_initial_formation", "Les heures de la formation innitiale sont obligatoire")
    hasErrors = true
  }

  // Validate alternance hours if checkbox is checked
  if (checkboxAlternanceStatus.value) {
    if (!CM_work_study.value && !TD_work_study.value && !TP_work_study.value) {
      errors.value.alternanceHours = true
      setErrorMessage("error_work_study", "Les heures de l'alternance sont obligatoire")
      hasErrors = true
    }
  }

  // Validate UE coefficients
  const ues = document.querySelectorAll('#ue_select')
  const coefs = document.querySelectorAll('#coefficient')

  // Update ue_list with current values
  for (let i = 0; i < ue_list.value.length; i++) {
    ue_list.value[i].ue = ues[i].value
    ue_list.value[i].coefficient = coefs[i].value
  }

  // Check if at least one UE is selected
  let hasValidUE = false
  for (let i = 0; i < ue_list.value.length; i++) {
    if (ue_list.value[i].ue !== '' && ue_list.value[i].coefficient !== '') {
      hasValidUE = true
      break
    }
  }

  if (!hasValidUE) {
    errors.value.ueCoefficients = true
    setErrorMessage('error_ue', "Au moins une UE avec un coefficient doit être sélectionnée")
    hasErrors = true
  }

  // Check for empty coefficients on selected UEs
  for (let i = 0; i < ue_list.value.length; i++) {
    if (ue_list.value[i].ue !== '' && ue_list.value[i].coefficient === '') {
      errors.value.ueCoefficients = true
      setErrorMessage('error_ue', "Le coefficient de chaque UE sélectionnée est obligatoire")
      hasErrors = true
    }
    if (ue_list.value[i].ue === '' && ue_list.value[i].coefficient !== '') {
      errors.value.ueCoefficients = true
      setErrorMessage('error_ue', "Vous devez sélectionner une UE pour chaque coefficient")
      hasErrors = true
    }
  }

  // Check for duplicate UEs
  for (let index1 = 0; index1 < ue_list.value.length; index1++) {
    for (let index2 = index1 + 1; index2 < ue_list.value.length; index2++) {
      if (ue_list.value[index1].ue === ue_list.value[index2].ue && ue_list.value[index1].ue !== '') {
        errors.value.ueCoefficients = true
        setErrorMessage('error_ue', "Une UE ne peut pas être affectée plusieurs fois")
        hasErrors = true
      }
    }
  }

  let teacher_names = []

  for (let i = 0; i < teachers_list.value.length; i++) {
    if (teachers_list.value[i].value && teachers_list.value[i].value.trim() !== '') {
      teacher_names.push(teachers_list.value[i].value.trim())
    }
  }

  const main_teacher_input = document.getElementById('main_teacher')
  const main_teacher_value = main_teacher_input ? main_teacher_input.value.trim() : ''

  if (main_teacher_value) {
    teacher_names.push(main_teacher_value)
  }

  // Check for duplicate teacher names
  for (let i = 0; i < teacher_names.length; i++) {
    for (let j = 0; j < teacher_names.length; j++) {
      if (isTeacherNamesEquals(i, j, teacher_names)) {
        errors.value.teacher = true
        setErrorMessage("error_teacher", "Un même professeur ne peut pas être affecté plusieurs fois")
        hasErrors = true
      }
    }
  }

  if (!main_teacher_value) {
    errors.value.mainTeacher = true
    setErrorMessage(
      "error_main_teacher",
      "Le professeur référent est obligatoire"
    )
    hasErrors = true
  }

  if (hasErrors) {
    console.log('❌ Erreurs de validation')
    return
  }

  // Prepare DTO
  const pathId = parseInt(getQueryParam('pathId'))
  const institutionId = parseInt(localStorage.idInstitution)

  const resourceDTO = {
    label: resource_label.value,
    name: resource_name.value,
    apogeeCode: apogee_code.value,
    semester: parseInt(getQueryParam('id')),
    institutionId: institutionId,
    termsCode: terms.value,
    pathId: pathId,
    cmInitial: parseFloat(CM_initial_formation.value) || 0,
    tdInitial: parseFloat(TD_initial_formation.value) || 0,
    tpInitial: parseFloat(TP_initial_formation.value) || 0,
    cmAlternance: (checkboxAlternanceStatus.value && CM_work_study.value) ? parseFloat(CM_work_study.value) : null,
    tdAlternance: (checkboxAlternanceStatus.value && TD_work_study.value) ? parseFloat(TD_work_study.value) : null,
    tpAlternance: (checkboxAlternanceStatus.value && TP_work_study.value) ? parseFloat(TP_work_study.value) : null,
    mainTeacher: main_teacher_value,
    teachers: teachers_list.value
      .filter(t => t.value && t.value.trim() !== '')
      .map(t => t.value.trim()),
    ueCoefficients: ue_list.value
      .filter(u => u.ue !== '' && u.coefficient !== '')
        .map(u => {
            const ueObject = filteredUeTableV2.value.find(
                ueItem => ueItem.label === u.ue
            )

            return {
                ueId: ueObject ? ueObject.ueNumber : null,
                ueLabel: u.ue,
                coefficient: parseFloat(u.coefficient)
            }
        })

  }

  console.log('📤 Envoi du DTO ressource:', resourceDTO)

  try {
    if (is_modifying.value && resource_id_to_modify.value) {
      // Update existing resource
      const response = await axios.put(`http://localhost:8080/api/v2/mccc/resources/${resource_id_to_modify.value}`, resourceDTO)
      console.log('✅ Ressource modifiée:', response.data)
    } else {
      // Create new resource
      const response = await axios.post('http://localhost:8080/api/v2/mccc/resources', resourceDTO)
      console.log('✅ Ressource créée:', response.data)
    }
    // 🔁 Recharge la liste des ressources pour affichage immédiat
    const pathIdReload = parseInt(getQueryParam('pathId'))

    const resourcesResponse = await axios.get(
       `http://localhost:8080/api/v2/mccc/resources/path/${pathIdReload}`
    )

    resources.value = resourcesResponse.data

    console.log(
          '📦 Resources brutes:',
          resources.value.map(r => ({
              id: r.resource?.idResource,
              label: r.resource?.label,
              semester: r.resource?.semester,
              pathId: r.resource?.path?.idPath
          }))
      )




      // Reload resource sheets
    const reloadResponse = await axios.get('http://localhost:8080/api/v2/resource-sheets')
    resource_sheets.value = reloadResponse.data

    // Reset form
    resource_label.value = ''
    resource_name.value = ''
    apogee_code.value = ''
    terms.value = ''
    CM_initial_formation.value = undefined
    TD_initial_formation.value = undefined
    TP_initial_formation.value = undefined
    CM_work_study.value = undefined
    TD_work_study.value = undefined
    TP_work_study.value = undefined
    checkboxAlternanceStatus.value = false
    ue_list.value = [{ id: 1, ue: '', coefficient: '' }]
    teachers_list.value = [{ id: 1, value: '' }]

    const mainTeacherInput = document.getElementById('main_teacher')
    if (mainTeacherInput) {
      mainTeacherInput.value = ''
    }

    is_modifying.value = false
    resource_id_to_modify.value = null
    display_more_area.value = false

    console.log('✅ Ressource sauvegardée avec succès')

  } catch (error) {
    console.error('❌ Erreur lors de la sauvegarde:', error)
    alert('Erreur lors de la sauvegarde de la ressource: ' + (error.response?.data || error.message))
  }
}

onMounted(async () => {
    pathId.value = parseInt(getQueryParam('pathId'))
    semesterNumber.value = parseInt(getQueryParam('id'))
    institutionId.value = parseInt(localStorage.idInstitution)

    if (!pathId.value || isNaN(pathId.value)) {
        alert("Erreur : pathId manquant")
        return
    }

    if (!semesterNumber.value || isNaN(semesterNumber.value)) {
        alert("Erreur : semestre manquant")
        return
    }

    if (!institutionId.value || isNaN(institutionId.value)) {
        alert("Erreur : institution manquante")
        return
    }



  await Promise.all([
    axios
      .get('http://localhost:8080/api/v2/resource-sheets')
      .then((reponse) => (resource_sheets.value = reponse.data)),
    axios
      .get(`http://localhost:8080/api/v2/mccc/resources/path/${pathId.value}`)
      .then((response) => (resources.value = response.data)),
    axios
      .get(`http://localhost:8080/api/v2/mccc/saes/path/${pathId.value}`)
      .then((response) => saes.value = response.data),
    axios
      .get(`http://localhost:8080/api/v2/mccc/ues/path/${pathId.value}`)
      .then((response) => {
        // Filtrer par institution pour sécurité supplémentaire
        UEs.value = response.data.filter(ue => ue.institutionId === institutionId.value);
        console.log(`UEs chargées pour institution ${institutionId.value} et path ${pathId.value}:`, UEs.value.length);
      }),
    axios
      .get('http://localhost:8080/api/access-rights')
      .then((response) => (access_rights.value = response.data)),
    axios
      .get('http://localhost:8080/api/paths')
      .then((response) => (path.value = response.data)),
  ])

  access_rights.value = access_rights.value.filter((ar) => ar.user.institution.idInstitution == localStorage.idInstitution).filter((ar) => ar.accessRight == access_right_teacher)
  saes.value = saes.value.filter((saes) => saes.semester == getQueryParam('id'))

    /*test*/
  access_rights.value.push({ user: { firstname: 'Aucun', lastname: '' } }) // to allow empty teacher
  access_rights.value.push({ user: { firstname: 'Autre', lastname: '' } }) // to allow "other" teacher
  access_rights.value.push({ user: { firstname: 'Intervenant externe', lastname: '' } }) // to allow "external" teacher

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

  // Lier la fonction de sauvegarde au bouton save
  document.getElementById('save').addEventListener('click', saveResource)

  // Le switch est géré par v-model et watch, pas besoin d'écouteur manuel

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

    } else if (event.target.id === 'button_ue_plus') {

      if (getUEsByInstitution().length <= ue_list.value.length) {
        setErrorMessage("error_ue", "Vous ne pouvez pas ajouter plus d'UEs car vous avez déjà séléctionné toutes les UE disponibles")
        return
      }

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

    } else if (event.target.id === 'button_teacher_plus') {

      if (access_rights.value.length - main_teachers_list.value.length <= teachers_list.value.length) {
        setErrorMessage("error_teacher", "Vous ne pouvez pas ajouter plus de professeurs car il n'y a plus de professeurs disponibles")
        return
      }

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
        const teachers = document.querySelectorAll('.teacher_row')

        let index_to_remove = -1

        teachers.forEach((div, index) => {
          if (div.contains(event.target)) {
            index_to_remove = index
          }
        })

      if (index_to_remove !== -1 && teachers_list.value.length > 1) {
        teachers_list.value = teachers_list.value.filter((_, i) => i !== index_to_remove)
        show_teacher_list.value = show_teacher_list.value.filter((_, i) => i !== index_to_remove)
      }
     } else if (event.target.id === 'button_main_teacher_plus') {

        if (access_rights.value.length - 1 <= main_teachers_list.value.length) {
            setErrorMessage("error_teacher", "Vous ne pouvez pas ajouter plus de professeurs car il n'y a plus de professeurs disponibles")
            return
        }

        // add a new teacher entry
        let id
        if (main_teachers_list.value.length > 0) {
            id = Math.max(...main_teachers_list.value.map(t => t.id)) + 1
        } else {
            id = 1
        }
        main_teachers_list.value.push({ id: id, value: '' })
        show_main_teacher_list.value.push(false)

        nextTick(() => {
            const containers = document.querySelectorAll('.main_teacher_select_container')
            /* attach events to the last added container */
            const new_container = containers[containers.length - 1]
            if (new_container) addMainTeacherEvents(new_container)
        })

    } else if (event.target.id === 'button_main_teacher_cross') {
        const teachers = document.querySelectorAll('.main_teacher_row')

        let index_to_remove = -1

        teachers.forEach((div, index) => {
            if (div.contains(event.target)) {
                index_to_remove = index
            }
        })

        if (index_to_remove !== -1 && main_teachers_list.value.length > 1) {
            main_teachers_list.value = main_teachers_list.value.filter((_, i) => i !== index_to_remove)
            show_main_teacher_list.value = show_main_teacher_list.value.filter((_, i) => i !== index_to_remove)
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

  const div_main_teacher_container = document.querySelectorAll('.main_teacher_select_container')
  div_main_teacher_container.forEach((div) => {
    /* add event to the new div */
      addMainTeacherEvents(div)
  })
})

function getUEsByInstitution() {
    return UEs.value.filter((ue) => ue.institutionId == localStorage.idInstitution)
}

function getResourcesBySemester() {
  return resources.value
    .filter((res) => res.semester == getQueryParam('id'))
    .filter((res) => res.pathId == getQueryParam('pathId'))
}

function getUEFromResource(resource) {
  let ues = []

  resource.ueCoefficients.map((ue) => {
    ues.push(ue.ueLabel)
  })

  return ues
}

function getCoefFromResource(resource) {
  let coefs = []

  resource.ueCoefficients.map((ue) => {
    coefs.push(ue.coefficient)
  })

  return coefs
}

function isTeacherNamesEquals(i, j, teacher_names) {
  return i!== j && teacher_names[i] === teacher_names[j] && teacher_names[i] !== '' && teacher_names[j] !== ''
}

const preventInvalidChars = (event) => {
  const invalidChars = ['e', 'E', '+', '-', ',']
  if (invalidChars.includes(event.key)) {
    event.preventDefault()
  }
}

function getPath() {
  return path.value.filter((p) => p.idPath == getQueryParam('pathId'))
}

const goBack = () => {
  const pathId = parseInt(getQueryParam('pathId'));
  if (pathId && !isNaN(pathId)) {
    window.location.hash = `#/mccc-select-form?pathId=${pathId}`;
  } else {
    window.location.hash = '#/mccc-select-form';
  }
}

total_initial_formation.value = computed(() => {
  const cm = parseFloat(CM_initial_formation.value) || 0
  const td = parseFloat(TD_initial_formation.value) || 0
  const tp = parseFloat(TP_initial_formation.value) || 0
  return cm + td + tp
})

total_work_study.value = computed(() => {
  const cm = parseFloat(CM_work_study.value) || 0
  const td = parseFloat(TD_work_study.value) || 0
  const tp = parseFloat(TP_work_study.value) || 0
  return cm + td + tp
})
async function reloadResources() {
    const response = await axios.get(
        `http://localhost:8080/api/v2/mccc/resources/path/${pathId.value}`
    )

    resources.value = response.data.filter(
        r => r.institutionId === institutionId.value
    )
}



</script>

<template>
  <div id="ressource">
    <div id="return_arrow">
      <button id="back_arrow"  @click="goBack">←</button>
      <p>Retour</p>
    </div>

    <div id="background_form">
      <div id="form">
        <div id="header_ressource">
          <p id="title">Ressources</p>
        </div>

        <div id="dark_bar">
          <h2>{{ is_modifying ? 'Modifier une ressource' : 'Ajouter une ressource' }}</h2>
          <button id="button_more" v-on:click="display_more_area = !display_more_area">{{ display_more_area ? '-' : '+' }}</button>
        </div>

        <a class="accordion" id="dark_bar" style="width: 97%" v-show="display_more_area" method="post" v-on:submit.prevent="">
          {{ is_modifying ? 'Modification de la ressource :' : 'Ajout d\'une ressource :' }}
        </a>

        <div class="panel_resource" v-show="display_more_area">
          <div id="left">
            <div>
              <label for="resource_label">Intitulé de la ressource : </label>
              <input id="resource_label" type="text" class="input" v-model="resource_label"/>
            </div>
            <p id="error_resource_label" class="error_message"></p>

            <div>
              <label for="resource_name">Nom de la ressource : </label>
              <input id="resource_name" type="text" class="input" v-model="resource_name"/>
            </div>
            <p id="error_resource_name" class="error_message"></p>

            <div>
              <label for="apogee_code">Code apogée : </label>
              <input id="apogee_code" type="text" class="input" v-model="apogee_code"/>
            </div>
            <p id="error_apogee_code" class="error_message"></p>

            <div>
              <label>Modalités : </label>
              <input id="terms" type="text" class="input" v-model="terms"/>
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
                      <input type="number" class="input" v-model="CM_initial_formation" @keydown="preventInvalidChars"/>
                    </td>
                    <td>
                      <input type="number" class="input" v-model="TD_initial_formation" @keydown="preventInvalidChars"/>
                    </td>
                    <td>
                      <input type="number" class="input" v-model="TP_initial_formation" @keydown="preventInvalidChars"/>
                    </td>
                  </tr>
                </tbody>
              </table>

              <p>Nombre d'heures totales : {{ total_initial_formation }}</p>
            </div>
            <p id="error_initial_formation" class="error_message"></p>

            <div id="sae_container">
              <p>SAE liées : </p>
              <div class="component">
                <div v-for="sae in saes" :key="sae.saeId" class="component sae_item">
                  <label class="switch" >
                    <input type="checkbox"/>
                    <span class="slider"></span>
                  </label>
                  <label>{{ sae.label }}</label>
                </div>
              </div>
            </div>

            <div id="btn">
              <input class="btn1" type="reset" value="Annuler" />
                <input
                    id="save"
                    class="btn1"
                    type="button"
                    value="Sauvegarder"
                    @click="saveResource"
                />

            </div>
          </div>

          <div id="right">
            <div id="work_study">
              <div class="component">
                <label class="switch" id="work_study_slider">
                  <input type="checkbox" v-model="checkboxAlternanceStatus" />
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
                        <input type="number" class="input input_work_study" v-model="CM_work_study" @keydown="preventInvalidChars" :disabled="!checkboxAlternanceStatus"/>
                      </td>
                      <td>
                        <input type="number" class="input input_work_study" v-model="TD_work_study" @keydown="preventInvalidChars" :disabled="!checkboxAlternanceStatus"/>
                      </td>
                      <td>
                        <input type="number" class="input input_work_study" v-model="TP_work_study" @keydown="preventInvalidChars" :disabled="!checkboxAlternanceStatus"/>
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
                      <input id="coefficient" type="number" class="input" style="margin-top: 4px" v-model="ue.coefficient" @keydown="preventInvalidChars" />
                    </div>
                  </div>
                  <p id="error_ue" class="error_message"></p>
                </div>

                <div style="margin-top: 5px">

                    <div class="component" style="justify-content: center">
                        <label for="teacher">Professeur(s) référent(s) : </label>
                        <button class="button_more" id="button_main_teacher_plus">+</button>
                    </div>

                    <div v-for="(main_teacher, t_index) in main_teachers_list" :key="main_teacher.id" class="component main_teacher_row" style="justify-content: center">
                        <div class="main_teacher_select_container">
                            <input type="text" class="input main_teacher" required v-model="main_teacher.value" />

                            <div class="show_main_teacher" v-show="show_main_teacher_list[t_index]">
                                <div v-if="access_rights.length > 0">
                                    <div class="main_teacher_name" v-for="acr in access_rights" :key="acr">
                                        {{acr.user.firstname}} {{acr.user.lastname}}
                                    </div>
                                </div>
                                <p v-else >Aucun professeur ne peut être sélectionné</p>
                            </div>
                        </div>

                        <button class="button_more" id="button_main_teacher_cross">x</button>
                    </div>
                    <p id="error_main_teacher" class="error_message"></p>


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
        <p v-if="getResourcesBySemester().length > 0">Ressources créées :</p>
        <p v-else>Aucune ressource n'a été créée</p>

        <div v-for="resource in getResourcesBySemester()" :key="resource.resourceId">
          <a class="accordion" id="dark_bar" style="width: 97%">{{ resource.label }} {{ resource.name }}</a>

          <div class="panel_resource">
            <div id="left_resource">
              <div class="container-fluid">
                <p>Code Apogee :</p>
                <input type="text" class="input" :value="resource.apogeeCode || ''" readonly />
              </div>

              <div class="container-fluid">
                <p>Modalités :</p>
                <input type="text" class="input" :value="resource.termsCode || ''" readonly />
              </div>

              <p>Nombre d'heures (formation initiale) :</p>

              <div class="container-fluid">
                <p>CM :</p>
                <input type="text" class="input" :value="resource.initialCm" readonly />
              </div>

              <div class="container-fluid">
                <p>TD :</p>
                <input type="text" class="input" :value="resource.initialTd" readonly />
              </div>

              <div class="container-fluid">
                <p>TP :</p>
                <input type="text" class="input" :value="resource.initialTp" readonly />
              </div>

              <div class="container-fluid">
                <p>Total : {{resource.initialTotal || 0}}</p>
              </div>

              <div v-if="resources.alternanceCm == 0 && resources.alternanceTd == 0 && resources.alternanceTp == 0 && resources.alternanceTotal == 0">
                <p>Nombre d'heures (alternance) : </p>

                <div class="container-fluid">
                  <p>CM :</p>
                  <input type="text" class="input" :value="resource.alternanceCm || ''" readonly />
                </div>

                <div class="container-fluid">
                  <p>TD :</p>
                  <input type="text" class="input" :value="resource.alternanceTd || ''" readonly />
                </div>

                <div class="container-fluid">
                  <p>TP :</p>
                  <input type="text" class="input" :value="resource.alternanceTp || ''" readonly />
                </div>

                <div class="container-fluid">
                  <p>Total : {{resource.alternanceCm || 0}}</p>
                </div>
              </div>

              <div v-else>
                <p>La ressource n'est pas en alternance</p>
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

              <div id="sae_container">
                <p>SAE liées :</p>
                <div v-if="(resource.saeResources || []).length === 0">
                  <p style="color: #888; font-style: italic;">Aucune SAE liée</p>
                </div>
                <div v-for="saeResource in (resource.saeResources || [])" :key="saeResource.saeId">
                  <input type="text" class="input" :value="saeResource.label" readonly />
                </div>

              </div>

              <div id="teacher_div">
                <p>Professeur référent :</p>
                <input type="text" class="input" :value="resource.mainTeacherName || 'Non renseigné'" readonly />

                <p>Professeur(s) associé(s) :</p>
                <div v-if="(resource.teachers || []).length === 0">
                  <p style="color: #888; font-style: italic;">Aucun professeur associé</p>
                </div>
                <div v-for="teacher in (resource.teachers || [])" :key="teacher">
                  <input type="text" class="input" :value="teacherName" readonly />
                </div>
              </div>

              <div style="display: flex; gap: 10px; justify-content: center">
                <input class="btn1" type="button" value="Supprimer" @click="deleteResource(resource.resourceId)" />
                <br />
                <input class="btn1" type="button" value="Modifier" @click="modifyResource(resource)" />
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
  margin: 2vw;
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

.show_main_teacher {
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


.show_main_teacher::-webkit-scrollbar {
    width: 12px;
}

.show_main_teacher::-webkit-scrollbar-track {
    margin: 1em;
    background: var(--main-theme-secondary-background-color);
    box-shadow: inset 0 0 5px var(--sub-scrollbar-color);
    border-radius: 10px;
}

.show_main_teacher::-webkit-scrollbar-thumb {
    background: var(--main-theme-secondary-color);
    border-radius: 10px;
}

.main_teacher_name {
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
  margin-bottom: 1vw;
  margin-top: 0.5vw;
}

#teacher_div {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 4vw;
  padding-bottom: 4vw;
}

/* Style pour les inputs en lecture seule dans la visualisation */
input[readonly].input {
  background-color: #4B575F !important;
  color: white !important;
  cursor: not-allowed;
  opacity: 1;
}
</style>
