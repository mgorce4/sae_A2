<script setup>

import { status, institutionLocation } from '../main'
status.value = 'Admin'
institutionLocation.value = localStorage.institutionLocation


</script>

<template>

    <p>Hello Admin !</p>
</template>

<style>
</style>
