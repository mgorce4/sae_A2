<script setup>
import { onMounted, ref, computed, nextTick, watch } from 'vue'
import axios from 'axios'
import { status } from '../main'

status.value = 'Administration'

let display_add_modify_area = ref(false)
let display_add_ue = ref(false)

const addModifySaeTitle = ref('Bob')
const addModifySdSaeModified = ref(null)
const addModifySaeLabel = ref('')
const addModifySaeApogeeCode = ref('')
const addModifySaeTermCode = ref('')
const addModifySaeHours = ref('')
const addModifySaeHoursAlternance = ref('')
const ue_list = ref([{ id: 1, ue: '', coefficient: '' }])

const checkboxStatus = ref(false)

// Watch checkboxStatus to clear alternance hours when unchecked
watch(checkboxStatus, (newValue) => {
    if (!newValue) {
        // When checkbox is unchecked, clear alternance hours
        addModifySaeHoursAlternance.value = ''
    }
})

const errors = ref({
    label: false,
    apogeeCode: false,
    termCode: false,
    hours: false,
    ueCoefficients: false,
    alternanceHours: false,
})

const saeTableV2 = ref([])
const ueTableV2 = ref([])
console.log('SAE TABLE V2 : ', saeTableV2)
console.log('UE TABLE V2 : ', ueTableV2)

/* Extract ID from hash URL parameters */
const getQueryParam = (param) => {
    const hash = window.location.hash
    const queryString = hash.split('?')[1]
    if (!queryString) return null
    const params = new URLSearchParams(queryString)
    return params.get(param)
}

const semesterNumber = ref(getQueryParam('id'))
console.log('semestre saé : ', semesterNumber.value)

// Filter SAE data by semester
const filteredSaeTableV2 = computed(() => {
    return saeTableV2.value.filter((sae) => {
        return sae.semester == semesterNumber.value
    })
})

// Filter UE data by semester
const filteredUeTableV2 = computed(() => {
    return ueTableV2.value.filter((ue) => {
        return ue.semester == semesterNumber.value
    })
})

const attachAccordionListeners = () => {
    nextTick(() => {
        const acc = document.getElementsByClassName('accordion_mccc')
        for (let i = 0; i < acc.length; i++) {
            // Skip cloning for add-modify-sae to preserve Vue reactivity
            if (acc[i].getAttribute('data-accordion') === 'add-modify-sae') {
                acc[i].addEventListener('click', function () {
                    this.classList.toggle('active')
                    const panel = this.nextElementSibling
                    if (panel.style.maxHeight) {
                        panel.style.maxHeight = null
                    } else {
                        panel.style.maxHeight = panel.scrollHeight + 'vw'
                        panel.style.padding = '0vw 0vw 0vw'
                    }
                })
            } else {
                const newElement = acc[i].cloneNode(true)
                acc[i].parentNode.replaceChild(newElement, acc[i])

                newElement.addEventListener('click', function () {
                    this.classList.toggle('active')
                    const panel = this.nextElementSibling
                    if (panel.style.maxHeight) {
                        panel.style.maxHeight = null
                    } else {
                        // Calculate the actual height including error messages
                        panel.style.maxHeight = panel.scrollHeight + 'vw'
                        panel.style.padding = '0vw 0vw 0vw'
                    }
                })
            }
        }
    })
}

// Prevent typing invalid characters in number inputs
const preventInvalidChars = (event) => {
    const invalidChars = ['e', 'E', '+', '-', ',']
    if (invalidChars.includes(event.key)) {
        event.preventDefault()
    }
}

onMounted(async () => {
    const pathId = parseInt(getQueryParam('pathId'))
    const institutionId = parseInt(localStorage.idInstitution)

    if (!pathId || isNaN(pathId)) {
        console.error('PathId manquant ou invalide')
        alert('Erreur: Parcours non spécifié. Retour à la sélection des parcours.')
        window.location.hash = '#/mccc-select-path'
        return
    }

    if (!institutionId || isNaN(institutionId)) {
        console.error('Institution ID manquant ou invalide')
        alert('Erreur: Institution non définie. Veuillez vous reconnecter.')
        return
    }

    // Charger les SAE et UE filtrées par path
    const response = await axios.get(`http://localhost:8080/api/v2/mccc/saes/path/${pathId}`)
    // Filtrer par institution pour sécurité supplémentaire
    saeTableV2.value = response.data.filter((sae) => sae.institutionId === institutionId)

    const responseUe = await axios.get(`http://localhost:8080/api/v2/mccc/ues/path/${pathId}`)
    // Filtrer par institution et trier
    ueTableV2.value = responseUe.data
        .filter((ue) => ue.institutionId === institutionId)
        .sort((a, b) => a.label.localeCompare(b.label))

    console.log(`SAE et UE chargées pour institution ${institutionId} et path ${pathId}:`, {
        saes: saeTableV2.value.length,
        ues: ueTableV2.value.length,
    })

    attachAccordionListeners()

    document.getElementById('save').addEventListener('click', () => {
        errors.value.ueCoefficients = false
        document.getElementById('error_ue').innerHTML = ''

        // Variabble for the messages
        let ues = document.querySelectorAll('#ue_select')
        let coefs = document.querySelectorAll('#coefficient')

        // add ues and coefs to the list
        for (let i = 0; i < ue_list.value.length; i++) {
            ue_list.value[i].ue = ues[i].value
            ue_list.value[i].coefficient = coefs[i].value
        }

        for (let i = 0; i < ue_list.value.length; i++) {
            if (ue_list.value[i].coefficient === '') {
                errors.value.ueCoefficients = true
                document.getElementById('error_ue').innerHTML =
                    'Le coefficient de chaque UE est obligatoire'
            }
        }

        if (ue_list.value.length >= 1) {
            for (let index1 = 0; index1 < ue_list.value.length; index1++) {
                if (ue_list.value[index1].ue === '') {
                    document.getElementById('error_ue').innerHTML =
                        'Les UE doivent être sélectionnées'
                }

                // verify if the same UE is selected multiple times
                for (let index2 = index1 + 1; index2 < ue_list.value.length; index2++) {
                    if (
                        ue_list.value[index1].ue === ue_list.value[index2].ue &&
                        ue_list.value[index1].ue !== ''
                    ) {
                        errors.value.ueCoefficients = true
                        document.getElementById('error_ue').innerHTML =
                            'Une UE ne peut pas être affectée plusieurs fois'
                    }
                }
            }
        }
        console.log('UE LIST : ', ue_list.value)
    })

    document.addEventListener('click', (event) => {
        if (event.target.id === 'button_ue_minus') {
            errors.value.ueCoefficients = false
            document.getElementById('error_ue').innerHTML = ''
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
            errors.value.ueCoefficients = false
            document.getElementById('error_ue').innerHTML = ''
            if (filteredUeTableV2.value.length > ue_list.value.length) {
                /* generate new unique id */
                let id
                if (ue_list.value.length > 0) {
                    /* get the max id and add 1 */
                    id = Math.max(...ue_list.value.map((u) => u.id)) + 1
                } else {
                    /* else it's the first id */
                    id = 1
                }
                ue_list.value.push({ id: id, ue: '', coefficient: '' })
            } else {
                errors.value.ueCoefficients = true
                document.getElementById('error_ue').innerHTML =
                    'Vous avez sélectionné toutes les UE disponibles pour ce semestre'
            }
        }
    })
})

function saveSae() {
    // Reset errors
    errors.value.label = false
    errors.value.apogeeCode = false
    errors.value.termCode = false
    errors.value.hours = false
    errors.value.ueCoefficients = false
    errors.value.alternanceHours = false
    document.getElementById('error_ue').innerHTML = ''

    // Validation before saving
    let hasErrors = false
    if (!addModifySaeLabel.value) {
        errors.value.label = true
        hasErrors = true
    }
    if (!addModifySaeApogeeCode.value) {
        errors.value.apogeeCode = true
        hasErrors = true
    }
    if (addModifySaeTermCode.value === '') {
        errors.value.termCode = true
        hasErrors = true
    }
    if (addModifySaeHours.value == '' || addModifySaeHours.value <= 0) {
        errors.value.hours = true
        hasErrors = true
    }
    if (checkboxStatus.value) {
        if (addModifySaeHoursAlternance.value == '' || addModifySaeHoursAlternance.value <= 0) {
            errors.value.alternanceHours = true
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
        document.getElementById('error_ue').innerHTML =
            'Au moins une UE avec un coefficient doit être sélectionnée'
        hasErrors = true
    }

    // Check for empty coefficients on selected UEs
    for (let i = 0; i < ue_list.value.length; i++) {
        if (ue_list.value[i].ue !== '' && ue_list.value[i].coefficient === '') {
            errors.value.ueCoefficients = true
            document.getElementById('error_ue').innerHTML =
                'Le coefficient de chaque UE sélectionnée est obligatoire'
            hasErrors = true
        }
        if (ue_list.value[i].ue === '' && ue_list.value[i].coefficient !== '') {
            errors.value.ueCoefficients = true
            document.getElementById('error_ue').innerHTML =
                'Vous devez sélectionner une UE pour chaque coefficient'
            hasErrors = true
        }
    }

    // Check for duplicate UEs
    for (let index1 = 0; index1 < ue_list.value.length; index1++) {
        for (let index2 = index1 + 1; index2 < ue_list.value.length; index2++) {
            if (
                ue_list.value[index1].ue === ue_list.value[index2].ue &&
                ue_list.value[index1].ue !== ''
            ) {
                errors.value.ueCoefficients = true
                document.getElementById('error_ue').innerHTML =
                    'Une UE ne peut pas être affectée plusieurs fois'
                hasErrors = true
            }
        }
    }

    // If data is missing, do not proceed
    if (hasErrors) {
        return
    }

    // Prepare DTO
    const pathId = parseInt(getQueryParam('pathId'))
    const institutionId = parseInt(localStorage.idInstitution)

    const saeDTO = {
        label: addModifySaeLabel.value,
        apogeeCode: addModifySaeApogeeCode.value,
        semester: parseInt(semesterNumber.value),
        institutionId: institutionId,
        termsCode: addModifySaeTermCode.value,
        pathId: pathId,
        hours: parseFloat(addModifySaeHours.value),
        hoursAlternance:
            checkboxStatus.value &&
            addModifySaeHoursAlternance.value !== '' &&
            addModifySaeHoursAlternance.value > 0
                ? parseFloat(addModifySaeHoursAlternance.value)
                : null,
        ueCoefficients: ue_list.value
            .filter((u) => u.ue !== '' && u.coefficient !== '')
            .map((u) => {
                // Find the UE object to get its ID
                const ueObject = filteredUeTableV2.value.find((ueItem) => ueItem.label === u.ue)
                return {
                    ueId: ueObject ? ueObject.ueNumber : null, // Use ueNumber as ueId
                    ueLabel: u.ue, // Keep label for backwards compatibility
                    coefficient: parseFloat(u.coefficient),
                }
            }),
    }

    console.log('SAE DTO à sauvegarder:', saeDTO)

    if (addModifySdSaeModified.value === null) {
        // Create new SAE
        axios
            .post('http://localhost:8080/api/v2/mccc/saes', saeDTO)
            .then(async (response) => {
                console.log('SAE créée:', response.data)
                // Reload SAEs
                const responseReload = await axios.get(
                    `http://localhost:8080/api/v2/mccc/saes/path/${pathId}`,
                )
                saeTableV2.value = responseReload.data.filter(
                    (sae) => sae.institutionId === institutionId,
                )
                // Close add/modify area
                display_add_modify_area.value = false
                // Reset form
                addModifySaeLabel.value = ''
                addModifySaeApogeeCode.value = ''
                addModifySaeHours.value = ''
                addModifySaeTermCode.value = ''
                addModifySaeHoursAlternance.value = ''
                ue_list.value = [{ id: 1, ue: '', coefficient: '' }]
                checkboxStatus.value = false
                attachAccordionListeners()
            })
            .catch((error) => {
                console.error('Erreur lors de la création:', error)
                alert(
                    'Erreur lors de la création de la SAÉ: ' +
                        (error.response?.data || error.message),
                )
            })
    } else {
        // Update existing SAE
        axios
            .put(`http://localhost:8080/api/v2/mccc/saes/${addModifySdSaeModified.value}`, saeDTO)
            .then(async (response) => {
                console.log('SAE modifiée:', response.data)
                // Reload SAEs
                const responseReload = await axios.get(
                    `http://localhost:8080/api/v2/mccc/saes/path/${pathId}`,
                )
                saeTableV2.value = responseReload.data.filter(
                    (sae) => sae.institutionId === institutionId,
                )
                // Close add/modify area
                display_add_modify_area.value = false
                // Reset form
                addModifySdSaeModified.value = null
                addModifySaeLabel.value = ''
                addModifySaeApogeeCode.value = ''
                addModifySaeHours.value = ''
                addModifySaeTermCode.value = ''
                addModifySaeHoursAlternance.value = ''
                ue_list.value = [{ id: 1, ue: '', coefficient: '' }]
                checkboxStatus.value = false
                attachAccordionListeners()
            })
            .catch((error) => {
                console.error('Erreur lors de la modification:', error)
                alert(
                    'Erreur lors de la modification de la SAÉ: ' +
                        (error.response?.data || error.message),
                )
            })
    }
}

async function deleteSae(saeId) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer cette SAÉ ?')) {
        return
    }

    try {
        await axios.delete(`http://localhost:8080/api/v2/mccc/saes/${saeId}`)
        console.log('SAE supprimée:', saeId)

        // Reload SAEs
        const pathId = parseInt(getQueryParam('pathId'))
        const institutionId = parseInt(localStorage.idInstitution)
        const responseReload = await axios.get(
            `http://localhost:8080/api/v2/mccc/saes/path/${pathId}`,
        )
        saeTableV2.value = responseReload.data.filter((sae) => sae.institutionId === institutionId)

        attachAccordionListeners()
    } catch (error) {
        console.error('Erreur lors de la suppression:', error)
        console.error("Détails de l'erreur:", error.response?.data)
        console.error('Status:', error.response?.status)
        const errorMessage =
            typeof error.response?.data === 'string'
                ? error.response.data
                : JSON.stringify(error.response?.data) || error.message
        alert('Erreur lors de la suppression de la SAÉ:\n' + errorMessage)
    }
}

function initAddModifyArea() {
    // Reset errors
    errors.value.label = false
    errors.value.apogeeCode = false
    errors.value.termCode = false
    errors.value.hours = false
    errors.value.ueCoefficients = false
    errors.value.alternanceHours = false

    display_add_modify_area.value = true
    document.getElementById('error_ue').innerHTML = ''

    nextTick(() => {
        // Open accordion after rendering
        const accordions = document.querySelectorAll('[data-accordion="add-modify-sae"]')
        accordions.forEach((accordion) => {
            accordion.classList.add('active')
            const panel = accordion.nextElementSibling
            if (panel) {
                panel.style.maxHeight = panel.scrollHeight + 'vw'
                panel.style.padding = '0vw 0vw 0vw'
            }
        })
    })
}

function addSae() {
    addModifySaeTitle.value = "Ajout d'une nouvelle SAÉ"

    addModifySaeLabel.value = ''
    addModifySaeApogeeCode.value = ''
    addModifySaeHours.value = ''
    addModifySaeTermCode.value = ''

    addModifySaeHoursAlternance.value = ''
    checkboxStatus.value = false

    ue_list.value = [{ id: 1, ue: '', coefficient: '' }]
    display_add_ue.value = false

    initAddModifyArea()
}

function modifySae(sae) {
    addModifySaeTitle.value = "Modification d'une SAÉ"

    addModifySdSaeModified.value = sae.saeId
    addModifySaeLabel.value = sae.label
    addModifySaeApogeeCode.value = sae.apogeeCode
    addModifySaeHours.value = sae.hours
    addModifySaeTermCode.value = sae.termsCode

    addModifySaeHoursAlternance.value = sae.hoursAlternance || ''
    if (sae.hoursAlternance != null && sae.hoursAlternance > 0) {
        checkboxStatus.value = true
    } else {
        checkboxStatus.value = false
    }

    for (let i = 0; i < sae.ueCoefficients.length; i++) {
        ue_list.value[i] = {
            id: i + 1,
            ue: sae.ueCoefficients[i].ueLabel,
            coefficient: sae.ueCoefficients[i].coefficient,
        }
    }
    console.log('UE LIST MODIF SAE : ', ue_list.value)

    display_add_ue.value = false

    initAddModifyArea()
}

const goBack = () => {
    const pathId = parseInt(getQueryParam('pathId'))
    if (pathId && !isNaN(pathId)) {
        window.location.hash = `#/mccc-select-form?pathId=${pathId}`
    } else {
        window.location.hash = '#/mccc-select-form'
    }
}
</script>

<template>
    <div id="form_mccc_sae">
        <div class="return_arrow">
            <button class="back_arrow" @click="goBack">←</button>
            <p>Retour</p>
        </div>
        <div class="background_form_mccc">
            <div class="form">
                <div class="header_form_mccc">
                    <p class="title">Situation d'Apprentissage Évaluée</p>
                </div>

                <div class="dark_bar">
                    <p>Ajouter une SAÉ</p>
                    <button class="button_more" v-on:click="addSae()">+</button>
                </div>

                <form v-show="display_add_modify_area" method="post" v-on:submit.prevent="">
                    <!--Area to add-->
                    <a class="dark_bar accordion_mccc" data-accordion="add-modify-sae">{{
                        addModifySaeTitle
                    }}</a>
                    <div class="panel_form_mccc container-fluid">
                        <div class="left_side container-fluid cfh spa">
                            <div class="container-fluid spb">
                                <label for="label_sae">Nom de la SAÉ :</label>
                                <input
                                    class="mccc_input"
                                    name="label_sae"
                                    type="text"
                                    v-model="addModifySaeLabel"
                                    :placeholder="'...'"
                                />
                            </div>
                            <p class="error_message" v-show="errors.label">
                                Le nom de la SAÉ est obligatoire
                            </p>
                            <div class="container-fluid spb">
                                <label for="apogee_code_sae">Code Apogée :</label>
                                <input
                                    class="mccc_input"
                                    name="apogee_code_sae"
                                    type="text"
                                    v-model="addModifySaeApogeeCode"
                                    :placeholder="'...'"
                                />
                            </div>
                            <p class="error_message" v-show="errors.apogeeCode">
                                Le code Apogée est obligatoire
                            </p>
                            <div class="container-fluid spb">
                                <label for="hours_sae"
                                    >Nombre d'heures (formation initiale) :</label
                                >
                                <input
                                    class="mccc_input"
                                    name="hours_sae"
                                    type="number"
                                    v-model="addModifySaeHours"
                                    :placeholder="'...'"
                                    @keydown="preventInvalidChars"
                                    step="any"
                                />
                            </div>
                            <p class="error_message" v-show="errors.hours">
                                Le nombre d'heures est obligatoire et doit être supérieur à 0
                            </p>
                            <div class="container-fluid spb">
                                <label for="modalite_sae">Modalité :</label>
                                <input
                                    class="mccc_input"
                                    name="modalite_sae"
                                    type="text"
                                    v-model="addModifySaeTermCode"
                                    :placeholder="'...'"
                                />
                            </div>
                            <p class="error_message" v-show="errors.termCode">
                                La modalité est obligatoire
                            </p>

                            <div class="container-fluid spa">
                                <input
                                    class="btn1"
                                    type="reset"
                                    value="Annuler"
                                    @click="
                                        display_add_modify_area = false
                                        display_add_ue = false
                                    "
                                />
                                <input
                                    id="save"
                                    class="btn1"
                                    type="submit"
                                    value="Sauvegarder"
                                    @click="saveSae"
                                />
                            </div>
                        </div>
                        <div class="right_side" style="">
                            <div id="work_study" style="padding: 1vw">
                                <div class="component">
                                    <label class="switch" id="work_study_slider">
                                        <input type="checkbox" v-model="checkboxStatus" />
                                        <span class="slider"></span>
                                    </label>
                                    <p>Alternance</p>
                                </div>
                                <div class="container-fluid spb" id="work_study_hours">
                                    <p>Nombre d'heures (alternance) :</p>
                                    <input
                                        type="number"
                                        class="input input_work_study"
                                        v-model="addModifySaeHoursAlternance"
                                        @keydown="preventInvalidChars"
                                        step="any"
                                        :disabled="!checkboxStatus"
                                    />
                                </div>
                                <p class="error_message" v-show="errors.alternanceHours">
                                    Vous devez saisir un nombre d'heures valide, <br />ou
                                    désélectionner les heures en alternance
                                </p>
                                <!--V2: put comparator with the programme national hour and total alternance -->
                            </div>

                            <div>
                                <div class="component" style="justify-content: center">
                                    <label for="ue_select">UE affectées : </label>

                                    <button class="button_more" id="button_ue_plus">+</button>

                                    <label
                                        for="coefficient"
                                        class="component"
                                        style="margin-top: 7px"
                                        >Coefficient :</label
                                    >
                                </div>
                                <p v-if="ue_list.length <= 0">Aucune UE créée</p>
                                <div v-else>
                                    <div
                                        v-for="ue in ue_list"
                                        :key="ue.id"
                                        class="component ue_div"
                                        style="margin-bottom: 1vw; margin-left: 5.9vw"
                                    >
                                        <select id="ue_select" class="input" v-model="ue.ue">
                                            <option value="">Sélectionner une UE</option>
                                            <option
                                                v-for="ueOption in filteredUeTableV2"
                                                :key="ueOption.ueNumber"
                                                :value="ueOption.label"
                                            >
                                                {{ ueOption.label }}
                                            </option>
                                        </select>
                                        <button class="button_more" id="button_ue_minus">x</button>
                                        <input
                                            id="coefficient"
                                            type="text"
                                            class="input"
                                            style="margin-top: 4px"
                                            v-model="ue.coefficient"
                                        />
                                    </div>
                                </div>
                                <p id="error_ue" class="error_message"></p>
                            </div>
                        </div>
                    </div>
                </form>

                <p
                    v-if="filteredSaeTableV2.length > 0"
                    style="color: var(--main-theme-secondary-color); font-size: 1.5vw"
                >
                    SAÉ créées :
                </p>
                <p v-else style="color: var(--main-theme-secondary-color); font-size: 1.5vw">
                    Aucune SAÉ n'a été créée
                </p>

                <!-- Display existing SAEs for the semester -->
                <div
                    v-for="(value, index) in filteredSaeTableV2"
                    v-bind:key="index"
                    class="added_content_mccc"
                >
                    <a class="dark_bar accordion_mccc">{{ value.label }}</a>
                    <div class="panel_form_mccc container-fluid">
                        <div class="left_side container-fluid cfh spa">
                            <div class="container-fluid spb">
                                <label>Code apogee : </label>
                                <p class="mccc_input">{{ value.apogeeCode }}</p>
                            </div>
                            <div class="container-fluid spb">
                                <label>Nombre d'heures (formation initiale) : </label>
                                <p class="mccc_input">{{ value.hours }}</p>
                            </div>
                            <div class="container-fluid spb">
                                <label>Modalité : </label>
                                <p class="mccc_input">{{ value.termsCode }}</p>
                            </div>
                            <div class="container-fluid spa">
                                <input
                                    id="btn_cancel_UE"
                                    class="btn1"
                                    type="reset"
                                    value="Supprimer"
                                    @click="deleteSae(value.saeId)"
                                />
                                <input
                                    id="btn_save_UE"
                                    class="btn1"
                                    type="button"
                                    value="Modifier"
                                    @click="modifySae(value)"
                                />
                            </div>
                        </div>
                        <div class="right_side container-fluid cfh spa">
                            <div
                                id="work_study"
                                v-show="value.hoursAlternance"
                                style="padding: 1vw"
                            >
                                <p style="font-weight: bold">Alternance</p>
                                <div class="container-fluid spb" id="work_study_hours">
                                    <p>Nombre d'heures (alternance) :</p>
                                    <p class="mccc_input input_work_study">
                                        {{ value.hoursAlternance }}
                                    </p>
                                </div>
                            </div>
                            <table class="ueCoefficient">
                                <tr>
                                    <td>U.E. affectée(s) :</td>
                                    <th
                                        class="display_coef_label"
                                        v-for="(labelUe, index2) in value.ueCoefficients"
                                        v-bind:key="index2"
                                    >
                                        {{ labelUe.ueLabel }}
                                    </th>
                                </tr>
                                <tr>
                                    <td>Coefficient :</td>
                                    <td
                                        class="display_coef_ue"
                                        v-for="(coefUe, index3) in value.ueCoefficients"
                                        v-bind:key="index3"
                                    >
                                        {{ coefUe.coefficient }}
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style>
#form_mccc_sae {
    margin: 3vw 14vw;
    justify-content: center;
}

.left_side {
    width: 45%;
    margin: 1vw 4vw 1vw 2vw;
}

.right_side {
    margin: 1vw;
    flex-wrap: wrap;
    margin: 1vw;
}

.mccc_input {
    border: 0.1vw solid var(--main-theme-tertiary-color);
    background-color: var(--div-rect-background-color);
    border-radius: 0.5vw;
    padding: 0.5vw;
    margin: 0.5vw 0;
    width: fit-content;
    min-width: 10vw;
    text-align: center;
    color: var(--main-theme-secondary-color);
}

.mccc_input::placeholder,
.display_coef_ue::placeholder {
    color: var(--main-theme-secondary-color);
    opacity: 0.7;
}

.ueCoefficient {
    border-collapse: separate;
    border-spacing: 0.6vw;
    height: 3.5vw;
    text-align: center;
    align-items: center;
}

.display_coef_label,
.display_coef_ue {
    width: 2vw;
    height: 1vw;
    border: 0.1vw solid var(--main-theme-tertiary-color);
    background-color: var(--div-rect-background-color);
    padding: 0.5vw;
}

.display_coef_label {
    border-top-left-radius: 0.8vw;
    border-top-right-radius: 0.8vw;
}

.display_coef_ue {
    border-bottom-left-radius: 0.8vw;
    border-bottom-right-radius: 0.8vw;
}

.select_ue {
    height: 2vw;
    border: 0.1vw solid var(--main-theme-tertiary-color);
    background-color: var(--div-rect-background-color);
    border-top-left-radius: 0.8vw;
    border-top-right-radius: 0.8vw;
    text-align: center;
    color: var(--main-theme-secondary-color);
}

.button_ue {
    width: 2vw !important;
    height: 2vw !important;
    margin: 0 auto;
}
</style>
