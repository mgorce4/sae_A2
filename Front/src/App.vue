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
    return routes[currentPath.value.slice(1) || '/'] || NotFound
  })
</script>

<template>
  <header-application/>
  <a href="#/">- Login -</a>
  <a href="#/dashboard-administration">- dashboard administration -</a>
  <a href="#/teacher_dashboard">- dashboard professeur -</a>
  <a href="#/mccc-select-form">- Sélection formulaires MCCC -</a>
  <a href="#/form-ressource-sheet">- Formulaire ressource sheet -</a>
  <component :is="currentView" />
  <footer-application/>
</template>
