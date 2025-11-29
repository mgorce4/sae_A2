<script setup>
  import { ref, computed } from 'vue'
  import Login from './pages/login_page.vue'
  import DsbrdAdmst from './pages/dashboard_administration.vue'
  import NotFound from './pages/not_found.vue'

  const routes = {
    '/': Login,
    '/dashboard-administration': DsbrdAdmst,
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
  <a href="#/">Login</a>
  <a href="#/dashboard-administration">dashboard administration</a>
  <component :is="currentView" />
  <footer-application/>
</template>
