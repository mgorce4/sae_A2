<script setup>
import Header_app from './pages/header_app.vue'
import Footer_app from './pages/footer_app.vue'
</script>
<template>
    <Header_app />
    <main>
        <RouterView />
    </main>
    <Footer_app />
</template>