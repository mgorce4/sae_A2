import { createWebHashHistory, createRouter } from 'vue-router'
import { getToken, getAccessRightsFromToken } from '@/utils/jwt.js'

import Login from '../pages/login_page.vue'

import AdministrationDashboard from '../pages/administration_dashboard.vue'
import DsbrProf from '../pages/teacher_dashboard.vue'

import AdminDashboard from '../pages/admin_dashboard.vue'
import AdminRS from '../pages/admin_ressources_sheet.vue'

import MCCCSelectForm from '../pages/mccc_select_form.vue'
import MCCCDisplay from '../pages/mccc_display.vue'
import FormSae from '../pages/form_mccc_sae.vue'
import FormUE from '../pages/form_mccc_UE.vue'
import FormRessources from '../pages/form_mccc_ressources.vue'

import NotFound from '../pages/not_found.vue'
import FormRessourceSheet from '../pages/ressource_sheet_form.vue'
import ResourceSheetDisplay from '../pages/resource_sheet_display.vue'
import MCCCSelectPath from '../pages/mccc_select_path.vue'
import ControlCenter from '../pages/control_center.vue'
import HelpCenter from '../pages/help_center.vue'
import addTeacherPage from '../pages/add_teacher_page.vue'
import SyncadiaPresentation from '../pages/Syncadia_presentation_page.vue'
import MultiAccessRightDashboard from '../pages/multi_access_right_dashboard.vue'

const routes = [
  { path: '/', component: Login },
  {path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFound},
  { path: '/dashboard-administration', component: AdministrationDashboard },
  { path: '/teacher-dashboard', component: DsbrProf },

  { path: '/admin-dashboard', component: AdminDashboard },
  { path: '/admin-ressources-sheet', component: AdminRS },

  { path: '/mccc-select-form', component: MCCCSelectForm },
  { path: '/mccc-display', component: MCCCDisplay },
  { path: '/form-mccc-sae', component: FormSae },
  { path: '/form-mccc-UE', component: FormUE },
  { path: '/form-mccc-ressources', component: FormRessources },
  { path: '/form-ressource-sheet', component: FormRessourceSheet },
  { path: '/mccc-select-path', component: MCCCSelectPath },
  { path: '/resource-sheet-display', component: ResourceSheetDisplay },
  { path: '/control-center', component: ControlCenter },
  { path: '/help-center', component: HelpCenter },
  { path: '/add-teacher-page', component: addTeacherPage },
  { path: '/syncadia-presentation', component: SyncadiaPresentation },
  { path: '/multi_access_right_dashboard', component: MultiAccessRightDashboard },
]

export const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

// Routes accessibles sans authentification
const publicRoutes = ['/', '/syncadia-presentation']

router.beforeEach((to) => {
  const token = getToken()
  const isPublic = publicRoutes.includes(to.path)

  // Redirige vers login si pas de token et route protégée
  if (!token && !isPublic) {
    return { path: '/' }
  }

  // Un utilisateur déjà connecté qui retourne sur '/' est redirigé vers son dashboard
  if (token && to.path === '/') {
    const roles = getAccessRightsFromToken()
    if (roles.length === 1) {
      const map = { 1: '/teacher-dashboard', 2: '/dashboard-administration', 3: '/admin-dashboard' }
      return { path: map[roles[0]] || '/' }
    } else if (roles.length > 1) {
      return { path: '/multi_access_right_dashboard' }
    }
  }
})
