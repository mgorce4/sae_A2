<script setup>
import { ref } from 'vue'
import { status } from '@/main.js'

const show_popup = ref(false)

function toggleShowPopUp() {
    show_popup.value = !show_popup.value
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

                <div id="button_help">
                    <RouterLink class="button" style="margin-top: 2vw; padding: 1vw;" to="/help-center">Centre d'aide</RouterLink>
                </div>
            </div>

            <div id="right_component">
                <RouterLink class="button" style=" width: 31.5vw; margin: 3vh 1vw;" to="/add-teacher-page">Ajout professeur</RouterLink>
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
