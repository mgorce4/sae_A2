import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import Header from './pages/header_app.vue'
import Footer from './pages/footer_app.vue'
import router from './router'

const app = createApp(App)

app.component('header-application', Header)
app.component('footer-application', Footer)

app.use(router)

app.mount('#app')
