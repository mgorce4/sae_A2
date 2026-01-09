import { createApp, ref } from 'vue'
import './style.css'
import App from './App.vue'
import Header from './pages/header_app.vue'
import Footer from './pages/footer_app.vue'

export const status = ref("")
export const userName = ref("")
export const institutionLocation = ref("")

export function removeUser() {
    localStorage.removeItem('access_rights')
    localStorage.removeItem('firstname')
    localStorage.removeItem('idUser')
    localStorage.removeItem('institutionLocation')
    localStorage.removeItem('institutionName')
    localStorage.removeItem('lastname')
    localStorage.removeItem('password')
    localStorage.removeItem('username')
    localStorage.removeItem('status')
    localStorage.removeItem('idInstitution')
}

import { Calendar, DatePicker } from 'v-calendar'
import 'v-calendar/style.css'

const app = createApp(App)

app.component('header-application', Header)
app.component('footer-application', Footer)
app.component('VCalendar', Calendar)
app.component('VDatePicker', DatePicker)


app.mount('#app')
