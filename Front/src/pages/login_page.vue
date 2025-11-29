<script setup>
    import { status } from '../main.js'

    status.value = ''
</script>

<template>
    <form id="blue-rect" v-on:submit.prevent="">
        <div id="login-top" class="container-fluid spe">
            <img id="profile-picture" src="./../../media/no_profile_picture.webp" alt="profile-picture">
            <div id="login-fill-infos">
                <label class="label-login">Identifiant</label>
                <input class="input-login" for="username" type="text" required v-model="username">
                <label class="label-login">Mot de passe </label>
                <input class="input-login" for="password" type="password" required v-model="password">
            </div>
        </div>
        <div id="btn-login" class="container-fluid spe">
            <input id="btn-cancel-login" class="btn1" type="reset" value="Annuler">
            <input id="btn-connect-login" class="btn1" type="submit" value="Se connecter">
        </div>
    </form>
</template>

<style>
#blue-rect {
    width: 40vw;
    height: 25vw;
    background-color: #3D4375;
    border-radius: 2vw;
    margin: 5vw 30vw;
}

#login-top {
    height: 75%;
}

#login-fill-infos {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    width: 50%;
}

.label-login {
    padding: 10% 0 5% 0;
    margin: 0;
    font-size: 1.5vw;
    color: #FFFFFF;
}

.input-login {
    width: 80%;
    font-size: 1vw;
    padding: 1%;
    border-radius: 0.5vw;
    border-width: 0.2vw;
}

#btn-login {
    width: 100%;
    height: 25%;
    align-items: start;
}
</style>