<script setup>
import { userName, institutionLocation, removeUser, status } from '../main.js'
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { router } from '@/router'
import { API_BASE_URL } from '@/config/api.js'
import { setToken, getAccessRightsFromToken, getFirstnameFromToken, getLastnameFromToken, getInstitutionLocationFromToken } from '@/utils/jwt.js'

onMounted(() => {
    removeUser()
})

const username = ref('')
const password = ref('')
const loginError = ref(false)
const redirectlink = ref('')

async function addItem() {
    loginError.value = false
    try {
        const response = await axios.post(`${API_BASE_URL}/api/auth/signin`, {
            username: username.value,
            password: password.value,
        })

        const data = response.data

        // Stocker uniquement le JWT — toutes les infos utilisateur sont dans son payload signé
        setToken(data.token)

        // Mettre à jour les états réactifs globaux depuis le payload JWT
        userName.value = (getLastnameFromToken() + ' ' + getFirstnameFromToken()).trim()
        institutionLocation.value = getInstitutionLocationFromToken()

        // Les droits d'accès sont lus directement depuis le payload du JWT (signé)
        redirect(getAccessRightsFromToken())
    } catch (error) {
        loginError.value = true
        removeUser()
    }
}

function redirect(accessRights) {
    if (accessRights.length === 1) {
        redirectSingle(accessRights[0])
    } else if (accessRights.length > 1) {
        status.value = 'Multiple'
        redirectlink.value = '/multi_access_right_dashboard'
    }
    router.push(redirectlink.value)
}

function redirectSingle(accessRight) {
    switch (accessRight) {
        case 1:
            status.value = 'Professeur'
            redirectlink.value = '/teacher-dashboard'
            break
        case 2:
            status.value = 'Administration'
            redirectlink.value = '/dashboard-administration'
            break
        case 3:
            status.value = 'Admin'
            redirectlink.value = '/admin-dashboard'
            break
    }
}
</script>

<template>
    <form id="blue_rect" method="post" v-on:submit.prevent="addItem">
        <div id="login_top" class="container-fluid spe">
            <img
                id="profile_picture"
                src="/media/no_profile_picture.webp"
                alt="profile_picture"
            />
            <div id="login_fill_infos">
                <label class="label_login">Identifiant</label>
                <input class="input_login" type="text" required v-model="username" />
                <label class="label_login">Mot de passe </label>
                <input class="input_login" type="password" required v-model="password" />
                <p v-if="loginError" class="login_error">Identifiant ou mot de passe incorrect</p>
            </div>
        </div>
        <div id="btn_login" class="container-fluid spe">
            <input class="btn1" type="reset" value="Annuler" />
            <input class="btn1" type="submit" value="Se connecter" />
        </div>
    </form>
</template>

<style>
#blue_rect {
    width: 40vw;
    height: 25vw;
    background-color: var(--main-theme-background-color);
    border-radius: 2vw;
    margin: 5vw 30vw;
}

#login_top {
    height: 75%;
}

#login_fill_infos {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    width: 50%;
}

.label_login {
    padding: 10% 0 5% 0;
    font-size: 1.5vw;
    color: var(--main-theme-secondary-color);
}

.input_login {
    width: 80%;
    font-size: 1vw;
    padding: 1%;
    border-radius: 0.5vw;
    border-width: 0.2vw;
}

#btn_login {
    width: 100%;
    height: 25%;
    align-items: start;
}

.login_error {
    color: var(--error-color);
    font-size: 1vw;
    margin-top: 0.5vw;
    font-weight: bold;
}
</style>
