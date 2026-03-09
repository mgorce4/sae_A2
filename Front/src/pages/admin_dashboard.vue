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
   <button class="btn1" to="/mccc-select-path">MCCC</button>
   <button class="btn1" @click="goToNext('/admin-ressources-sheet',Admin)">Vue Fiche professeur</button>
   <button class="btn1" @click="goToNext('/add-teacher-page',Admin)">Vue professseur</button>
</template>

<style>
</style>
