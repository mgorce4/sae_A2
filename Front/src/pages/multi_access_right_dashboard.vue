<script setup>
import teacher_dashboard from '@/pages/teacher_dashboard.vue'
import administration_dashboard from '@/pages/administration_dashboard.vue'
import { ref } from 'vue'

const map_colors = {
    'Professeur': 'var(--onglet-techer-color)',
    'Administration': 'var(--onglet-administration-color)',
    'Admin': 'var(--onglet-admin-color)',
}

let isTeacher = ref(true)
let isAdministration = ref(false)

const changeColorsTemplate = async (status) => {
    const color = map_colors[status] || 'var(--onglet-techer-color)'
    document.getElementById("template").style.backgroundColor = color

    switch (status) {
        case 'Professeur':
            isTeacher.value = true
            isAdministration.value = false
            break
        case 'Administration':
            isTeacher.value = false
            isAdministration.value = true
            break
        case 'Admin':
            isTeacher.value = false
            isAdministration.value = false
            break
    }
}

</script>

<template>
    <div>
        <div>
            <div style="display: flex">
                <p class="onglet" v-on:click="changeColorsTemplate('Professeur')">Professeur</p>
                <p class="onglet" style="margin-left: 0; background-color: var(--onglet-administration-color)" v-on:click="changeColorsTemplate('Administration')">Administration</p>
                <p class="onglet" style="margin-left: 0; background-color: var(--onglet-admin-color)" v-on:click="changeColorsTemplate('Admin')">Admin</p>
            </div>
            <div id="template">
                <teacher_dashboard v-if="isTeacher" />
                <administration_dashboard v-else-if="isAdministration" />
                <p v-else style="color: white">En cours...</p>
            </div>

        </div>
    </div>
</template>

<style scoped>
.onglet {
    background-color: var(--onglet-techer-color);
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
    background-color: var(--onglet-techer-color);
}

</style>


