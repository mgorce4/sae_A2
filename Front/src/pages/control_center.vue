<script setup>
import { onMounted, ref } from 'vue'
import { status } from '@/main.js'
import axios from 'axios'
import { API_BASE_URL } from '@/config/api.js'
import { getAccessRightsFromToken, getIdInstitutionFromToken } from '@/utils/jwt.js'
import { router } from '@/router'

const show_popup = ref(false)
const pathId = ref(1)
const fileInputRef = ref(null)
const isLoading = ref(false)
const message = ref('')
const isError = ref(false)

const triggerFilePicker = () => {
  fileInputRef.value.click()
}

const uploadExcel = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  isLoading.value = true
  message.value = ''
  isError.value = false

  const formData = new FormData()
  formData.append('file', file)
  formData.append('pathId', pathId.value)

  const rawToken = localStorage.getItem('jwt_token') || sessionStorage.getItem('token')

  if (!rawToken) {
    message.value = "Erreur : Pas d'utilisateurs."
    isError.value = true
    isLoading.value = false
    return
  }

  const token = rawToken.replace(/^["']|["']$/g, '')

  console.log("Token envoyé (nettoyé) :", token.substring(0, 20) + "...") 

  try {
    const response = await fetch('http://localhost:8080/api/csv/import-excel-resources', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}` 
      },
      body: formData
    })

    const responseText = await response.text()

    if (response.ok) {
      message.value = responseText || "Importation réussie !"
      isError.value = false
    } else {
      message.value = `Erreur (${response.status}) : ${responseText}`
      isError.value = true
    }
  } catch (error) {
    console.error(error)
    message.value = "Erreur réseau"
    isError.value = true
  } finally {
    isLoading.value = false
    if (fileInputRef.value) fileInputRef.value.value = ''
  }
}

const startDate = ref('')
const endGame = ref('')
const deliveryDatesId = ref(null)

const errors= ref({
    startDate: false,
    endGame: false,
})

const errorMessages = ref({
    startDate: 'La date de début n\'est pas bonne',
    endGame: 'La date de fin n\'est pas bonne',
})

const normalizeDateForInput = (dateValue) => {
    if (!dateValue) return ''
    if (typeof dateValue === 'string') return dateValue.slice(0, 10)
    return new Date(dateValue).toISOString().slice(0, 10)
}

const loadSavedYearDates = async () => {
    try {
        const institutionId = getIdInstitutionFromToken()
        if (!institutionId) {
            return
        }

        const response = await axios.get(
            `${API_BASE_URL}/api/year-dates/v2/institution/${institutionId}/latest`,
            { skipAuthRedirect: true }
        )

        if (response.data) {
            startDate.value = normalizeDateForInput(response.data.startYear)
            endGame.value = normalizeDateForInput(response.data.endYear)
            deliveryDatesId.value = response.data.idYearDates ?? null
        }
    } catch (error) {
        if (error?.response?.status !== 404) {
            console.error('Error loading year dates: ', error)
        }
    }
}

const checkCurrentSchoolYearDates = async (institutionId) => {
    if (!institutionId) return { exists: false, yearDates: null }

    try {
        const response = await axios.get(
            `${API_BASE_URL}/api/year-dates/v2/institution/${institutionId}`,
            { skipAuthRedirect: true }
        )

        const rows = Array.isArray(response.data) ? response.data : []
        const today = new Date().toISOString().slice(0, 10)

        const current = rows.find((row) => {
            const start = (row.startYear || '').slice(0, 10)
            const end = (row.endYear || '').slice(0, 10)
            return start && end && start <= today && today <= end
        })

        return { exists: Boolean(current), yearDates: current || null }
    } catch (error) {
        if (error?.response?.status !== 404) {
            console.error('Error checking current school year dates:', error)
        }
        return { exists: false, yearDates: null }
    }
}

function toggleShowPopUp() {
    show_popup.value = !show_popup.value
}

const access_right = getAccessRightsFromToken()

const goToNext = (url, status) => {
    router.push({
        path: url,
        query: {
            status: status
        }
    })
}

const saveDateYear = async () =>{

    const hasError = ref(false)
    errors.value={
        startDate: false,
        endGame: false,
    }

    if (startDate.value===endGame.value){
        errors.value.endGame = true
    }

    if (startDate.value == '' || endGame.value == '') {
        errors.value.startDate = true,
        errors.value.endGame = true
    }

    if (startDate.value > endGame.value){
        errors.value.endGame=true;    
    }

    if (errors.value.startDate) {
        hasError.value = true
        console.error(errorMessages.value.startDate)
    }

    if (errors.value.endGame) {
        hasError.value = true
        console.error(errorMessages.value.endGame)
    }

    if (hasError.value) {
        return
    }

    try {
        const institutionId = getIdInstitutionFromToken()
        if (!institutionId) {
            console.error('Institution non trouvée')
            return
        }

        const yearDatesData = {
            startYear: startDate.value,
            endYear: endGame.value,
            institutionId: parseInt(institutionId),
        }

        const { exists, yearDates } = await checkCurrentSchoolYearDates(institutionId)

        const response = exists && yearDates?.idYearDates
            ? await axios.put(
                `${API_BASE_URL}/api/year-dates/v2/${yearDates.idYearDates}`,
                yearDatesData
            )
            : await axios.post(
                `${API_BASE_URL}/api/year-dates/v2`,
                yearDatesData
            )
        
        deliveryDatesId.value = response.data.idYearDates
        startDate.value = normalizeDateForInput(response.data.startYear ?? startDate.value)
        endGame.value = normalizeDateForInput(response.data.endYear ?? endGame.value)

    } catch (error) {
        console.error('Error saving year dates: ', error)
    }
}

onMounted(async () => {
    await loadSavedYearDates()
})


</script>

<template>
    <div id="main">
        <div style="display: flex; align-items: center; height: 1vw">
            <div id="return_arrow">
                <RouterLink id="back_arrow" to="/dashboard-administration" v-if="access_right.length == 1">←</RouterLink>
                <RouterLink to="/multi-access-right-dashboard" id="back_arrow" v-else>←</RouterLink>
                <p>Retour</p>
            </div>
        </div>

        <div style="display: flex; gap: 50px; margin-top: 1vw">
            <div id="left_component">
                <div id="date">
                    <div style="display: flex">
                        <p v-if="status" class="btn_how_to" @click="toggleShowPopUp">ⓘ</p>
                        <div v-show="show_popup" id="popup_date">
                            Vous pouvez remplir les dates de début et de fin d'année scolaire. À la
                            fin de l'année scolaire, la date de fin devindra la date de début de la
                            nouvelle année scolaire.
                        </div>
                    </div>
                    <div style="justify-items: center">
                        <p class="title" style="margin-top: 0; font-size: 2vw">Date de l'année scolaire en cours</p>

                        <div style="margin-bottom: 1vw">
                            <div style="display: flex; gap: 1vw; margin-top: 1vw">
                                <p>Date de début : </p>
                                <input
                                    type="date"
                                    v-model="startDate"
                                    name="start-year"
                                    :min="new Date().toISOString().split('T')[0]"
                                />
                            </div>

                            <div style="display: flex; gap: 2.5vw; margin-top: 1vw">
                                <p>Date de fin : </p>
                                <input
                                    type="date"
                                    v-model="endGame"
                                    name="end-year"
                                    :min="new Date().toISOString().split('T')[0]"
                                />
                            </div>
                        </div>
                        
                        <p class="error_message" v-show="errors.endGame">
                            Attention, les dates sont invalides.
                        </p>

                        <div id="button_help">
                            <button class="btn1" @click="saveDateYear">Sauvegarder</button>
                        </div>
                    </div>
                </div>

                <RouterLink class="button button-full-width" to="/help-center">Centre d'aide</RouterLink>
            </div>

            <div id="right_component">
                <button class="button" style="font-size: 2vw; width: 31.5vw; margin: 3vh 1vw;" @click="goToNext('/add-teacher-page',Administration)">Ajout professeur</button>
                <input
                  type="file"
                  ref="fileInputRef"
                  accept=".xlsx, .xls"
                  style="display: none"
                  @change="uploadExcel"
                />
                <button class="button" style="font-size: 2vw; width: 31.5vw; margin: 3vh 1vw;"  @click="triggerFilePicker" :disabled="isLoading">
                  {{ isLoading ? 'Importation en cours...' : 'Importer des MCCC (Excel)' }}
                </button>

                <p v-if="message" :style="{ color: isError ? 'red' : 'green' }">{{ message }}</p>
            </div>
        </div>
    </div>
</template>

<style scoped>
#left_component {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 50%;
    color: white;
}

#date {
    background-color: var(--main-theme-background-color);
    border-radius: 1vw;
    padding: 0.5vw;
    width: 30vw;
}

#button_help {
    margin-top: 2vw;
}

.button-full-width {
    padding: 1vw;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 2vw;
    width: 30vw;
}

#right_component {
    width: 50%;
    background-color: var(--main-theme-background-color);
    height: 40vw;
    border-radius: 1vw;
}

#popup_date {
    z-index: 10;
    color: white;
    background-color: var(--sub-div-background-color);
    border-radius: 15px;
    padding: 0.6vw;
    font-size: 0.7vw;
    max-width: 15vw;
    max-height: 4vw;
    text-align: justify;
}

#popup_date::after {
    content: "";
    position: absolute;
    top: 19.2vw;
    right: 78.5vw;
    rotate: 90deg;
    border-left: 0.8vw solid transparent;
    border-right: 0.8vw solid transparent;
    border-top: 0.8vw solid var(--sub-div-background-color);
}
</style>
