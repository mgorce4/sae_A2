<script setup>
import { onMounted, ref } from 'vue'
import axios from 'axios'
import { API_BASE_URL } from '@/config/api.js'
import {
    getIdInstitutionFromToken
} from '@/utils/jwt.js'
import { router } from '@/router/index.js'
import { useRoute } from 'vue-router'

const show_popup = ref(false)

const toggleShowPopUp = () => {
    show_popup.value = !show_popup.value
}

const mail_object = ref("")
const mail_body = ref("")

const sendMail = async () => {
    if (!receiver.value || !receiver.value.user || !receiver.value.user.idUser) {
        alert("Veuillez sélectionner un destinataire.")
        return
    }

    try {
        const params = {
            userId: receiver.value.user.idUser,
            subject: mail_object.value,
            message: mail_body.value
        }

        const response = await axios.post(`${API_BASE_URL}/api/send-mail-to-user`, { params })
        alert("Mail envoyé avec succès !")
        mail_object.value = ""
        mail_body.value = ""
        receiver.value = ""
        document.getElementById("input_receiver").value = ""

        console.log('Mail envoyé avec succès:', response.data)
    } catch (err) {
        console.error('Erreur envoi mail', err)
        alert("Erreur lors de l'envoi du mail. Veuillez réessayer.")
    }
}

const receiver = ref("")

const teachers = ref([])

onMounted(async () => {
    try {
        const response = await axios.get(`${API_BASE_URL}/api/access-rights`)
        teachers.value = response.data

        teachers.value = teachers.value
            .filter((teacher) => teacher && teacher.user && teacher.user.institution && teacher.user.institution.idInstitution === getIdInstitutionFromToken())
            .filter((teacher) => teacher && (teacher.accessRight === 1 || teacher.accessRight === '1'))
    } catch (err) {
        console.error('Erreur récupération access-rights', err)
    }
})

const getUserFirstName = (teacher) => {
    return teacher && teacher.user ? teacher.user.firstname : ''
}

const getUserLastName = (teacher) => {
    return teacher && teacher.user ? teacher.user.lastname : ''
}

const goToResourceSheetDisplay = (url, label) => {
    router.push({
        path: url,
        query: {
            label: label
        }
    })
}

const route = useRoute()
const resource_label = route.query.label

const getReceiver = (teacher) => {
    const input_receiver = document.getElementById("input_receiver")

    input_receiver.value = getUserFirstName(teacher) + " " + getUserLastName(teacher)
    show_popup.value = false

    try {
        receiver.value = JSON.parse(JSON.stringify(teacher))
    } catch (e) {
        receiver.value = { user: teacher.user ? { id: teacher.user.id, firstname: teacher.user.firstname, lastname: teacher.user.lastname } : null }
    }
}

</script>

<template>
    <div id="main">
        {{teachers}}
        <div id="return_arrow">
            <button id="back_arrow" @click="goToResourceSheetDisplay('/resource-sheet-display', resource_label)">←</button>
            <p>Retour</p>
        </div>
        <form v-on:submit.prevent="sendMail" id="mail_form">
            <p id="title">Envoi de mail</p>

            <div style="margin-bottom: 2vw">
                <label class="label_login" style="padding-top: 0vw">Destinataire : </label>

                <div style="display: block; margin-top: 1vw">
                    <input class="keyword-input" type="text" required v-on:focus="toggleShowPopUp()"
                           style="max-width: 15vw" id="input_receiver">

                    <div  v-show="show_popup" class="show_main_teacher" style="max-width: 14.5vw" >
                        <div v-for="teacher in teachers" :key="teacher.user ? teacher.user.id : teacher" @click="getReceiver(teacher)" class="main_teacher_name" id="teacher_selector">
                            {{getUserFirstName(teacher)}} {{getUserLastName(teacher)}}
                        </div>
                    </div>
                </div>
            </div>

            <label class="label_login" style="padding-top: 0vw">Objet du mail : </label>
            <input class="keyword-input" type="text" required v-model="mail_object" style="max-width: 30vw">

            <label class="label_login" style="padding-top: 2vw">Corp du mail : </label>
            <textarea class="modality-textarea" v-model="mail_body"></textarea>

            <button type="submit" class="btn1" style="margin-top: 2vw; width: 10vw; align-self: center">
                Envoyer
            </button>
        </form>
    </div>
</template>

<style scoped>

#mail_form {
    background-color: var(--main-theme-background-color);
    border-radius: 1vw;
    padding: 2vw;
    margin-top: 2vw;
    color: white;
    display: flex;
    flex-direction: column;
}

#title {
    display: block;
    margin-block-start: 1em;
    margin-block-end: 1em;
    margin-inline-start: 0px;
    margin-inline-end: 0px;
    unicode-bidi: isolate;
}

</style>
