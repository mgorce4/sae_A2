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
 * Retourne le payload complet du JWT décodé en UTF-8.
 * Le contenu est signé par le serveur — impossible à falsifier sans invalider la signature.
 * atob() retourne des bytes Latin-1 — on réencode en UTF-8 pour gérer les accents.
 */
function getPayload() {
  const token = getToken()
  if (!token) return null
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    const jsonStr = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
        .join('')
    )
    return JSON.parse(jsonStr)
  } catch {
    return null
  }
}

/** Droits d'accès (rôles) de l'utilisateur connecté */
export function getAccessRightsFromToken() {
  return getPayload()?.roles || []
}

/** ID de l'utilisateur connecté */
export function getIdFromToken() {
  return getPayload()?.id ?? null
}

/** Prénom de l'utilisateur connecté */
export function getFirstnameFromToken() {
  return getPayload()?.firstname ?? ''
}

/** Nom de l'utilisateur connecté */
export function getLastnameFromToken() {
  return getPayload()?.lastname ?? ''
}

/** ID de l'institution de l'utilisateur connecté */
export function getIdInstitutionFromToken() {
  return getPayload()?.idInstitution ?? null
}

/** Nom de l'institution de l'utilisateur connecté */
export function getInstitutionNameFromToken() {
  return getPayload()?.institutionName ?? ''
}

/** Localisation de l'institution de l'utilisateur connecté */
export function getInstitutionLocationFromToken() {
  return getPayload()?.institutionLocation ?? ''
}

const ROLE_MAP = { 1: 'Professeur', 2: 'Administration', 3: 'Admin' }

/**
 * Dérive le statut affiché directement depuis le payload du JWT signé.
 */
export function getStatusFromToken() {
  const roles = getAccessRightsFromToken()
  if (roles.length > 1) return 'Multiple'
  return ROLE_MAP[roles[0]] || ''
}

// Evite d'enregistrer les intercepteurs plusieurs fois (HMR / double import)
if (!window.__axiosInterceptorsRegistered) {
  window.__axiosInterceptorsRegistered = true

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
        // Ne pas rediriger si la requête gère elle-même l'erreur (skipAuthRedirect)
        if (!error.config?.skipAuthRedirect) {
          removeToken()
          window.location.hash = '#/'
        }
      }
      return Promise.reject(error)
    }
  )
}
