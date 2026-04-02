<script setup>

import { nextTick, onMounted, ref } from 'vue'
import axios from 'axios'
import { API_BASE_URL } from '@/config/api.js'
import { status } from '@/main.js'
import {
    getIdFromToken,
    getIdInstitutionFromToken,
    getInstitutionNameFromToken,
    getInstitutionLocationFromToken,
    getAccessRightsFromToken
} from '@/utils/jwt.js'


let display_more_area = ref(false)
let is_modifying = ref(false)


status.value = 'Super Admin'

let title = ref("")

const institut_name = ref("")
const institut_location = ref("")
const institut_id = ref(0)

const errors = ref({
    name: false,
    location: false
})

const error_messages = ref({
    name: "Le nom doit être renseigné",
    location: "La localisation doit être renseignée",
})

const instituts = ref([])

const attachAccordionListeners = () => {
    nextTick(() => {
        const acc = document.getElementsByClassName('accordion_teacher')
        for (let i = 0; i < acc.length; i++) {

            if (acc[i].getAttribute('data-accordion') === 'add-modify-teacher') {
                acc[i].addEventListener('click', function () {
                    this.classList.toggle('active')
                    const panel = this.nextElementSibling
                    if (panel.style.maxHeight) {
                        panel.style.maxHeight = null
                    } else {
                        panel.style.maxHeight = panel.scrollHeight + 'vw'
                        panel.style.padding = '0 18px'
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
                        panel.style.padding = '0 18px'
                    }
                })
            }
        }
    })
}

onMounted(async () => {
    const response = await axios.get(`${API_BASE_URL}/api/institutions`)
    instituts.value = response.data
    // instituts.value = instituts.value.filter((institut) => institut.user.institution.idInstitution === getIdInstitutionFromToken())

    await nextTick()
    attachAccordionListeners()
})

function addInstitut() {
    title.value = "Ajouter un établissement"
    is_modifying.value = false
    institut_id.value = 0
    institut_name.value = ''
    institut_location.value = ''
}

const save = async () => {

    // reste all errors
    errors.value = {
        name: false,
        location: false
    }

    let hasError = false

    if (institut_name.value === "") {
        errors.value.name = true
        hasError = true
    }

    if (institut_location.value === "") {
        errors.value.location = true
        hasError = true
    }

    if (hasError) {
        return
    }

    try {
        // is user logged in
        if (!getIdFromToken()) {
            alert('Erreur : Veuillez vous reconnecter.')
            return
        }

        const payload = {
            location : institut_location.value,
            name : institut_name.value,
            institution : {
                idInstitution : getIdInstitutionFromToken(),
                name : getInstitutionNameFromToken(),
                location : getInstitutionLocationFromToken(),
            },
        }

        if (!is_modifying.value) {
            await axios.post(`${API_BASE_URL}/api/institutions`, payload)
        } else {
            await axios.put(`${API_BASE_URL}/api/institutions/${institut_id.value}`, payload)
            is_modifying.value = false
            institut_id.value = 0
        }

        ;[institut_location, institut_name].forEach((f) => (f.value = ''))
        display_more_area.value = false

        await reloadInstitut()
        attachAccordionListeners()

        console.log('Établissement sauvegardé avec succès')
    } catch (error) {
        console.error('Erreur lors de la sauvegarde:', error)
        if (error.response) {
            console.error("Détails de l'erreur:", error.response.data)
            console.error('Status:', error.response.status)
        }
        alert('Erreur lors de la sauvegarde. Consultez la console pour plus de détails.')
    }
}

async function reloadInstitut() {
    const response = await axios.get(`${API_BASE_URL}/api/institutions`)
    instituts.value = response.data
}

function modify(institut) {
    title.value = "Modifier un établissement"
    institut_id.value = institut.idInstitution
    institut_name.value = institut.name
    institut_location.value = institut.location
}

const deleteInstitut = async (id) => {
    if (
        !confirm(
            'Cette action est irréversible (pour le moment), continuer à vos risques et périls.',
        )
    ) {
        return
    }
    try {
        await axios.delete(`${API_BASE_URL}/api/institutions/${id}`)
        await reloadInstitut()

        attachAccordionListeners()
    } catch (error) {
        console.error('Erreur lors de la suppression', error)
    }
}

const access_right_list = getAccessRightsFromToken()

</script>

<template>
    <div id="main">
        <div id="return_arrow">
            <div v-if="access_right_list.length == 1">
                <RouterLink id="back_arrow" to="/sup-admin-dashboard">←</RouterLink>
            </div>
            <RouterLink to="/multi-access-right-dashboard" id="back_arrow" v-else>←</RouterLink>
            <p>Retour</p>
        </div>

        <div class="background">
            <div id="form">
                <div id="header">
                    <p id="title">Ajouter un établissement</p>
                </div>

                <div id="dark_bar">
                    <p>Ajouter un établissement</p>
                    <button id="button_more" v-on:click="display_more_area = !display_more_area;  addInstitut()">
                        {{ display_more_area ? '-' : '+' }}
                    </button>
                </div>

                <form v-show="display_more_area" method="post" v-on:submit.prevent="">
                    <a class="accordion_teacher dark_bar" data-accordion="add-modify-teacher">{{ title }}</a>

                    <div class="panel" style="display: flex">
                        <div style="margin-left: 15vw; padding-top: 1vw">
                            <div class="sub_div_panel">
                                <label style="font-size: 1vw;">Nom : </label>
                                <input type="text" class="input" v-model="institut_name">
                                <input style="margin-left: 11.5vw" class="btn1" type="reset" value="Annuler" v-on:click="display_more_area = !display_more_area" />
                            </div>

                            <p v-if="errors.name" class="error_message" style="text-align: left">{{ error_messages.name }}</p>

                            <div class="sub_div_panel">
                                <label style="font-size: 1vw;">Localisation : </label>
                                <input type="text" class="input" v-model="institut_location">
                                <input style="margin-left: 10vw" id="save" class="btn1" type="button" value="Sauvegarder" v-on:click="save()" />
                            </div>

                            <p v-if="errors.location" class="error_message" style="text-align: left">{{ error_messages.location }}</p>

                        </div>

                    </div>
                </form>
            </div>

            <div id="form_resources">
                <p v-if="instituts.length > 0">Établissements enregistrés : </p>
                <p v-else>Aucun établissement n'a été enregistré</p>

                <div v-for="institut in instituts" :key="institut.idInstitution" style="color: white" >
                    <a class="accordion_teacher" id="dark_bar">{{institut.name}} {{institut.location}}</a>

                    <div class="panel">
                        <div class="hours_grid" style="gap: 1vw">
                            <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                <p>Nom : </p>
                                <p>{{institut.name}}</p>
                            </div>

                            <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                <p>Localisation : </p>
                                <p>{{institut.location}}</p>
                            </div>
                        </div>
                        <div style="background-color: transparent; display: flex; padding: 0; margin-top: 0; margin-bottom: 1vh; justify-content: center; align-items: center">
                            <input class="btn1" type="button" value="Supprimer" v-on:click="deleteInstitut(institut.idInstitution)"/>
                            <input class="btn1" type="button" value="Modifier" v-on:click="is_modifying = true; display_more_area = true; modify(institut)" />
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>
