<script setup>
import { onMounted, ref } from 'vue'
import axios from 'axios'
import { API_BASE_URL } from '@/config/api.js'
import { status } from '@/main.js'
import { router } from '@/router'
import { useRoute } from 'vue-router'

const route = useRoute()

const afficherBoutons = ref([
    [false, false],
    [false, false],
    [false, false]
])

const saeTable = ref([])
const ueTable = ref([])
const resourceTable = ref([])
const pathId = ref(null)

onMounted(async () => {
    // Récupérer le pathId depuis l'URL ou localStorage
    pathId.value = parseInt(route.query.pathId) || parseInt(localStorage.pathId)
    const institutionId = parseInt(localStorage.idInstitution)

    if (!pathId.value || isNaN(pathId.value)) {
        console.error('PathId manquant ou invalide dans l\'URL')
        alert('Erreur: Parcours non spécifié. Retour à la sélection des parcours.')
        router.push('/mccc-select-path')
        return
    }

    // Stocker le pathId dans localStorage pour les pages suivantes
    localStorage.pathId = pathId.value

    if (!institutionId || isNaN(institutionId)) {
        console.error('Institution ID manquant ou invalide')
        alert('Erreur: Institution non définie. Veuillez vous reconnecter.')
        return
    }

    try {
        // Charger les données filtrées par path
        const saes = await axios.get(`${API_BASE_URL}/api/v2/mccc/saes/path/${pathId.value}`)
        // Filtrer par institution pour sécurité supplémentaire
        saeTable.value = saes.data.filter(sae =>
            sae.institutionId === institutionId
        )

        const ues = await axios.get(`${API_BASE_URL}/api/v2/mccc/ues/path/${pathId.value}`)
        // Filtrer par institution pour sécurité supplémentaire
        ueTable.value = ues.data.filter(ue =>
            ue.institutionId === institutionId
        )

        const resources = await axios.get(`${API_BASE_URL}/api/v2/mccc/resources/path/${pathId.value}`)
        // Filtrer par institution pour sécurité supplémentaire
        resourceTable.value = resources.data.filter(resource =>
            resource.institutionId === institutionId
        )

        console.log(`Données chargées pour le parcours ${pathId.value} et institution ${institutionId}:`, {
            saes: saeTable.value.length,
            ues: ueTable.value.length,
            resources: resourceTable.value.length
        })
    } catch (error) {
        console.error('Erreur lors du chargement des données:', error)
        alert('Erreur lors du chargement des données du parcours')
    }
})

const hasSAEInSemester = (semester) => {
    return saeTable.value.some(sae => sae.semester === semester)
}

const hasResourceInSemester = (semester) => {
    return resourceTable.value.some(resource => resource.semester === semester)
}

const hasUEInSemester = (semester) => {
    return ueTable.value.some(ue => Number(ue.semester) === Number(semester))
}


const getStatusForSemester = (semester) => {
    const hasResource = hasResourceInSemester(semester)
    const hasSAE = hasSAEInSemester(semester)

    if (hasResource || hasSAE) {
        return 'Rempli'
    }
    return 'Vierge'
}

const show_popup = ref(false)

const goToRessourceSheet = (url, semester, pathId) => {
    console.log('=== NAVIGATION MCCC SELECT FORM ===')
    console.log('url:', url)
    console.log('semester:', semester)
    console.log('pathId:', pathId)

    localStorage.pathId = pathId
    console.log('pathId stocké dans localStorage:', localStorage.pathId)

    router.push({
        path: url,
        query: {
            id: semester,
            pathId: pathId
        }
    })
}

function toggleShowPopUp() {
    show_popup.value = !show_popup.value
}
</script>

<template>
    <div id="form_select_page">
        <div style="display: flex; align-items: center; height: 1vw;justify-content: space-between; margin-bottom: 3vw">
            <div style="display: flex">
                <RouterLink class="back_arrow" to="/mccc-select-path">←</RouterLink>
                <p class="back" >Retour</p>
            </div>
            <div style="display: flex">
                <div v-show="show_popup" id="popup_years" style="width: 60vw; height: 4vw">
                    Vous ne pouvez pas créer de ressources ou de SAE tant qu'une UE n'a pas été créée pour le semestre correspondant.
                </div>
                <p v-if="status" class="btn_how_to" @click="toggleShowPopUp" style="color: black">ⓘ</p>
            </div>
        </div>
        <div v-for="(year, index) in afficherBoutons" v-bind:key="index" class="blue_rect">
            <p class="semester_display">Année {{ index+1 }} :</p>
            <div class="container-fluid spe" style="align-items: normal;">
                <div class="semester_rect" v-for="(btn, index2) in year" v-bind:key="index2" v-on:mouseover="afficherBoutons[index][index2] = true" v-on:mouseout="afficherBoutons[index][index2] = false" :style="{ marginBottom: btn ? '0' : '' }">
                    <p class="semester_display">Semestre {{ 2*index+index2+1 }}</p>
                    <p class="status_display" v-show="!btn">{{ getStatusForSemester(2*index+index2+1) }}</p>
                    <div v-show="btn" class="container-fluid spe">
                        <button v-show="btn" class="btn_form_acces" @click="goToRessourceSheet('/form-mccc-UE', (2*index+index2+1), pathId)">UE</button>
                        <button v-show="hasResourceInSemester(2*index+index2+1) || hasUEInSemester(2*index+index2+1)" class="btn_form_acces" @click="goToRessourceSheet('/form-mccc-ressources', (2*index+index2+1), pathId)">Ressource</button>
                        <button v-show="hasSAEInSemester(2*index+index2+1) || hasUEInSemester(2*index+index2+1)" class="btn_form_acces" @click="goToRessourceSheet('/form-mccc-sae', (2*index+index2+1), pathId)">SAÉ</button>
                    </div>
                    <!-- <button v-show="btn && hasUEInSemester(2*index+index2+1)" class="btn_form_acces btn_display_mccc" @click="goToRessourceSheet('/mccc-display', (2*index+index2+1), pathId)">Affichage des MCCC</button> -->
                </div>
            </div>
        </div>
    </div>
</template>

<style>
#form_select_page {
    margin: 3vw 14vw;
    justify-content: center;
    color: var(--main-theme-tertiary-color);
}

.back {
    font-size: 1.5vw;
    font-weight: bold;
    color: var(--main-theme-tertiary-color);
    margin-left: 1.5vw;
}

.blue_rect {
    width: 90%;
    height: fit-content;
    margin: 2vw;
    padding: 1vw 2vw 2vw 2vw;
    border-radius: 1vw;
    background-color: var(--main-theme-background-color);
    color: var(--main-theme-secondary-color);
}

.semester_rect {
    width: 40%;
    height: fit-content;
    margin: 0 5% 6% 5%;
    padding: 1% 2% 3% 2%;
    border-radius: 1.5vw;
    background-color: var(--sub-section-background-color);
    color: var(--main-theme-secondary-color);
}

.semester_display {
    margin: 0.5vw 0;
    padding: 0.5vw;
    text-align: left;
    font-size: 1.5vw;
    font-weight: bold;
}

.status_display {
    text-align: right;
    margin: 0 5% 0 0;
    font-size: 1.3vw;
}

.btn_form_acces {
    width: 25%;
    height: fit-content;
    margin: 0;
    padding: 2% 0;
    border-radius: 0.5vw;
    border-width: 0;
    background-color: var(--clickable-background-color);
    color: var(--main-theme-secondary-color);
    text-decoration: none;
    text-align: center;
    font-size: 0.75vw;
}

.btn_form_acces:hover {
    cursor: pointer;
}

.btn_display_mccc {
    width: 80%;
    margin: 0.8vw 0 0 10%;
}

#popup_years {
    z-index: 10;
    color: white;
    background-color: var(--sub-div-background-color);
    border-radius: 15px;
    padding: 0.6vw;
    font-size: 0.7vw;
    max-width: 12vw;
    max-height: 5vw;
    text-align: justify;
}

#popup_years::after {
    content: "";
    position: absolute;
    top: 11vw;
    right: 17vw;
    rotate: -90deg;
    border-left: 0.8vw solid transparent;
    border-right: 0.8vw solid transparent;
    border-top: 0.8vw solid var(--sub-div-background-color);
}
</style>
