<script setup>
import { onMounted } from 'vue'
import { institutionLocation } from '../main'

onMounted(async () => {
    institutionLocation.value = localStorage.institutionLocation
})
</script>

<template>
    <footer id="footer_red_rect" class="container-fluid spb">
        <p v-if="institutionLocation" id="establishment_name">{{ institutionLocation }}</p>
        <p id="edit_year">{{ new Date().getFullYear() }}</p>
    </footer>
</template>

<style>
/* -- Footer -- */
#footer_red_rect {
    width: 94%;
    height: 3.5vw;
    padding: 0 3%;
    background-color: var(--header-footer-color);
}

#establishment_name {
    color: var(--main-theme-secondary-color);
    text-shadow: 0 0 5px var(--main-theme-tertiary-color);
    font-size: 1.5vw;
}

#edit_year {
    font-size: 1.5vw;
    color: var(--main-theme-secondary-color);
    text-shadow: 0 0 5px var(--main-theme-tertiary-color);
}
</style>
