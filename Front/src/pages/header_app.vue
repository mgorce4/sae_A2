<script setup>
import { status, userName, removeUser } from '../main'
import { ref, computed } from 'vue'
import how_to_administration from '../userGuidePages/how_to_administration.vue'
import how_to_teacher from '../userGuidePages/how_to_teacher.vue'
import { router } from '@/router'

status.value = localStorage.status
userName.value = localStorage.lastname + ' ' + localStorage.firstname

const show_how_to_popup = ref(false)

const togglePopup = () => {
    show_how_to_popup.value = !show_how_to_popup.value
}

const routes_how_to = {
    Administration: how_to_administration,
    Professeur: how_to_teacher,
}

const routes = {
    Administration: '/dashboard-administration',
    Professeur: '/teacher-dashboard',
}

const current_how_to = computed(() => {
    return routes_how_to[status.value] || null
})

const goToDashboard = () => {
    router.push(routes[status.value] || '/')
}

const handleDisconnect = () => {
    removeUser()
    router.push('/')
}
</script>

<template>
    <header id="header_div">
        <div id="header_top" class="container-fluid spb">
            <div id="app_name_and_logo" class="container-fluid">
                <a id="unilim_logo" @click="goToDashboard">
                    <img
                        src="/media/unilim_logo.webp"
                        style="margin: 0; padding: 0; width: 100%; height: 100%"
                        alt="logo"
                    />
                </a>
                <div id="dividing_line"></div>
                <p id="app_name" @click="router.push('/syncadia-presentation')" style="cursor: pointer">Syncadia</p>
            </div>
            <div v-show="status" id="user_name_and_pp" class="container-fluid">
                <p v-if="userName" id="user_name">{{ userName }}</p>
                <img
                    id="profile_picture"
                    src="/media/no_profile_picture.webp"
                    alt="profile_picture"
                />
            </div>
        </div>
        <div id="red_rect" class="container-fluid spb">
            <p v-if="status" id="user_status">Statut : {{ status }}</p>

            <div style="display: flex; align-items: center">
                <p v-if="status" class="btn_how_to" @click="togglePopup">ⓘ</p>
                <a v-if="status" id="btn_disconnect" v-on:click="handleDisconnect"
                    >Déconnexion</a
                >
            </div>
        </div>
    </header>

    <div v-if="show_how_to_popup" class="popup-overlay">
        <div class="popup-content" @click.stop>
            <button class="popup-close" @click="togglePopup">&times;</button>
            <component :is="current_how_to" />
        </div>
    </div>
</template>

<style>
/* -- Header -- */
#header_div {
    width: 100%;
    height: 8vw;
}

#header_top {
    width: 94%;
    height: 70%;
    padding: 0 3%;
}

#app_name_and_logo {
    width: 50%;
}

#unilim_logo {
    cursor: pointer;
    height: 80%;
    width: calc(height);
    margin: 0 2.5vw 0 0;
}

#dividing_line {
    background-color: var(--header-color);
    width: 0.2vw;
    height: 70%;
}

#app_name {
    font-size: 2.8vw;
    height: fit-content;
    margin: 0 0 0 2.5vw;
    color: var(--header-color);
    font-weight: bold;
}

#user_name_and_pp {
    justify-content: flex-end;
    width: 50%;
}

#user_name {
    font-size: 1.2vw;
    margin: 0 0.5vw;
}

#profile_picture {
    cursor: pointer;
    border-radius: 3vw;
    height: 60%;
    width: calc(height);
}

#red_rect {
    background-color: var(--header-footer-color);
    width: 94%;
    height: 3vw;
    padding: 0 3%;
}

#user_status {
    font-size: 1.5vw;
    color: var(--main-theme-secondary-color);
    text-shadow: 0 0 5px var(--main-theme-tertiary-color);
    justify-content: center;
}

#btn_disconnect {
    height: 1.5vw;
    width: 7vw;
    padding: 0.2vw;
    border-width: 0.15vw;
    border-radius: 0.5vw;
    font-size: 1vw;
    text-decoration: none;
    align-content: center; /*center the text in the button */
    text-align: center;
    background-color: var(--header-color);
    color: var(--main-theme-secondary-color);
}

#btn_disconnect:hover {
    cursor: pointer;
}
</style>
