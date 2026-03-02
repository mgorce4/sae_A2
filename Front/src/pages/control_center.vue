<script setup>
import { ref } from 'vue'
import { status } from '@/main.js'

const show_popup = ref(false)

function toggleShowPopUp() {
    show_popup.value = !show_popup.value
}

const fileInputRef = ref(null)
const isLoading = ref(false)
const message = ref('')
const isError = ref(false)

const triggerFilePicker = () => {
    fileInputRef.value.click()
}

const uploadCsv = async (event) => {
    const file = event.target.files[0]
    if (!file) return

    isLoading.value = true
    message.value = ''
    isError.value = false

    const formData = new FormData()
    formData.append('file', file)

    const currentUser = localStorage.getItem('username') || 'admin'
    formData.append('currentUser', currentUser)

    try {
        const response = await fetch('http://localhost:8080/api/csv/import', {
            method: 'POST',
            body: formData,
            credentials: 'include'
        })

        if (response.ok) {
            const responseText = await response.text()
            message.value = responseText
            isError.value = false
        } else {
            const errorText = await response.text()
            message.value = `Erreur : ${errorText}`
            isError.value = true
        }
    } catch (error) {
        console.error("Erreur lors de l'import :", error)
        isError.value = true
    } finally {
        isLoading.value = false
        if (fileInputRef.value) {
            fileInputRef.value.value = ''
        }
    }
}
</script>

<template>
    <div id="main">
        <div style="display: flex; align-items: center; height: 1vw">
            <div id="return_arrow">
                <RouterLink id="back_arrow" to="/dashboard-administration">←</RouterLink>
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
                                <input type="date" id="start_date" name="start_date">
                            </div>

                            <div style="display: flex; gap: 2.5vw; margin-top: 1vw">
                                <p>Date de fin : </p>
                                <input type="date" id="end_date" name="end_date">
                            </div>
                        </div>

                        <div id="button_help">
                            <button class="btn1">Sauvegarder</button>
                        </div>
                    </div>
                </div>

                <RouterLink class="button button-full-width" to="/help-center">Centre d'aide</RouterLink>
            </div>

            <div id="right_component">
                <RouterLink class="button" style=" width: 31.5vw; margin: 3vh 1vw;" to="/add-teacher-page">Ajout professeur</RouterLink>

                <div class="import-section">
                    <input
                        type="file"
                        ref="fileInputRef"
                        accept=".csv"
                        style="display: none"
                        @change="uploadCsv"
                    />

                    <button 
                        class="button btn-import" 
                        @click="triggerFilePicker" 
                        :disabled="isLoading"
                        style="width: 31.5vw; margin: 0 1vw;"
                    >
                        {{ isLoading ? 'Importation en cours...' : 'Importer des professeurs (CSV)' }}
                    </button>

                    <p v-if="message" :class="['status-message', isError ? 'error-msg' : 'success-msg']">
                        {{ message }}
                    </p>
                </div>
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
    display: flex;
    flex-direction: column;
    align-items: center; 
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

.import-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
    margin-top: 1vw;
}

.btn-import {
    cursor: pointer;
    text-align: center;
    display: flex;
    justify-content: center;
}

.btn-import:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.status-message {
    margin-top: 1vw;
    font-weight: bold;
    text-align: center;
    max-width: 90%;
}

.success-msg {
    color: #4caf50;
}

.error-msg {
    color: #f44336;
}
</style>