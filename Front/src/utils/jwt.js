
export function setToken(token) {
  localStorage.setItem('jwt_token', token)
}

export function getToken() {
  return window.localStorage.getItem('jwt_token')
}

export function removeToken() {
  localStorage.removeItem('jwt_token')
}

