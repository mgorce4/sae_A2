import { createApp, ref } from 'vue'
import './style.css'
import App from './App.vue'
import { router } from './router/index.js'

export const status = ref('')
export const userName = ref('')
export const institutionLocation = ref('')

export function removeUser() {
    if (typeof window === 'undefined') return

    const ls = window.localStorage

    ls.removeItem('access_rights')
    ls.removeItem('firstname')
    ls.removeItem('idUser')
    ls.removeItem('institutionLocation')
    ls.removeItem('institutionName')
    ls.removeItem('lastname')
    ls.removeItem('password')
    ls.removeItem('username')
    ls.removeItem('status')
    ls.removeItem('idInstitution')
}




createApp(App).use(router).mount('#app')

