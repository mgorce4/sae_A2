<script setup>

import { nextTick, onMounted, ref } from 'vue'
import axios from 'axios'
import { API_BASE_URL } from '@/config/api.js'

const teacher_acces_right = 1

let display_more_area = ref(false)
let is_modifying = ref(false)

let title = ref("")

const teacher_name = ref("")
const teacher_firstname = ref("")
const teacher_id = ref(0)

const errors = ref({
    name: false,
    firstname: false,
})

const error_messages = ref({
    name: "Le nom doit être renseigné",
    firstname: "Le prenom doit être renseigné",
})

const teachers = ref([])

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
    const response = await axios.get('\${API_BASE_URL}/api/access-rights')
    teachers.value = response.data.filter((ar) => ar.accessRight === 1)

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

    if (hasError) {
        return
    }

    try {
        // is user logged in
        if (!localStorage.idUser) {
            alert('Erreur : Veuillez vous reconnecter.')
            return
        }

        const payload = {
            firstname : teacher_firstname.value,
            lastname : teacher_name.value,
            username : getUsername(),
            password : getUsername() + '123',
            institution : {
                idInstitution : parseInt(localStorage.idInstitution),
                name : localStorage.institutionName,
                location : localStorage.institutionLocation,
            },
        }

        if (!is_modifying.value) {
            let user_response = await axios.post('http://localhost:8080/api/users', payload);
            [teacher_firstname, teacher_name].forEach((r) => r.value = '')
            display_more_area.value = false

            // get the id of the new user
            let user = user_response.data
            let id = user.idUser

            const access_right_payload = {
                accessRight : teacher_acces_right,
                idUser : id,
            }

            await axios.post('http://localhost:8080/api/access-rights', access_right_payload);
        } else {
            const user_id = teacher_id

            await axios.put(`http://localhost:8080/api/users/${user_id.value}`, payload);

            [teacher_firstname, teacher_name].forEach((r) => r.value = '')
            display_more_area.value = false
            is_modifying.value = false
        }

        await reloadTeachers()
        attachAccordionListeners()

        console.log('professeur sauvegardée avec succès')
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
    const response = await axios.get('http://localhost:8080/api/access-rights')
    teachers.value = response.data.filter((ar) => ar.accessRight === teacher_acces_right).filter((teacher) => teacher.user.institution.idInstitution === parseInt(localStorage.idInstitution))
}

function modify(teacher) {
    title.value = "Modifier un professeur"
    teacher_name.value = teacher.user.lastname
    teacher_firstname.value = teacher.user.firstname
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
        let main_resources = await axios.get(`http://localhost:8080/api/main-teachers-for-resource/user/${id}`)
        let main_resources_data = main_resources.data

        if (main_resources_data.length > 0) {

            for (let i = 0; i < main_resources_data.length; i++) {
                await axios.delete(`http://localhost:8080/api/main-teachers-for-resource/user/${id}/resource/${main_resources_data[i].idResource}`)
            }

            for (let i = 0; i < main_resources_data.length; i++) {
                alert('Vous venez de supprimer un professeur référent de la ressource' + main_resources_data[i].resourceLabel + '. Veuillez rajouter un nouveau professeur référent pour cette ressource.')
            }
        }

        let normal_resources = await axios.get(`http://localhost:8080/api/teachers-for-resource/user/${id}`)
        let normal_resources_data = normal_resources.data

        if (normal_resources_data.length > 0) {

            for (let i = 0; i < normal_resources_data.length; i++) {
                await axios.delete(`http://localhost:8080/api/teachers-for-resource/user/${id}/resource/${normal_resources_data[i].idResource}`)
            }

            for (let i = 0; i < normal_resources_data.length; i++) {
                alert('Vous venez de supprimer un professeur enseignant de la ressource' + normal_resources_data[i].resourceLabel + '. Si besoin, veuillez rajouter un nouveau professeur enseignant pour cette ressource si nécessaire.')
            }
        }

        await axios.delete(`http://localhost:8080/api/users/${id}`)
        await axios.delete(`http://localhost:8080/api/access-rights/1/${id}`)

        await reloadTeachers()

        attachAccordionListeners()
    } catch (error) {
        console.error('Erreur lors de la suppression', error)
    }
}

</script>

<template>
    <div id="main">
        <div id="return_arrow">
            <RouterLink id="back_arrow" to="/control-center">←</RouterLink>
            <p>Retour</p>
        </div>

        <div id="background">
            <div id="form">
                <div id="header">
                    <p id="title">Ajouter un professeur</p>
                </div>

                <div id="dark_bar">
                    <h2>Ajouter un professeur</h2>
                    <button id="button_more" v-on:click="display_more_area = !display_more_area;  addTeacher()">
                        {{ display_more_area ? '-' : '+' }}
                    </button>
                </div>

                <form v-show="display_more_area" method="post" v-on:submit.prevent="">
                    <a class="accordion_teacher dark_bar" data-accordion="add-modify-teacher">{{ title }}</a>

                    <div class="panel" style="display: flex">
                        <div style="margin-left: 15vw; padding-top: 1vw">
                            <div class="sub_div_panel">
                                <label>Nom : </label>
                                <input type="text" class="input" v-model="teacher_name">
                                <input style="margin-left: 11.5vw" class="btn1" type="reset" value="Annuler" v-on:click="display_more_area = !display_more_area" />
                            </div>

                            <p v-if="errors.name" class="error_message" style="text-align: left">{{ error_messages.name }}</p>

                            <div class="sub_div_panel">
                                <label>Prenom : </label>
                                <input type="text" class="input" v-model="teacher_firstname">
                                <input style="margin-left: 10vw" id="save" class="btn1" type="button" value="Sauvegarder" v-on:click="save()" />
                            </div>

                            <p v-if="errors.firstname" class="error_message" style="text-align: left">{{ error_messages.firstname }}</p>
                        </div>

                    </div>
                </form>
            </div>

            <div id="form_resources">
                <p v-if="teachers.length > 0">Professeurs enregistrés : </p>
                <p v-else>Aucun professeurs n'a été enregistré</p>

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

                            <div style="background-color: transparent; display: flex; padding: 0; margin-bottom: 0; gap: 0.3vw; justify-content: center; align-items: center">
                                <input class="btn1" type="button" value="Supprimer" v-on:click="deleteTeacher(teacher.idUser)"/>
                                <input class="btn1" type="button" value="Modifier" v-on:click="is_modifying = true; display_more_area = true; modify(teacher)" />
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>

<style scoped>

#background {
    height: auto;
    background-color: var(--main-theme-background-color);
    border-radius: 15px;
    overflow-x: hidden;
    overflow-y: hidden;
    box-sizing: border-box;
    padding-bottom: 1vw;
}

#header {
    background-color: var(--main-theme-secondary-background-color);
    height: auto;
    border-radius: 10px;
    margin: 1vw;
    display: flex;
    justify-content: center;
    align-items: center;
}

.accordion_teacher,
#dark_bar > p {
    font-weight: lighter;
    font-size: 1.05vw;
}

.accordion_teacher {
    cursor: pointer;
    position: relative;
}

.accordion_teacher::after {
    content: '^';
    position: absolute;
    right: 1vw;
    transition: transform 0.3s ease;
    font-size: 0.9vw;
}

.accordion_teacher.active::after {
    transform: rotate(180deg);
}

.sub_div_panel {
    gap: 10px;
    margin-bottom: 0.7vw;
}
</style>
