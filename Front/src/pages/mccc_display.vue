<script setup>
import axios from 'axios';
import { ref, nextTick, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router'

const route = useRoute()

const pathId = parseInt(localStorage.pathId)
console.log('path ID from localStorage:', pathId)
const institutionId = parseInt(localStorage.idInstitution)
const ueList = ref([])
const resourceList = ref([])
const saeList = ref([])

const ueSelected = ref(false)
const selectedUeId = ref(null)
const selectedUeCodeApogee = ref(null)
const selectedUeIntitule = ref(null)
const selectedUeCompetenceLevel = ref(null)
const selectedUeLinkedResources = ref([])
const selectedUeLinkedSaes = ref([])

function selectUe(ue) {
    ueSelected.value = true
    selectedUeId.value = ue.ueNumber
    selectedUeCodeApogee.value = ue.euApogeeCode
    selectedUeIntitule.value = ue.label
    selectedUeCompetenceLevel.value = ue.competenceLevel

    selectedUeLinkedResources.value = resourceList.value.filter(
        (resource) => resource.ueCoefficients && resource.ueCoefficients.some(
            (coef) => coef.ueId === selectedUeId.value
        )
    )
    selectedUeLinkedSaes.value = saeList.value.filter(
        (sae) => sae.ueCoefficients && sae.ueCoefficients.some(
            (coef) => coef.ueId === selectedUeId.value
        )
    )
    console.log('Selected UE:', ue)
    console.log('Linked Resources:', selectedUeLinkedResources.value)
    console.log('Linked SAEs:', selectedUeLinkedSaes.value)
    attachAccordionListeners()
}

const semester = parseInt(route.query.id)

function getUESemesterInstitution() {
    const pathId = parseInt(route.query.pathId) || parseInt(localStorage.pathId)
    const semester = parseInt(route.query.id)
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
    <div id="display_mccc_page">
        <div id="return_arrow">
            <RouterLink id="back_arrow" :to="`/mccc-select-form?pathId=${pathId}`">←</RouterLink>
            <p>Retour</p>
        </div>
        <div class="container-fluid" style="gap: 0px; width: 100%; align-items: start;">
            <div class="container-fluid cfh" style="width: fit-content;">
                <div v-for="(value, index) in getUESemesterInstitution()" :key="index" v-show="resourceList.length >= 1" class="container-fluid" style="gap: 0;">
                    <div class="ue_selection_button" @click="selectUe(value)">{{ value.label }}</div>
                    <div class="display_mccc_triangle" v-show="ueSelected"></div>
                    <div style="width: 2.5vw;" v-show="!ueSelected"></div>
                </div>
            </div>

            <div class="background_form_mccc" style="padding: 2vw; width: 100%;">
                <p>Code apogee : {{ selectedUeCodeApogee }}</p>
                <p>Intitulé de la compétence : {{ selectedUeIntitule }}</p>
                <p>Niveau de la compétence : {{ selectedUeCompetenceLevel }}</p>
                <div v-for="(value, index) in selectedUeLinkedResources" :key="index">
                    <a class="dark_bar accordion_mccc" data-accordion="add-modify-resource">{{ value.label }} : {{ value.name }}</a>
                    <div class="panel_form_mccc container-fluid cfh">
                        <div class="container-fluid">
                            <p>Code apogee : </p>
                            <p class="mccc_input">{{ value.apogeeCode }}</p>
                        </div>
                        <div class="container-fluid">
                            <p>Nombre d'heures (initiale) : </p>
                            <div class="container-fluid cfh mccc_input">
                                <p>CM : {{ value.initialCm }}</p>
                                <p>TD : {{ value.initialTd }}</p>
                                <p>TP : {{ value.initialTp }}</p>
                                <p>Projet : {{ value.initialProject }}</p>
                                <p>Total : {{ value.initialTotal }}</p>
                            </div>
                        </div>
                        <div class="container-fluid" v-show="value.alternanceCm != null && value.alternanceTd != null && value.alternanceTp != null && value.alternanceProject != null && value.alternanceTotal > 0">
                            <p>Nombre d'heures (alternance) : </p>
                            <div class="container-fluid cfh mccc_input">
                                <p>CM : {{ value.alternanceCm }}</p>
                                <p>TD : {{ value.alternanceTd }}</p>
                                <p>TP : {{ value.alternanceTp }}</p>
                                <p>Projet : {{ value.alternanceProject }}</p>
                                <p>Total : {{ value.alternanceTotal }}</p>
                            </div>
                        </div>
                        <div class="container-fluid">
                            <p>Coefficient : </p>
                            <p class="mccc_input">{{ value.ueCoefficients[0].coefficient }}</p>
                        </div>
                        <div class="container-fluid">
                            <p>Autres UE reliées : </p>
                            <p class="mccc_input">{{ value.apogeeCode }}</p>
                        </div>
                    </div>
                </div>
                <div v-for="(value, index) in selectedUeLinkedSaes" :key="index">
                    <a class="dark_bar accordion_mccc" data-accordion="add-modify-sae">{{ value.label }} : {{ value.name }}</a>
                    <div class="panel_form_mccc container-fluid">
                        <div class="container-fluid">
                            <p>Code apogee : </p>
                            <p>{{ value.apogeeCode }}</p>
                        </div>
                    </div>
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
    margin-left: 0.5vw;
    border-top: 1vw solid transparent;
    border-right: 2vw solid var(--main-theme-background-color);
    border-bottom: 1vw solid transparent;
}

.mccc_input > p {
    font-size: 1vw;
    margin: 0vw 1vw;
}
</style>


