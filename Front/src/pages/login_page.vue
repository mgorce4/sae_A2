<script setup>
    import { status } from '../main.js'
    import { computed, ref, onMounted } from 'vue'
    import axios from 'axios'

    status.value = ''

    const users = ref([])
    const access_rights = ref([])

    const user_access_rights = ref([])

    onMounted(async () => {
        axios.get('http://localhost:8080/api/users').then(response => (users.value = response.data))
        axios.get('http://localhost:8080/api/access-rights').then(response => (access_rights.value = response.data))

        if (localStorage.getItem('access_rights')) {
            try {
                user_access_rights.value = JSON.parse(localStorage.getItem('access_rights'));
            } catch(e) {
                localStorage.removeItem('access_rights')
            }
        }
    })

    const users_access_right = computed(() => {
        // Join between users and access_rights where users.idUser = access_rights.idUser
        return users.value.map((user) => {
            const userRights = access_rights.value.filter((ar) => ar.idUser === user.idUser)
            return {
                ...user,
                accessRights: userRights
            }
        }).filter(user => user.accessRights.length > 0)
    })

    let username = null
    let password = null
    function addItem() {
        localStorage.access_rights = null

        verifyUser(username, password)

        // set variables back to null
        username = null
        password = null
    }

    function is_username_and_password_ok(username, user, password) {
      return username == user.username && password == user.password
    }

    function verifyUser(username, password) {
        users.value.forEach((user) => {
            if (is_username_and_password_ok(username, user, password)) {
                localStorage.username = username
                localStorage.password = password
                console.log("Utilisateur : ", user.username)
                verifyAccessRight()
            } else {
                console.log("Utilisateur non trouvé")
            }
        });
    }

    function number_of_access_right(access_right) {
      return access_right.accessRights.length
    }

    function verifyAccessRight() {
        user_access_rights.value = []
        console.log(users_access_right.value)
        users_access_right.value.forEach((access_right) => {
            if (localStorage.username == access_right.username) {
                console.log("nombre d'élements : ", number_of_access_right(access_right))
                access_right.accessRights.forEach(access_right => {
                    console.log(access_right)
                    user_access_rights.value.push(access_right.accessRight)
                });
            }
        });
        const parsed = JSON.stringify(user_access_rights.value)
        localStorage.setItem('access_rights', parsed)
    }
</script>

<template>
    <form id="blue-rect" method="post" v-on:submit.prevent="addItem">
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
