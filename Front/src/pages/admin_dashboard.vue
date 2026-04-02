<script setup>
import {ref} from 'vue'
import { router } from '@/router'
import { onMounted } from 'vue'
import axios from 'axios'
import { API_BASE_URL } from '@/config/api.js'
import { status, institutionLocation } from '../main'
import { getToken, removeToken, getIdInstitutionFromToken, getInstitutionLocationFromToken} from '@/utils/jwt.js'
institutionLocation.value = getInstitutionLocationFromToken()

const goToNext = (url, status) => {
    router.push({
        path: url,
        query: {
            status: status
        }
    })
}

/* constantes */
institutionLocation.value = localStorage.institutionLocation
status.value = 'Admin'
institutionLocation.value = getInstitutionLocationFromToken()

const list_semesters = [1, 2, 3, 4, 5, 6]

/* we can use selected_semester to get the semester*/
let selected_semester_sheets = ref(list_semesters[0])

/* link with the API */

const resource_sheets = ref([])
const selectedSheets = ref([])      // Pour stocker les IDs des fiches sélectionnées
const paths = ref([])               // Liste des parcours pour l'institution
const pathId = ref(null)            // ID du parcours sélectionné

function getAuthHeaders() {
    const token = getToken()
    return token ? { Authorization: `Bearer ${token}` } : {}
}

async function loadData() {
    const institutionId = getIdInstitutionFromToken()
    const startTime = performance.now()
    const headers = getAuthHeaders()

    const [sheetsResult, datesResult, pathsResult] = await Promise.allSettled([
        axios.get(`${API_BASE_URL}/api/v2/resource-sheets/institution/${institutionId}`, { headers, skipAuthRedirect: true }),
        institutionId
            ? axios.get(`${API_BASE_URL}/api/final-delivery-dates/institution/${institutionId}`, { headers, skipAuthRedirect: true })
            : Promise.resolve(null),
        axios.get(`${API_BASE_URL}/api/paths`, { headers, skipAuthRedirect: true }),
    ])

    const endTime = performance.now()
    console.log(`API load time: ${Math.round(endTime - startTime)} ms`)

    // Si toutes les requêtes retournent 401, le token est invalide → rediriger vers login
    const allUnauthorized = [sheetsResult, datesResult, pathsResult].every(
        r => r.status === 'rejected' && r.reason?.response?.status === 401
    )
    if (allUnauthorized) {
        console.warn('Token invalide ou expiré, redirection vers la page de connexion')
        removeToken()
        window.location.hash = '#/'
        return
    }

    // Resource sheets
    if (sheetsResult.status === 'fulfilled') {
        resource_sheets.value = sheetsResult.value.data
        console.log('Resource sheets chargées:', resource_sheets.value.length)
    } else {
        console.error('Error loading resource sheets:', sheetsResult.reason)
        resource_sheets.value = []
    }

    // Paths
    if (pathsResult.status === 'fulfilled') {
        paths.value = pathsResult.value.data.filter(
            (path) => path.institution?.idInstitution === parseInt(institutionId),
        )
        console.log('Parcours filtrés pour institution', institutionId, ':', paths.value)
    } else {
        console.error('Error loading paths:', pathsResult.reason)
        paths.value = []
    }
}

onMounted(async () => {
    await loadData()
})

function getResourcesForSemester(semester) {
    // Ne rien afficher si aucun parcours n'est sélectionné
    if (pathId.value === null) {
        return []
    }

    let filteredSheets = resource_sheets.value
        .filter((sheet) => sheet.semester === semester)

    // Filtrer par path si un path est sélectionné
    if (pathId.value !== null) {
        const selectedPath = paths.value.find((p) => p.idPath === pathId.value)
        if (selectedPath) {
            filteredSheets = filteredSheets.filter((sheet) => sheet.path === selectedPath.name)
        }
    }

    return filteredSheets
}

function toggleSheetSelection(sheetId) {
    const index = selectedSheets.value.indexOf(sheetId)
    if (index > -1) {
        selectedSheets.value.splice(index, 1)
    } else {
        selectedSheets.value.push(sheetId)
    }
}

function isSheetSelected(sheetId) {
    return selectedSheets.value.includes(sheetId)
}

const goToResourceSheetDisplay = (url, label) => {
    router.push({
        path: url,
        query: {
            label: label
        }
    })
}

</script>

<template>
    <div class="container-fluid main_div" style="align-items: center;">
        <div id="sub_div_for_MCCC_and_calender">
            <a class="button" @click="goToNext('/mccc-select-path',Admin)">MCCC</a>
            <a class="button" @click="goToNext('/admin-user',Admin)">Vue utilisateur</a>
        </div>

        <div id="return_sheets_div">
            <div id="return_sheets_div_header">
                <div id="top">
                    <p style="font-size: 1.8vw">Rendu des fiches</p>
                    <div style="display: flex; align-items: center">
                        <div v-show="show_popup" id="popup">
                            Vous pouvez séléctionnez les fiches resource en cochant le carré et
                            cliquer sur l'image de la fléche pour télécharger la version PDF de la
                            fiche resource
                        </div>
                        <p v-if="status" class="btn_how_to" @click="toggleShowPopUp">ⓘ</p>
                        <img
                            id="download"
                            src="/media/download.webp"
                            width="35"
                            height="35"
                            alt="download"
                            @click="downloadSheets"
                        />
                    </div>
                </div>

                <div id="option_path">
                    <select name="paths" class="paths" v-model="pathId">
                        <option :value="null" disabled>Sélectionner un parcours</option>
                        <option v-for="path in paths" :key="path.idPath" :value="path.idPath">
                            {{ path.name }}
                        </option>
                    </select>
                </div>

                <div id="semesters_div">
                    <select name="semesters" class="semesters" v-model="selected_semester_sheets">
                        <option v-for="index in list_semesters" :key="index" :value="index">
                            S{{ index }}
                        </option>
                    </select>
                </div>
            </div>

            <div id="list-of-resources">
                <!-- usage of v-if and v-else to display a message if there is no sheet for the selected semester -->
                <p v-if="getResourcesForSemester(selected_semester_sheets).length === 0">
                    Aucune fiche rendue pour ce semestre.
                </p>

                <div v-else v-for="sheet in getResourcesForSemester(selected_semester_sheets)" :key="sheet.id">
                    <div
                        v-if="!sheet.hasTeacherHours"
                        class="resource"
                        style="background-color: var(--sub-scrollbar-color)"
                    >
                        <p class="resource_label">{{ sheet.resourceLabel }}</p>
                        <div style="gap: 5px">
                            <button
                                class="btn1"
                                style="width: 5vw"
                                @click="
                                goToResourceSheetDisplay(
                                    '/resource-sheet-display',
                                    sheet.resourceLabel,
                                )
                            "
                            >
                                Visualiser
                            </button>
                            <input
                                type="checkbox"
                                :checked="isSheetSelected(sheet.id)"
                                @change="toggleSheetSelection(sheet.id)"
                            />
                        </div>
                    </div>

                    <div v-else class="resource">

                        <p class="resource_label">{{ sheet.resourceLabel }}</p>
                        <div style="gap: 5px">

                            <button
                                class="btn1"
                                style="width: 5vw"
                                @click="
                                goToResourceSheetDisplay(
                                    '/resource-sheet-display',
                                    sheet.resourceLabel,
                                )
                            "
                            >
                                Visualiser
                            </button>
                            <input
                                type="checkbox"
                                :checked="isSheetSelected(sheet.id)"
                                @change="toggleSheetSelection(sheet.id)"
                            />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style>

#admin_template {
    max-width: 30vw;
    margin: 0;
    gap: 1vw;
}

</style>
