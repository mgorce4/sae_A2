import { createApp, ref } from 'vue'
import './style.css'
import App from './App.vue'
import { router } from './router/index.js'
import './utils/jwt.js' // Enregistre les intercepteurs axios JWT pour toute l'application

export const status = ref('')
export const userName = ref('')
export const institutionLocation = ref('')

export function removeUser() {
    if (typeof window === 'undefined') return

    // Seul le jwt_token est dans localStorage — on le supprime
    window.localStorage.removeItem('jwt_token')

    // Réinitialiser les états réactifs globaux pour que le header se mette à jour
    status.value = ''
    userName.value = ''
    institutionLocation.value = ''
}




createApp(App).use(router).mount('#app')

