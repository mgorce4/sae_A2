import axios from 'axios'

export function setToken(token) {
  localStorage.setItem('jwt_token', token)
}

export function getToken() {
  return window.localStorage.getItem('jwt_token')
}

export function removeToken() {
  localStorage.removeItem('jwt_token')
}

/**
 * Décode le payload du JWT (base64) et retourne les droits d'accès.
 * Le JWT est signé par le serveur : son payload ne peut pas être falsifié
 * sans invalider la signature, qui est vérifiée à chaque requête côté backend.
 */
export function getAccessRightsFromToken() {
  const token = getToken()
  if (!token) return []
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.roles || []
  } catch {
    return []
  }
}

const ROLE_MAP = { 1: 'Professeur', 2: 'Administration', 3: 'Admin' }

/**
 * Dérive le statut affiché directement depuis le payload du JWT signé.
 * Jamais stocké en localStorage — impossible à falsifier.
 */
export function getStatusFromToken() {
  const roles = getAccessRightsFromToken()
  if (roles.length > 1) return 'Multiple'
  return ROLE_MAP[roles[0]] || ''
}

// Attache automatiquement le token JWT dans l'en-tête Authorization de toutes les requêtes axios
axios.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Redirige vers la page de login si le serveur répond 401
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      removeToken()
      window.location.hash = '#/'
    }
    return Promise.reject(error)
  }
)

