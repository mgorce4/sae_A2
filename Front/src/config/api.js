// Configuration de l'URL de base de l'API
// En production, utilise l'IP du serveur
// En développement local, utilise localhost
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://164.81.120.77:8080'
