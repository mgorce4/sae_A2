import { createWebHashHistory, createRouter } from 'vue-router'

import Login from '../pages/login_page.vue'
import AdministrationDashboard from '../pages/administration_dashboard.vue'
import MCCCSelectForm from '../pages/mccc_select_form.vue'
import MCCCDisplay from '../pages/mccc_display.vue'
import NotFound from '../pages/not_found.vue'
import DsbrProf from '../pages/teacher_dashboard.vue'
import FormSae from '../pages/form_mccc_sae.vue'
import FormUE from '../pages/form_mccc_UE.vue'
import FormRessources from '../pages/form_mccc_ressources.vue'
import FormRessourceSheet from '../pages/ressource_sheet_form.vue'
import MCCCSelectPath from '../pages/mccc_select_path.vue'
import ResourceSheetDisplay from '../pages/resource_sheet_display.vue'
import ControlCenter from '../pages/control_center.vue'
import HelpCenter from '../pages/help_center.vue'
import addTeacherPage from '../pages/add_teacher_page.vue'

const routes = [
  { path: '/', component: Login },
  { path: '/dashboard-administration', component: AdministrationDashboard },
  { path: '/teacher-dashboard', component: DsbrProf },
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
  { path: '/:pathMatch(.*)*', component: NotFound },
]

export const router = createRouter({
  history: createWebHashHistory(),
  routes,
})
