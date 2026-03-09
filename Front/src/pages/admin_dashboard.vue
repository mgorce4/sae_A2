<script setup>

import { status, institutionLocation } from '../main'
import { getInstitutionLocationFromToken } from '@/utils/jwt.js'
status.value = 'Admin'
institutionLocation.value = getInstitutionLocationFromToken()


</script>

<template>

    <p>Hello Admin !</p>
</template>

<style>
</style>
