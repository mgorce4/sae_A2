<script setup>
import { router } from '@/router'
import { status, institutionLocation } from '../main'
import { getInstitutionLocationFromToken } from '@/utils/jwt.js'
status.value = 'Admin'
institutionLocation.value = getInstitutionLocationFromToken()

const goToNext = (url, status) => {
    router.push({
        path: url,
        query: {
            status: status
        }
    })
}

</script>

<template>
    <div id="admin_template">
        <a class="button" @click="goToNext('/mccc-select-path',Admin)">MCCC</a>
        <a class="button" @click="goToNext('/admin-ressources-sheet',Admin)">Vue Fiche professeur</a>
        <a class="button" @click="goToNext('/admin-user',Admin)">Vue utilisateur</a>
    </div>

</template>

<style>

#admin_template {
    display: flex;
    flex-direction: column;
    align-items: center;
    max-width: 30vw;
    margin: 0 auto;
    gap: 1vw;
    padding: 2vw
}

</style>
