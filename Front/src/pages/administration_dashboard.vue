<script setup>
/* import */
import { ref } from 'vue'
import { status, institutionLocation } from '../main'
import { onMounted } from 'vue'
import axios from 'axios'
import { API_BASE_URL } from '@/config/api.js'
import { router } from '@/router'

/* constantes */

status.value = 'Administration'
institutionLocation.value = localStorage.institutionLocation

const show_popup = ref(false)

const list_semesters = [1, 2, 3, 4, 5, 6]

/* we can use selected_semester to get the semester*/
let selected_semester_sheets = ref(list_semesters[0])

/* link with the API */

const resource_sheets = ref([])
const firstDeliveryDate = ref('')
const secondDeliveryDate = ref('')
const deliveryDatesId = ref(null)
const selectedSheets = ref([]) // Pour stocker les IDs des fiches sélectionnées
const paths = ref([]) // Liste des parcours pour l'institution
const pathId = ref(null) // ID du parcours sélectionné

onMounted(async () => {
    console.log('=== ADMINISTRATION DASHBOARD - ONMOUNTED DEBUT ===')
    console.log('status:', status.value)
    console.log('localStorage.idInstitution:', localStorage.idInstitution)

    try {
        console.log('Chargement des resource sheets...')
        const response = await axios.get(`${API_BASE_URL}/api/v2/resource-sheets`)
        resource_sheets.value = response.data
        console.log('Resource sheets chargées:', resource_sheets.value.length)
    } catch (error) {
        console.error('Error loading resource sheets:', error)
        resource_sheets.value = []
    }

    // Load delivery dates for the current institution
    try {
        const institutionId = localStorage.idInstitution
        if (institutionId) {
            const datesResponse = await axios.get(
                `${API_BASE_URL}/api/final-delivery-dates/institution/${institutionId}`,
            )
            if (datesResponse.data) {
                deliveryDatesId.value = datesResponse.data.idFinalDelivery
                firstDeliveryDate.value = datesResponse.data.firstDelivery || ''
                secondDeliveryDate.value = datesResponse.data.secondDelivery || ''
            }
        }
    } catch (error) {
        // 404 is normal if no dates exist yet for this institution
        if (error.response?.status !== 404) {
            console.error('Error loading delivery dates:', error)
        }
    }

    // Load paths for the current institution
    try {
        const institutionId = localStorage.idInstitution
        console.log('=== CHARGEMENT PARCOURS ADMIN DASHBOARD ===')
        console.log('institutionId:', institutionId)

        if (institutionId) {
            const pathsResponse = await axios.get(`${API_BASE_URL}/api/paths`)
            console.log('Tous les parcours reçus:', pathsResponse.data)

            paths.value = pathsResponse.data.filter(
                (path) => path.institution?.idInstitution === parseInt(institutionId),
            )

            console.log('Parcours filtrés pour institution', institutionId, ':', paths.value)
        }
    } catch (error) {
        console.error('Error loading paths:', error)
        paths.value = []
    }
})

function getResourcesForSemester(semester) {
    // Ne rien afficher si aucun parcours n'est sélectionné
    if (pathId.value === null) {
        return []
    }

    let filteredSheets = resource_sheets.value
        .filter((sheet) => sheet.semester === semester)
        .filter((sheet) => sheet.institutionId == localStorage.idInstitution)

    // Filtrer par path si un path est sélectionné
    if (pathId.value !== null) {
        const selectedPath = paths.value.find((p) => p.idPath === pathId.value)
        if (selectedPath) {
            filteredSheets = filteredSheets.filter((sheet) => sheet.path === selectedPath.name)
        }
    }

    return filteredSheets
}

async function saveDeliveryDates() {
    try {
        const institutionId = localStorage.idInstitution
        if (!institutionId) {
            console.error('Institution non trouvée')
            return
        }

        const deliveryDatesData = {
            firstDelivery: firstDeliveryDate.value,
            secondDelivery: secondDeliveryDate.value,
            institution: {
                idInstitution: parseInt(institutionId),
            },
        }

        // Use the save-by-institution endpoint which automatically handles create or update
        const response = await axios.post(
            `${API_BASE_URL}/api/final-delivery-dates/save-by-institution`,
            deliveryDatesData,
        )
        deliveryDatesId.value = response.data.idFinalDelivery

        // Reload the page to show the updated dates
        window.location.reload()
    } catch (error) {
        console.error('Error saving delivery dates:', error)
    }
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

async function downloadSheets() {
    console.log('=== DOWNLOAD SHEETS ===')
    console.log('selectedSheets:', selectedSheets.value)

    if (selectedSheets.value.length === 0) {
        console.log('Aucune fiche sélectionnée')
        alert('Veuillez sélectionner au moins une fiche à télécharger')
        return
    }

    const userName = localStorage.username || 'user'
    console.log('userName:', userName)

    try {
        // Download each selected sheet
        for (const sheetId of selectedSheets.value) {
            const sheet = resource_sheets.value.find((s) => s.id === sheetId)
            console.log('Sheet trouvée:', sheet)

            if (sheet && sheet.resourceLabel) {
                console.log('Téléchargement du PDF pour:', sheet.resourceLabel)

                // Call the PDF generation endpoint
                const response = await axios.get(`${API_BASE_URL}/api/pdf/generate`, {
                    params: {
                        resourceName: sheet.resourceLabel,
                        userName: userName,
                    },
                    responseType: 'blob',
                })

                console.log('Réponse reçue, création du lien de téléchargement...')

                // Create a download link
                const url = window.URL.createObjectURL(new Blob([response.data]))
                const link = document.createElement('a')
                link.href = url
                link.setAttribute('download', `${sheet.resourceLabel}_ressource_sheet.pdf`)
                document.body.appendChild(link)
                link.click()
                link.remove()
                window.URL.revokeObjectURL(url)

                console.log('PDF téléchargé avec succès')
            } else {
                console.error('Fiche introuvable ou sans label:', sheetId)
            }
        }

        alert(`${selectedSheets.value.length} PDF(s) téléchargé(s) avec succès`)
    } catch (error) {
        console.error('Error downloading PDFs:', error)
        console.error('Error response:', error.response)
        console.error('Error status:', error.response?.status)
        console.error('Error data type:', typeof error.response?.data)

        // Si c'est un blob, le lire
        if (error.response?.data instanceof Blob) {
            const reader = new FileReader()
            reader.onload = () => {
                console.error('Error message from server:', reader.result)
                alert(`Erreur lors de la génération du PDF:\n${reader.result}`)
            }
            reader.readAsText(error.response.data)
        } else {
            alert(`Erreur lors du téléchargement des PDF: ${error.message}`)
        }
    }
}

const goToRessourceSheetDisplay = (url, label) => {
    router.push({
        path: url,
        query: {
            label: label
        }
    })
}

function toggleShowPopUp() {
    show_popup.value = !show_popup.value
}
</script>

<template>
    <div id="main_div">
        <div id="sub_div_for_MCCC_and_calender">
            <div id="MCCC_div">
                <!-- link into MCCC page -->
                <RouterLink
                    type="button"
                    class="button"
                    to="/mccc-select-path"
                >
                    MCCC
                </RouterLink>
            </div>
            <div id="date_selector_div">
                <p id="title_font">Dates de rendu des fiches ressources</p>
                <div id="inline">
                    <p>Date de rendu du 1er semestre</p>
                    <input
                        type="date"
                        v-model="firstDeliveryDate"
                        name="first-delivery"
                        :min="new Date().toISOString().split('T')[0]"
                    />
                </div>
                <div id="inline">
                    <p>Date de rendu du 2ème semestre</p>
                    <input
                        type="date"
                        v-model="secondDeliveryDate"
                        name="second-delivery"
                        :min="new Date().toISOString().split('T')[0]"
                    />
                </div>
                <button class="btn1" @click="saveDeliveryDates">Valider</button>
            </div>

            <RouterLink class="button" to="/control-center">
                Centre de controle
            </RouterLink>

        </div>

        <div id="return_sheets_div">
            <div id="return_sheets_div_header">
                <div id="top">
                    <p style="font-size: 1.8vw">Rendu des fiches</p>
                    <div style="display: flex; align-items: center">
                        <div v-show="show_popup" id="popup">
                            Vous pouvez séléctionnez les fiches ressource en cochant le carré et
                            cliquer sur l'image de la fléche pour télécharger la version PDF de la
                            fiche ressource
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

            <div id="list-of-ressources">
                <!-- usage of v-if and v-else to display a message if there is no sheet for the selected semester -->
                <p v-if="getResourcesForSemester(selected_semester_sheets).length === 0">
                    Aucune fiche rendue pour ce semestre.
                </p>

                <div
                    v-else
                    class="ressource"
                    v-for="sheet in getResourcesForSemester(selected_semester_sheets)"
                    :key="sheet.id"
                >
                    <p class="ressource_label">{{ sheet.resourceLabel }}</p>
                    <div style="gap: 5px">
                        <button
                            class="btn1"
                            style="width: 5vw"
                            @click="
                                goToRessourceSheetDisplay(
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
</template>

<style>
/* -- main --*/

#top {
    margin-bottom: 1vw;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

#download {
    cursor: pointer;
    transition: transform 0.2s;
}

#download:hover {
    transform: scale(1.1);
}

#title_font {
    font-size: 1.5vw;
}

#main_div {
    display: flex;
    align-items: flex-start; /* Alignement en haut */
    height: 100%;
    align-self: center;
    width: 100%;
    justify-content: center;
    gap: 100px;
    margin-top: 3.2vw;
}

#main_div > div {
    border-radius: 15px;
}

#main_div > div > div {
    /* -- for MCCC-div and calender-div -- */
    border-radius: 15px;
}

/* -- sub dic for MCCC and calender --*/

#sub_div_for_MCCC_and_calender {
    width: 30%;
    display: flex;
    flex-direction: column;
}

/* -- MCCC -- */

#MCCC_div {
    text-align: center;
    align-content: center;
}

.button {
    color: var(--main-theme-secondary-color);
    font-size: 3vw;
    background-color: var(--sub-section-background-color);
    border-radius: 15px;
    width: 100%;
    height: 7vw;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 1.5vw;

}

.button:hover {
    cursor: pointer;
    background-color: var(--main-theme-secondary-background-color);
}

/* -- calendar -- */

#date_selector_div {
    background-color: var(--main-theme-background-color);
    color: var(--main-theme-secondary-color);
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-bottom: 1vw;
    text-align: center;
    margin-bottom: 1.5vw;
}

#inline {
    display: inline-flex;
    gap: 1vw;
    align-items: center;
    margin-bottom: 1vw;
    justify-content: center;
}

#inline p {
    text-align: center;
}

input[type='date'] {
    border-radius: 15px;
    background-color: var(--div-rect-background-color);
    color: var(--main-theme-secondary-color);
    border: none;
    padding: 0.5vw 1vw;
    font-size: 1vw;
    min-height: 2em;
    box-sizing: border-box;
}

input[type='date']:focus {
    outline: none;
    box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.3);
}

input[type='date']::-webkit-calendar-picker-indicator {
    filter: invert(1);
    cursor: pointer;
}

/* -- return sheets div -- */

#return_sheets_div {
    background-color: var(--main-theme-background-color);
    color: var(--main-theme-secondary-color);
    height: calc(6vw + 1.5vw + var(--date-panel-height, 20vw) + 1.5vw + 6vw); /* Hauteur fixe calculée */
    max-height: calc(6vw + 1.5vw + var(--date-panel-height, 20vw) + 1.5vw + 6vw);
    width: 35%;
    padding: 0 1vw 1vw 1vw;
    margin-bottom: 1vw;
    margin-top: -1.5vw;
    overflow-y: auto; /* Scrollbar si contenu dépasse */
    font-size: 2vw;
    display: flex;
    flex-direction: column;
}

#return_sheets_div_header {
    position: sticky;
    top: 0;
    background-color: var(--main-theme-background-color);
    z-index: 10;
    padding-bottom: 1vw;
}

#top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 1vw;
}

#list-of-ressources {
    flex: 1;
    overflow-y: auto; /* Scrollbar uniquement sur le contenu */
    padding-right: 1vw; /* Espace à droite de la scrollbar */
}

#list-of-ressources::-webkit-scrollbar {
    width: 0.8vw;
}

#list-of-ressources::-webkit-scrollbar-track {
    margin: 1vw 0.5vw 1vw 0; /* Marge à droite */
    background: var(--main-theme-secondary-background-color);
    box-shadow: inset 0 0 1vw var(--sub-scrollbar-color);
    border-radius: 15px;
}

#list-of-ressources::-webkit-scrollbar-thumb {
    background: var(--main-theme-secondary-color);
    border-radius: 15px;
    border: 0.2vw var(--main-theme-tertiary-color) solid;
}

/* Remove scrollbar from parent div */
#return_sheets_div::-webkit-scrollbar {
    display: none;
}

.ressource {
    background-color: var(--sub-div-background-color);
    margin-bottom: 1vw;
    padding: 5px;
    font-size: 2vw;
    border-radius: 15px;
    justify-content: space-between;
    display: flex;
    height: 4vw;
    align-items: center;
    margin-left: 1.5vw;
    margin-right: 1vw;
}

.ressource_label {
    max-width: 23vw;
    overflow: auto;
}

.ressource_label::-webkit-scrollbar {
    width: 0.8vw;
    height: 0.6vw;
}

.ressource_label::-webkit-scrollbar-track {
    margin: 1vw 0.5vw 1vw 0; /* Marge à droite */
    background: var(--main-theme-secondary-background-color);
    box-shadow: inset 0 0 1vw var(--sub-scrollbar-color);
    border-radius: 15px;
}

.ressource_label::-webkit-scrollbar-thumb {
    background: var(--main-theme-secondary-color);
    border-radius: 15px;
    border: 0.2vw var(--main-theme-tertiary-color) solid;
}

.semesters {
    background-color: var(--sub-div-background-color);
    width: 90%;
    height: 2.5vw;
    text-align: center;
    color: var(--main-theme-secondary-color);
    border-bottom-left-radius: 15px;
    border-bottom-right-radius: 15px;
    font-size: 1.5vw;
    margin-left: 1.5vw;
    margin-top: 0vw;
}

.paths {
    background-color: var(--sub-scrollbar-color);
    color: var(--main-theme-secondary-color);
    width: 100%;
    height: 3.5vw;
    border-radius: 15px;
    margin-bottom: 0vw;
    border: none;
    text-align: center;
    font-size: 1.3vw;
}

#popup {
    z-index: 10;
    color: white;
    background-color: var(--sub-div-background-color);
    border-radius: 15px;
    padding: 0.6vw;
    font-size: 0.7vw;
    max-width: 11vw;
    max-height: 5vw;
    text-align: justify;
}

#popup::after {
    content: '';
    position: absolute;
    top: 3.5vw;
    right: 5.1vw;
    rotate: -90deg;
    border-left: 0.8vw solid transparent;
    border-right: 0.8vw solid transparent;
    border-top: 0.8vw solid var(--sub-div-background-color);
}
</style>
