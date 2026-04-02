<script setup>
import { onMounted, ref } from 'vue'
import axios from 'axios'
import { API_BASE_URL } from '@/config/api.js'
import {
    getToken
} from '@/utils/jwt.js'
import { router } from '@/router/index.js'
import { useRoute } from 'vue-router'

const mail_object = ref("")
const mail_body = ref("")

const sendMail = async () => {
    if (!receiver.value || !receiver.value.user || !receiver.value.user.idUser) {
        alert("Veuillez sélectionner un destinataire.")
        return
    }

    try {
        const token = getToken()
        const response = await axios.post(
            `${API_BASE_URL}/api/send-mail-to-user`,
            {
                userId: receiver.value.user.idUser,
                subject: mail_object.value,
                message: mail_body.value
            },
            {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            }
        )
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
const resource = ref("")

const route = useRoute()
const resourceId = route.query.resourceId
const label = ref("")

onMounted(async () => {
    try {
        console.log(resourceId)
        const responseResource = await axios.get(`${API_BASE_URL}/api/v2/resource-sheets/${resourceId}`)

        // Récupère les main teachers et teachers, stocke-les dans teachers.value
        const { data: mainTeachers } = await axios.get(`${API_BASE_URL}/api/main-teachers-for-resource`)
        teachers.value.push(...(mainTeachers || []))

        const { data: otherTeachers } = await axios.get(`${API_BASE_URL}/api/teachers-for-resource`)
        teachers.value.push(...(otherTeachers || []))

        // Normalise resourceId coté client (route.query peut être string)
        const resource_id_number = Number(resourceId)

        // Filtre les enseignants pour la ressource demandée (gère différentes formes possibles de l'objet)
        teachers.value = teachers.value.filter(teacher => {
            const id = teacher?.resource?.idResource ?? teacher?.idResource ?? teacher?.resourceId
            return Number(id) === resource_id_number
        })

        // Trie par resource id (ascendant) pour un affichage stable
        teachers.value.sort((a, b) => {
            const a_id = Number(a?.resource?.idResource ?? a?.idResource ?? a?.resourceId ?? 0)
            const b_id = Number(b?.resource?.idResource ?? b?.idResource ?? b?.resourceId ?? 0)
            return a_id - b_id
        })

        resource.value = responseResource.data
    } catch (err) {
        console.error('Erreur récupération access-rights', err)
    }

    label.value = resource.value.resourceLabel
})

const getUserFirstName = (teacher) => {
    return teacher && teacher.user ? teacher.user.firstname : ''
}

const getUserLastName = (teacher) => {
    return teacher && teacher.user ? teacher.user.lastname : ''
}

const getUserFullName = (teacher) => {
    const firstName = getUserFirstName(teacher)
    const lastName = getUserLastName(teacher)
    return `${firstName} ${lastName}`.trim()
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
    {{teachers}}
    <div id="main">
        <div id="return_arrow">
            <button id="back_arrow" @click="goToResourceSheetDisplay('/resource-sheet-display', label)">←</button>
            <p>Retour</p>
        </div>
        <form v-on:submit.prevent="sendMail" id="mail_form">
            <p id="title" style="margin-bottom: 0">Envoi de mail</p>
            <p style="margin-bottom: 2vw">Vous envoyez un mail concernant la ressource {{resource.resourceLabel}} {{resource.resourceName}}</p>

            <div style="margin-bottom: 2vw">
                <label class="label_login" style="padding-top: 0vw">Destinataire : </label>

                <div v-for="teacher in teachers" :key="`${teacher.user?.idUser}-${teacher.resource?.idResource}`">
                  <input type="radio" :id="`teacher_${teacher.user?.idUser}_${teacher.resource?.idResource}`" name="receiver" v-model="receiver" :value="teacher">
                  <label :for="`teacher_${teacher.user?.idUser}_${teacher.resource?.idResource}`">{{ getUserFullName(teacher) }}</label>
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
