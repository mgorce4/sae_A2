<script setup>
import axios from 'axios';
import { ref, nextTick, onMounted, computed } from 'vue';
const pathId = parseInt(localStorage.pathId)
console.log('path ID from localStorage:', pathId)
const institutionId = parseInt(localStorage.idInstitution)
const ueList = ref([])
const resourceList = ref([])
const saeList = ref([])
const semester = parseInt(localStorage.semester)

const selectedUeCodeApogee = ref(null)
const selectedUeIntitule = ref(null)
const selectedUeCompetenceLevel = ref(null)

function selectUe(ue) {
    selectedUeCodeApogee.value = ue.euApogeeCode
    selectedUeIntitule.value = ue.label
    selectedUeCompetenceLevel.value = ue.competenceLevel
    console.log('Selected UE:', ue)
}

const getQueryParam = (param) => {
    const hash = window.location.hash
    const queryString = hash.split('?')[1]
    if (!queryString) return null
    const params = new URLSearchParams(queryString)
    return params.get(param)
}

function getUESemesterInstitution() {
    const pathId = parseInt(getQueryParam('pathId'))
    const semester = parseInt(getQueryParam('id'))
    const institutionId = parseInt(localStorage.idInstitution)

    console.log('=== FILTRAGE UE POUR AFFICHAGE ===')
    console.log('PathId:', pathId)
    console.log('Semester:', semester)
    console.log('Institution:', institutionId)
    console.log('UE totales dans ueList:', ueList.value.length)

    const filtered = ueList.value.filter((ue) => {
        const matchSemester = ue.semester === semester
        const matchInstitution = ue.institutionId === institutionId
        const matchPath = true

        console.log(`UE ${ue.label}:`, {
            semester: ue.semester,
            matchSemester,
            institutionId: ue.institutionId,
            matchInstitution,
            pathNumber: ue.pathNumber,
            matchPath,
            included: matchSemester && matchInstitution && matchPath,
        })

        return matchSemester && matchInstitution && matchPath
    })

    console.log('UE filtrées pour affichage:', filtered.length)
    return filtered
}

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

onMounted(async () => {
    // Charger les UEs filtrées par path
    const response = await axios.get(`http://localhost:8080/api/v2/mccc/ues/path/${pathId}`)
    // Filtrer par institution seulement (pas par semestre, c'est fait dans getUESemesterInstitution)
    ueList.value = response.data.filter((ue) => ue.institutionId === institutionId)

    const response2 = await axios.get(`http://localhost:8080/api/v2/mccc/resources/path/${pathId}/semester/${semester}`)
    resourceList.value = response2.data.filter((resource) => resource.institutionId === institutionId)

    const response3 = await axios.get(`http://localhost:8080/api/v2/mccc/saes/path/${pathId}`)
    saeList.value = response3.data.filter((sae) => sae.institutionId === institutionId)

    const mccc_table = computed(() => {
        return users.value
            .map((user) => {
                const userRights = access_rights.value.filter((ar) => ar.idUser === user.idUser)
                return {
                    ...user,
                    accessRights: userRights,
                }
            })
            .filter((user) => user.accessRights.length > 0)
    })

    attachAccordionListeners()
})
</script>

<template>
    {{ getUESemesterInstitution() }}<br><br>
    {{ resourceList }}<br><br>
    {{ saeList }}<br><br>
    {{ mccc_table }}
    <div id="display_mccc_page">
        <div id="return_arrow">
            <button id="back_arrow" @click="goBack">←</button>
            <p>Retour</p>
        </div>
        <div class="container-fluid" style="gap: 0px; width: 100%; align-items: start;">
            <div class="container-fluid cfh" style="width: fit-content;">
                <div v-for="(value, index) in getUESemesterInstitution()" :key="index" class="container-fluid">
                    <div class="ue_selection_button" @click="selectUe(value)">{{ value.label }}</div>
                    <div class="display_mccc_triangle"></div>
                </div>
            </div>

            <div class="background_form_mccc" style="padding: 2vw; width: 100%;">
                <p>Code apogee : {{ selectedUeCodeApogee }}</p>
                <p>Intitulé de la compétence : {{ selectedUeIntitule }}</p>
                <p>Niveau de la compétence : {{ selectedUeCompetenceLevel }}</p>
                <a class="dark_bar accordion_mccc" data-accordion="add-modify-sae">Test afficher</a>
                <div class="panel_form_mccc container-fluid">
                    <p>Contenu de la compétence</p>
                </div>
            </div>
        </div>
    </div>
</template>

<style>
#display_mccc_page {
    margin: 3vw 14vw;
    justify-content: center;
}

.ue_selection_button {
    background-color: var(--main-theme-tertiary-color);
    color: white;
    font-size: 2vw;
    padding: 1.5vw 1vw;
    border-radius: 0.8vw;
    text-align: center;
    width: 8vw;
    cursor: pointer;
}

.display_mccc_triangle {
    width: 0;
    height: 0;
    border-top: 1vw solid transparent;
    border-right: 2vw solid var(--main-theme-background-color);
    border-bottom: 1vw solid transparent;
}
</style>
