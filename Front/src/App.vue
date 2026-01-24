<script setup>
import { ref, computed } from 'vue'
import Login from './pages/login_page.vue'
import AdministrationDashboard from './pages/administration_dashboard.vue'
import MCCCSelectForm from './pages/mccc_select_form.vue'
import NotFound from './pages/not_found.vue'
import DsbrProf from './pages/teacher_dashboard.vue'
import FormSae from './pages/form_mccc_sae.vue'
import FormUE from './pages/form_mccc_UE.vue'
import FormRessources from './pages/form_mccc_ressources.vue'
import FormRessourceSheet from './pages/ressource_sheet_form.vue'
import MCCCSelectPath from './pages/mccc_select_path.vue'
import ResourceSheetDisplay from './pages/resource_sheet_display.vue'
import ControlCenter from './pages/control_center.vue'

const routes = {
    '/': Login,
    '/dashboard-administration': AdministrationDashboard,
    '/teacher-dashboard': DsbrProf,
    '/mccc-select-form': MCCCSelectForm,
    '/form-mccc-sae': FormSae,
    '/form-mccc-UE': FormUE,
    '/form-mccc-ressources': FormRessources,
    '/form-ressource-sheet': FormRessourceSheet,
    '/mccc-select-path': MCCCSelectPath,
    '/resource-sheet-display': ResourceSheetDisplay,
    '/control-center': ControlCenter,
}

const currentPath = ref(window.location.hash)

window.addEventListener('hashchange', () => {
    currentPath.value = window.location.hash
})

const currentView = computed(() => {
    // Extract only the path part, ignoring query parameters
    const pathWithoutHash = currentPath.value.slice(1) || '/'
    const pathWithoutQuery = pathWithoutHash.split('?')[0]
    return routes[pathWithoutQuery] || NotFound
})
</script>

<template>
    <header-application />
    <main>
        <component :is="currentView" />
    </main>
    <footer-application />
</template>
