<script setup>
    import { status, userName, institutionLocation, removeUser } from '../main.js'
    import { computed, ref, onMounted } from 'vue'
    import axios from 'axios'

    status.value = ''
    institutionLocation.value = ''

    const users = ref([])
    const access_rights = ref([])

    const user_access_rights = ref([])

    onMounted(async () => {
        removeUser()

        axios.get('http://localhost:8080/api/users').then(response => (users.value = response.data))
        axios.get('http://localhost:8080/api/access-rights').then(response => (access_rights.value = response.data))

        if (localStorage.getItem('access_rights')) {
            try {
                user_access_rights.value = JSON.parse(localStorage.getItem('access_rights'));
            } catch {
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

    const username = ref('')
    const password = ref('')
    const loginError = ref(false)

    function addItem() {
        loginError.value = false
        verifyUser(username.value, password.value)

        const accessRights = JSON.parse(localStorage.getItem('access_rights'))
        if (accessRights && accessRights.length === 1) {
            redirect(accessRights[0])
        } else if (accessRights && accessRights.length > 1) {
            // Multiple access rights - could show a selection menu
            // For now, redirect to the first one
            redirect(accessRights[0])
        }
    }

    function is_username_and_password_ok(username, user, password) {
      return username === user.username && password === user.password
    }

    function verifyUser(username, password) {
        const user = users.value.find((user) => is_username_and_password_ok(username, user, password))

        if (user) {
            localStorage.username = username
            localStorage.password = password
            localStorage.firstname = user.firstname
            localStorage.lastname = user.lastname
            localStorage.idUser = user.idUser
            localStorage.idInstitution = user.institution.idInstitution
            localStorage.institutionName = user.institution.name
            localStorage.institutionLocation = user.institution.location
            verifyAccessRight()
        } else {
            loginError.value = true
            removeUser()
        }
    }

    function number_of_access_right(access_right) {
      return access_right.accessRights.length
    }

    function verifyAccessRight() {
        user_access_rights.value = []
        users_access_right.value.forEach((access_right) => {
            if (localStorage.username === access_right.username) {
                access_right.accessRights.forEach(access_right => {
                    user_access_rights.value.push(access_right.accessRight)
                });
            }
        });
        const parsed = JSON.stringify(user_access_rights.value)
        localStorage.setItem('access_rights', parsed)
    }

    function redirect(access_right) {
        userName.value = localStorage.lastname + " " + localStorage.firstname
        switch (access_right) {
            case 1:
                localStorage.status = "Professeur"
                window.location.hash = '#/teacher-dashboard'
                break;
            case 2:
                localStorage.status = "Administration"
                window.location.hash = '#/dashboard-administration'
                break;
            case 3:
                localStorage.status = "Admin"
                window.location.hash = '#/'
                break;
        }
    }
</script>

<template>
    <form id="blue_rect" method="post" v-on:submit.prevent="addItem">
        <div id="login_top" class="container-fluid spe">
            <img id="profile_picture" src="./../../media/no_profile_picture.webp" alt="profile_picture">
            <div id="login_fill_infos">
                <label class="label_login">Identifiant</label>
                <input class="input_login" type="text" required v-model="username">
                <label class="label_login">Mot de passe </label>
                <input class="input_login" type="password" required v-model="password">
                <p v-if="loginError" class="login_error">Identifiant ou mot de passe incorrect</p>
            </div>
        </div>
        <div id="btn_login" class="container-fluid spe">
            <input class="btn1" type="reset" value="Annuler">
            <input class="btn1" type="submit" value="Se connecter">
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
