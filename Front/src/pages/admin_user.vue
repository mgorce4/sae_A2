<script setup>

import { nextTick, onMounted, ref } from 'vue'
import axios from 'axios'
import { API_BASE_URL } from '@/config/api.js'
import { status } from '@/main.js'
import { getIdFromToken, getIdInstitutionFromToken, getInstitutionNameFromToken, getInstitutionLocationFromToken } from '@/utils/jwt.js'

let display_more_area = ref(false)
let is_modifying = ref(false)

let title = ref("")

const teacher_name = ref("")
const teacher_firstname = ref("")
const teacher_mail = ref("")
const teacher_id = ref(0)
const access_right = ref(0)
const show_popup = ref(false)

const errors = ref({
    name: false,
    firstname: false,
    mail: false,
})

const error_messages = ref({
    name: "Le nom doit être renseigné",
    firstname: "Le prenom doit être renseigné",
    mail: "Le mail doit être renseigné",
})

const teachers = ref([])
const administrations = ref([])

const attachAccordionListeners = () => {
    nextTick(() => {
        const accordions = document.querySelectorAll('.accordion_teacher')

        accordions.forEach((accordion) => {
            accordion.onclick = function () {
                this.classList.toggle('active')
                const panel = this.nextElementSibling

                if (!panel) {
                    return
                }

                if (panel.style.maxHeight) {
                    panel.style.maxHeight = null
                    panel.style.padding = null
                } else {
                    panel.style.maxHeight = panel.scrollHeight + 'px'
                    panel.style.padding = '0 18px'
                }
            }
        })
    })
}

onMounted(async () => {
    const response = await axios.get(`${API_BASE_URL}/api/access-rights`)
    const usersInInstitution = response.data.filter((entry) => entry.user.institution.idInstitution === getIdInstitutionFromToken())
    teachers.value = usersInInstitution.filter((ar) => ar.accessRight === 1)
    administrations.value = usersInInstitution.filter((ar) => ar.accessRight === 2)

    await nextTick()
    attachAccordionListeners()
})

function addTeacher() {
    title.value = "Ajouter un professeur"
}

function getUsername() {
    return (teacher_firstname.value.charAt(0) + teacher_name.value).toLowerCase()
}

const save = async () => {

    // reste all errors
    errors.value = {
        name: false,
        firstname: false,
        mail: false,
    }

    let hasError = false

    if (teacher_name.value === "") {
        errors.value.name = true
        hasError = true
    }

    if (teacher_firstname.value === "") {
        errors.value.firstname = true
        hasError = true
    }

    if (teacher_mail.value ===""){
        errors.value.mail = true
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
            firstname : teacher_firstname.value,
            lastname : teacher_name.value,
            username : getUsername(),
            password : getUsername() + '123',
            mail : teacher_mail.value,
            institution : {
                idInstitution : getIdInstitutionFromToken(),
                name : getInstitutionNameFromToken(),
                location : getInstitutionLocationFromToken(),
            },
        }

        if (!is_modifying.value) {
            let user_response = await axios.post(`${API_BASE_URL}/api/users`, payload);
            [teacher_firstname, teacher_name, teacher_mail].forEach((r) => r.value = '')
            display_more_area.value = false

            // get the id of the new user
            let user = user_response.data
            let id = user.idUser

            const access_right_payload = {
                accessRight : access_right.value,
                idUser : id,
            }
            console.log("access-right-payload : ", access_right_payload)

            await axios.post(`${API_BASE_URL}/api/access-rights`, access_right_payload);
        } else {
            const user_id = teacher_id

            await axios.put(`${API_BASE_URL}/api/users/${user_id.value}`, payload);

            [teacher_firstname, teacher_name, teacher_mail].forEach((r) => r.value = '')
            display_more_area.value = false
            is_modifying.value = false
        }

        await reloadTeachers()
        attachAccordionListeners()

        console.log('professeur sauvegardé avec succès')
    } catch (error) {
        console.error('Erreur lors de la sauvegarde:', error)
        if (error.response) {
            console.error("Détails de l'erreur:", error.response.data)
            console.error('Status:', error.response.status)
        }
        alert('Erreur lors de la sauvegarde. Consultez la console pour plus de détails.')
    }
}

async function reloadTeachers() {
    const response = await axios.get(`${API_BASE_URL}/api/access-rights`)
    const usersInInstitution = response.data.filter((entry) => entry.user.institution.idInstitution === getIdInstitutionFromToken())
    teachers.value = usersInInstitution.filter((ar) => ar.accessRight === 1)
    administrations.value = usersInInstitution.filter((ar) => ar.accessRight === 2)
}

function modify(teacher) {
    title.value = "Modifier un professeur"
    teacher_name.value = teacher.user.lastname
    teacher_firstname.value = teacher.user.firstname
    teacher_mail.value = teacher.user.mail
    teacher_id.value = teacher.idUser
}

const deleteTeacher = async (id) => {
    if (
        !confirm(
            'Cette action est irréversible (pour le moment), continuer à vos risques et périls.',
        )
    ) {
        return
    }
    try {
        let main_resources = await axios.get(`${API_BASE_URL}/api/main-teachers-for-resource/user/${id}`)
        let main_resources_data = main_resources.data

        if (main_resources_data.length > 0) {

            for (let i = 0; i < main_resources_data.length; i++) {
                await axios.delete(`${API_BASE_URL}/api/main-teachers-for-resource/user/${id}/resource/${main_resources_data[i].idResource}`)
            }

            for (let i = 0; i < main_resources_data.length; i++) {
                alert('Vous venez de supprimer un professeur référent de la ressource' + main_resources_data[i].resourceLabel + '. Veuillez rajouter un nouveau professeur référent pour cette ressource.')
            }
        }

        let normal_resources = await axios.get(`${API_BASE_URL}/api/teachers-for-resource/user/${id}`)
        let normal_resources_data = normal_resources.data

        if (normal_resources_data.length > 0) {

            for (let i = 0; i < normal_resources_data.length; i++) {
                await axios.delete(`${API_BASE_URL}/api/teachers-for-resource/user/${id}/resource/${normal_resources_data[i].idResource}`)
            }

            for (let i = 0; i < normal_resources_data.length; i++) {
                alert('Vous venez de supprimer un professeur enseignant de la ressource' + normal_resources_data[i].resourceLabel + '. Si besoin, veuillez rajouter un nouveau professeur enseignant pour cette ressource si nécessaire.')
            }
        }

        await axios.delete(`${API_BASE_URL}/api/users/${id}`)
        await axios.delete(`${API_BASE_URL}/api/access-rights/1/${id}`)

        await reloadTeachers()

        attachAccordionListeners()
    } catch (error) {
        console.error('Erreur lors de la suppression', error)
    }
}

function toggleShowPopUp() {
    show_popup.value = !show_popup.value
}
</script>

<template>
    <div id="main">
        <div id="return_arrow">
            <RouterLink v-if="status==='Administration'" id="back_arrow" to="/control-center">←</RouterLink>
            <RouterLink v-else-if="status==='Admin'" id="back_arrow" to="/admin-dashboard">←</RouterLink>
            <RouterLink v-else-if="status==='Super Admin'" id="back_arrow" to="/sup-admin-dashboard">←</RouterLink>
            <p>Retour</p>
        </div>

        <div class="background">
            <div id="form">
                <div id="header">
                    <p id="title">Ajouter un utilisateur</p>
                </div>

                <div id="dark_bar">
                    <p>Ajout d'un utilisateur</p>
                    <button id="button_more" v-on:click="display_more_area = !display_more_area;  addTeacher()">
                        {{ display_more_area ? '-' : '+' }}
                    </button>
                </div>

                <form v-show="display_more_area" method="post" v-on:submit.prevent="">
                    <a class="accordion_teacher dark_bar" data-accordion="add-modify-teacher">{{ title }}</a>

                    <div class="panel" style="display: flex">
                        <div style="margin-left: 15vw; padding-top: 1vw">
                            <div class="sub_div_panel">
                                <label style="font-size: 1vw;">Nom : </label>
                                <input type="text" class="input" style=" height: 3vh; width: 17vw; font-size: 1.5vh;" v-model="teacher_name">
                                <input style="margin-left: 11.5vw" class="btn1" type="reset" value="Annuler" v-on:click="display_more_area = !display_more_area" />
                            </div>

                            <p v-if="errors.name" class="error_message" style="text-align: left">{{ error_messages.name }}</p>

                            <div class="sub_div_panel">
                                <label style="font-size: 1vw;">Prenom : </label>
                                <input type="text" class="input" style=" height: 3vh; width: 17vw; font-size: 1.5vh;" v-model="teacher_firstname">
                                <input style="margin-left: 10vw" id="save" class="btn1" type="button" value="Sauvegarder" v-on:click="save()" />
                            </div>

                            <p v-if="errors.firstname" class="error_message" style="text-align: left">{{ error_messages.firstname }}</p>

                            <div class="sub_div_panel">
                                <label style="font-size: 1vw;">Mail : </label>
                                <input type="text" class="input" style=" height: 3vh; width: 17vw; font-size: 1.5vh;" v-model="teacher_mail">
                            </div>

                            <p v-if="errors.mail" class="error_message" style="text-align: left">{{ error_messages.mail }}</p>

                            <div class="sub_div_panel container-fluid">
                                <label style="font-size: 1vw;">Statut : </label>
                                <div class="component ue_div">
                                    <select id="ue_select" class="input" v-model="access_right" style=" height: 3vh; width: 8vw; font-size: 1.5vh;">
                                        <option value="">Sélectionner une UE</option>
                                        <option value="1">professeur</option>
                                        <option value="2">Administration</option>
                                    </select>
                                </div>
                            </div>

                            <p v-if="errors.mail" class="error_message" style="text-align: left">{{ error_messages.mail }}</p>

                        </div>

                    </div>
                </form>
            </div>

            <div class="container-fluid" style="align-items: start;">                
                <div id="form_resources" style="width: 50%;">
                    <div class="container-fluid spb" style="font-size: 1.5vw; color: var(--main-theme-secondary-color);">
                        <p v-if="teachers.length > 0">Professeurs enregistrés : </p>
                        <p v-else>Aucun professeurs n'a été enregistré</p>
                    
                        <div style="display: flex; align-items: center">
                            <div v-show="show_popup" id="popup">
                                Vous pouvez modifier le statut/droit d'accès d'un utilisateur en cliquant sur le bouton modifier.
                            </div>
                            <p v-if="status" class="btn_how_to" @click="toggleShowPopUp">ⓘ</p>
                        </div>
                    </div>

                    <div v-for="teacher in teachers" :key="teacher.idUser" style="color: white" >
                        <a class="accordion_teacher" id="dark_bar">{{teacher.user.firstname}} {{teacher.user.lastname}}</a>

                        <div class="panel">
                            <div class="hours_grid" style="gap: 1vw">
                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Nom : </p>
                                    <p>{{teacher.user.firstname}}</p>
                                </div>

                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Prenom : </p>
                                    <p>{{teacher.user.lastname}}</p>
                                </div>

                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Identifiant : </p>
                                    <p>{{teacher.user.username}}</p>
                                </div>

                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Mail : </p>
                                    <p>{{teacher.user.mail}}</p>
                                </div>
                            </div>
                            <div style="background-color: transparent; display: flex; padding: 0; margin-top: 0; margin-bottom: 1vh; justify-content: center; align-items: center">
                                <input class="btn1" type="button" value="Supprimer" v-on:click="deleteTeacher(teacher.idUser)"/>
                                <input class="btn1" type="button" value="Modifier" v-on:click="is_modifying = true; display_more_area = true; modify(teacher)" />
                            </div>
                        </div>
                    </div>
                </div>
                                
                <div id="form_resources" style="width: 50%;">
                    <div class="container-fluid spb" style="font-size: 1.5vw; color: var(--main-theme-secondary-color);">
                        <p v-if="administrations.length > 0">Utilisateurs administration enregistrés : </p>
                        <p v-else>Aucun utilisateur n'a été enregistré</p>
                    
                        <div style="display: flex; align-items: center">
                            <div v-show="show_popup" id="popup">
                                Vous pouvez modifier le statut/droit d'accès d'un utilisateur en cliquant sur le bouton modifier.
                            </div>
                            <p v-if="status" class="btn_how_to" @click="toggleShowPopUp">ⓘ</p>
                        </div>
                    </div>

                    <div v-for="adminUser in administrations" :key="adminUser.idUser" style="color: white" >
                        <a class="accordion_teacher" id="dark_bar">{{adminUser.user.firstname}} {{adminUser.user.lastname}}</a>

                        <div class="panel">
                            <div class="hours_grid" style="gap: 1vw">
                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Nom : </p>
                                    <p>{{adminUser.user.firstname}}</p>
                                </div>

                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Prenom : </p>
                                    <p>{{adminUser.user.lastname}}</p>
                                </div>

                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Identifiant : </p>
                                    <p>{{adminUser.user.username}}</p>
                                </div>

                                <div style="display: flex; padding-top: 0; gap: 0.3vw">
                                    <p>Mail : </p>
                                    <p>{{adminUser.user.mail}}</p>
                                </div>
                            </div>
                            <div style="background-color: transparent; display: flex; padding: 0; margin-top: 0; margin-bottom: 1vh; justify-content: center; align-items: center">
                                <input class="btn1" type="button" value="Supprimer" v-on:click="deleteTeacher(adminUser.idUser)"/>
                                <input class="btn1" type="button" value="Modifier" v-on:click="is_modifying = true; display_more_area = true; modify(adminUser)" />
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>

<style scoped>
</style>
