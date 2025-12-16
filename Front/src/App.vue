<script setup>
  import { ref, computed } from 'vue'
  import Login from './pages/login_page.vue'
  import AdministrationDashboard from './pages/administration_dashboard.vue'
  import MCCCSelectForm from './pages/mccc_select_form.vue'
  import NotFound from './pages/not_found.vue'
  import DsbrProf from './pages/teacher_dashboard.vue'
  import FormUE from './pages/form_mccc_UE.vue'
  import FormRessourceSheet from './pages/ressource_sheet_form.vue'

  const routes = {
    '/': Login,
    '/dashboard-administration': AdministrationDashboard,
    '/teacher_dashboard': DsbrProf,
    '/mccc-select-form': MCCCSelectForm,
    '/form-mccc-UE': FormUE,
    '/form-ressource-sheet': FormRessourceSheet
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
  <header-application/>
  <component :is="currentView" />
  <footer-application/>
</template>


