<script setup>
import teacher_dashboard from '@/pages/teacher_dashboard.vue'
import administration_dashboard from '@/pages/administration_dashboard.vue'
import admin_dashboard from '@/pages/admin_dashboard.vue'
import super_admin_dashboard from '@/pages/SupAdminDasboard.vue'
import { ref } from 'vue'
import { getAccessRightsFromToken } from '@/utils/jwt.js'

const map_colors = {
    'Professeur': 'var(--onglet-teacher-color)',
    'Administration': 'var(--onglet-administration-color)',
    'Admin': 'var(--sub-scrollbar-color)',
    'Super Admin': 'var(--pop-up-background-color)',
}

let isTeacher = ref(true)
let isAdministration = ref(false)
let isAdmin = ref(false)
let isSuperAdmin = ref(false)

const changeColorsTemplate = async (status) => {
    const color = map_colors[status] || 'var(--onglet-teacher-color)'
    document.getElementById("template").style.backgroundColor = color

    switch (status) {
        case 'Professeur':
            isTeacher.value = true
            isAdministration.value = false
            isAdmin.value = false
            isSuperAdmin.value = false
            break
        case 'Administration':
            isTeacher.value = false
            isAdministration.value = true
            isAdmin.value = false
            isSuperAdmin.value = false
            break
        case 'Admin':
            isTeacher.value = false
            isAdministration.value = false
            isAdmin.value = true
            isSuperAdmin.value = false
            break
        case 'Super Admin':
            isTeacher.value = false
            isAdministration.value = false
            isAdmin.value = false
            isSuperAdmin.value = true
            break
    }
}

const access_rights = getAccessRightsFromToken()

const hasTeacherRight = access_rights.includes(1)
const hasAdministrationRight = access_rights.includes(2)
const hasAdminRight = access_rights.includes(3)
const hasMultipleRights = access_rights.includes(4)

</script>

<template>
    <div>
        <div>
            <div style="display: flex">
                <p class="onglet" v-if="hasTeacherRight" v-on:click="changeColorsTemplate('Professeur')">Professeur</p>
                <p class="onglet" v-if="hasAdministrationRight" style="margin-left: 0; background-color: var(--onglet-administration-color)" v-on:click="changeColorsTemplate('Administration')">Administration</p>
                <p class="onglet" v-if="hasAdminRight" style="margin-left: 0; background-color: var(--sub-scrollbar-color)" v-on:click="changeColorsTemplate('Admin')">Admin</p>
                <p class="onglet" v-if="hasMultipleRights" style="margin-left: 0; background-color: var(--pop-up-background-color)" v-on:click="changeColorsTemplate('Super Admin')">Super Admin</p>
            </div>
            <div id="template">
                <teacher_dashboard v-if="isTeacher" />
                <administration_dashboard v-else-if="isAdministration" />
                <admin_dashboard v-else-if="isAdmin" />
                <super_admin_dashboard v-else-if="isSuperAdmin" />
            </div>

        </div>
    </div>
</template>

<style scoped>
.onglet {
    background-color: var(--onglet-teacher-color);
    text-align: center;
    padding: 1vw;
    border-top-right-radius: 1vw;
    border-top-left-radius: 1vw;
    margin-bottom: 0;
    margin-left: 2vw;
    color: white;
}

.onglet:hover {
    cursor: pointer;
}

#template {
    border-bottom-left-radius: 1vw;
    border-bottom-right-radius: 1vw;
    border-top-right-radius: 1vw;
    padding: 1vw;
    margin: 0 2vw 2vw;
    background-color: var(--onglet-teacher-color);
}

</style>


