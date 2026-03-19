<script setup>
import { onMounted, ref } from 'vue'
import axios from 'axios'
import { API_BASE_URL } from '@/config/api.js'
import {
    getIdInstitutionFromToken,
} from '@/utils/jwt.js'

const show_popup = ref(false)

const toggleShowPopUp = () => {
    show_popup.value = !show_popup.value
}

const mail_object = ref("")
const mail_body = ref("")

const sendMail = () => {
    console.log("test mail")
}

const receiver = ref("")

const teacher = ref([])

onMounted(async () => {
    await axios.get(`${API_BASE_URL}/api/access-rights`).then((response) => {
        for (let i = 0; i < response.data.length; i++) {
            teacher.value.push(response.data.filter((teacher) => teacher.accessRight === 1 &&
            teacher.user.institution.idInstitution === getIdInstitutionFromToken())[i])
        }
    })
})

</script>

<template>
    <div id="main">
        <div id="return_arrow">
            <RouterLink id="back_arrow" to="/mccc-select-form">←</RouterLink>
            <p>Retour</p>
        </div>
        <form v-on:submit.prevent="sendMail" id="mail_form">
            <p id="title">Envoi de mail</p>

            <div>
                <label class="label_login" style="padding-top: 0vw">Destinataire</label>
                <input class="keyword-input" type="text" required v-on:focus="toggleShowPopUp()" v-on:blur="toggleShowPopUp()"
                       v-model="receiver" style="max-width: 10vw; margin-bottom: 2vw; margin-left: 1vw">
            </div>

            <div v-show="show_popup" v-for="teacher in teacher" :key="teacher">
                {{ teacher.user.firstName }} {{ teacher.user.lastName }}
            </div>

            <label class="label_login" style="padding-top: 0vw">Objet du mail</label>
            <input class="keyword-input" type="text" required v-model="mail_object" style="max-width: 30vw">

            <label class="label_login" style="padding-top: 2vw">Corp du mail</label>
            <textarea class="modality-textarea" v-model="mail_body"></textarea>
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
