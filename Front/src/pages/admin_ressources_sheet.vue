<script setup>
import { status } from '@/main'
import { getStatusFromToken } from '@/utils/jwt'



// Valeur venant du token
status.value = getStatusFromToken()
console.log(status.value)

</script>

<template>
    <div id="return_arrow">
        <RouterLink id="back_arrow" :to="`/admin-dashboard`">←</RouterLink>
        <p>Retour</p>
    </div>
    <p>Hello fiches ressources non modifiables !</p>
    <p v-if="status">{{ status}}</p>
</template>