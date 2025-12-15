import { createApp, ref } from 'vue'
import './style.css'
import App from './App.vue'
import Header from './pages/header_app.vue'
import Footer from './pages/footer_app.vue'
import router from './router'

export const status = ref("")
export const userName = ref("")

import { Calendar, DatePicker } from 'v-calendar'
import 'v-calendar/style.css'

const app = createApp(App)

app.component('header-application', Header)
app.component('footer-application', Footer)
app.component('VCalendar', Calendar)
app.component('VDatePicker', DatePicker)

app.use(router)

app.mount('#app')
