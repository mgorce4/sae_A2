<script setup>

import { computed, onMounted, ref } from 'vue'
import axios from 'axios'

const getQueryParam = (param) => {
  const hash = window.location.hash
  const queryString = hash.split('?')[1]
  if (!queryString) return null
  const params = new URLSearchParams(queryString)
  return params.get(param)
}

const resource_label = getQueryParam('label')
const resource_sheet = ref([])

const ueLabels = computed(() => {
  if (!resource_sheet.value?.ueReferences) return []
  return resource_sheet.value.ueReferences.map(ue => ue.label)
})

onMounted(() => {
  axios.get('http://localhost:8080/api/v2/resource-sheets')
    .then(response => {resource_sheet.value = response.data.find(sheet => sheet.resourceLabel === resource_label)})
})

</script>

<template>
  <div id="main">
    <div>{{resource_sheet}}</div>
    <div class="component spb">
      <div id="return_arrow">
        <button id="back_arrow" onclick="document.location.href='#/dashboard-administration'">←</button>
        <p>Retour</p>
      </div>

      <p v-if="resource_sheet.mainTeacher !== undefined" id="ref_resource">Référent module : {{resource_sheet.mainTeacher}}</p>
      <p v-else id="ref_resource">Référent module : Aucun professeur référent pour le module</p>
    </div>


    <div id="resource_sheet_display">
      <div class="header_Form">
        <p>Réf. UE : </p>
        <p v-if="ueLabels.length !== 0">{{ ueLabels && ueLabels.length > 0 ? ueLabels.join(', ') : '' }}</p>
        <p v-else style="max-width: 1vw">Aucune UE pour <br> cette ressource</p>

        <p v-if="resource_sheet.resourceName != undefined" class="title">{{resource_sheet.resourceName}}</p>
        <p v-else class="title">Aucun nom pour cette ressource</p>

        <p v-if="resource_sheet.department != undefined">DEP : {{resource_sheet.department}}</p>
        <p v-else>DEP : Aucun département <br> pour  cette ressource</p>
      </div>

      <div class="ref_Section">
        <p v-if="resource_sheet.resourceLabel">Réf. Ressource : {{resource_sheet.resourceLabel}}</p>
        <p v-else>Réf. Ressource : Aucun label pour cette ressource</p>
      </div>

      <div id="panel_NP">
        <div class="dark_bar" style="margin: 0;">
          Objectif de la resource :
        </div>

        <div style="padding: 1.5vw">
          <p v-if="resource_sheet.objective != undefined || resource_sheet.objective != null" class="skill-input skill-input-description">{{resource_sheet.objective}}</p>
          <p v-else class="skill-input skill-input-description">Aucun objectif pour cette ressource</p>
        </div>

      </div>

      <div id="panel_NP" style="justify-content: center">
        <div class="dark_bar" style="margin: 0">
          Compétences :
        </div>

        <div class="panel_NP">
          <div class="skills_table" style="padding: 1.5vw">
            <div class="skills-header">
              <div class="skill-column-label">Label</div>
              <div class="skill-column-description">Description</div>
            </div>
            <div v-if="resource_sheet.skills != undefined && resource_sheet.skills.length > 0">
              <div v-for="skill in resource_sheet.skills" :key="skill.id" class="skill-row">
                <div class="skill-inputs">
                  <p class="skill-input skill-input-label">{{skill.label}}</p>
                  <p class="skill-input skill-input-description" rows="2">{{skill.description}}</p>
                </div>
              </div>
            </div>
            <div v-else class="skill-row">
              <div class="skill-inputs">
                <p class="skill-input skill-input-label">Aucun label</p>
                <p class="skill-input skill-input-description" rows="2">Aucune déscription</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div id="sae_alignement">
        <p class="section_title">SAE(s) Concérnées : </p>
        <div v-if="resource_sheet.linkedSaes !== undefined && resource_sheet.linkedSaes.length > 0" class="sae_switches_container">
          <div v-for="sae in resource_sheet.linkedSaes" :key="sae.id" class="sae_switch_item">
            <label class="switch">
              <input type="checkbox" :checked="sae.isLinked" disabled>
              <span class="slider"></span>
            </label>
            <span class="sae_label">{{ sae.label }}</span>
          </div>
        </div>

        <div v-else class="sae_switches_container">
          <div class="sae_switch_item">
            <span class="sae_label">Aucune SAE n'est liée à cette ressource</span>
          </div>
        </div>
      </div>

      <div id="align_items_inline_center">
        <div id="align_items_column_left">
          <p class="subsection_title">Mots clés : </p>
          <div v-if="resource_sheet.keywords !== undefined && resource_sheet.keywords.length > 0" class="keywords-container">
            <div v-for="keyword in resource_sheet.keywords" :key="keyword" class="keyword-item" style="justify-content: center">
              <p class="keyword-input" style="margin-top: 0px; margin-bottom: 0px;flex: 0.5">{{keyword}}</p>
            </div>
          </div>

          <div v-else class="keywords-container">
            <div class="keyword-item" style="justify-content: center">
              <p class="keyword-input" style="margin-top: 0px; margin-bottom: 0px;flex: 0.5">Aucun mots clés</p>
            </div>
          </div>
        </div>
        <div id="align_items_column_left">
          <p class="subsection_title">Modalités de mise en œuvre :</p>
          <div v-if="resource_sheet.modalities !== undefined && resource_sheet.modalities.length > 0" class="modalities-list">
            <div v-for="modality in resource_sheet.modalities" :key="modality" class="modality-item" >
              <p class="modality-textarea" rows="3">{{modality}}</p>
            </div>
          </div>

          <div v-else class="modalities-list">
            <p class="modality-textarea" rows="3">Aucune modalité disponible</p>
          </div>
        </div>
      </div>

      <div id="hours_section" v-if="resource_sheet.hoursTeacher !== undefined">
        <p class="section_title">Répartition des heures (volume étudiant) :</p>

        <p class="subsection_title" style="color: white; margin-top: 1.5vw; margin-bottom: 0.5vw;">Formation Initiale</p>
        <div class="hours_container">
          <div class="hours_row">
            <div class="hours_item">
              <label class="hours_label">CM</label>
              <div class="hours_box">
                <p v-if="resource_sheet.hoursTeacher.cm !== undefined" class="hours_display" min="0" step="0.5">{{resource_sheet.hoursTeacher.cm}}</p>
                <p v-else class="hours_display" min="0" step="0.5">0</p>
              </div>
            </div>
            <div class="hours_item">
              <label class="hours_label">TD</label>
              <div class="hours_box">
                <p v-if="resource_sheet.hoursTeacher.td !== undefined" class="hours_display" min="0" step="0.5">{{resource_sheet.hoursTeacher.td}}</p>
                <p v-else class="hours_display" min="0" step="0.5">0</p>
              </div>
            </div>
            <div class="hours_item">
              <label class="hours_label">TP</label>
              <div class="hours_box">
                <p v-if="resource_sheet.hoursTeacher.tp !== undefined" class="hours_display" min="0" step="0.5">{{resource_sheet.hoursTeacher.tp}}</p>
                <p v-else class="hours_display" min="0" step="0.5">0</p>
              </div>
            </div>
            <div class="hours_total_display">
              <p class="hours_total_text">Nombre d'heures total :
                <span v-if="resource_sheet.hoursTeacher.total !== undefined" class="hours_total_value">{{ resource_sheet.hoursTeacher.total }}</span>
                <span v-else class="hours_total_value">0</span> /
                <span v-if="resource_sheet.hoursPN.total !== undefined" class="hours_total_value">{{ resource_sheet.hoursPN.total }}</span>
                <span v-else class="hours_total_value">0</span>
              </p>
            </div>
          </div>
        </div>

        <div v-if="resource_sheet.hoursTeacherAlternance != null || resource_sheet.hoursTeacherAlternance != undefined">
          <p class="subsection_title" style="color: white; margin-top: 1.5vw; margin-bottom: 0.5vw;">Alternance</p>
          <div class="hours_container">
            <div class="hours_row">
              <div class="hours_item">
                <label class="hours_label">CM</label>
                <div class="hours_box">
                  <p v-if="resource_sheet.hoursTeacherAlternance.cm != undefined" class="hours_display" min="0" step="0.5">{{resource_sheet.hoursTeacherAlternance.cm}}</p>
                  <p v-else class="hours_display" min="0" step="0.5">0</p>
                </div>
              </div>
              <div class="hours_item">
                <label class="hours_label">TD</label>
                <div class="hours_box">
                  <p v-if="resource_sheet.hoursTeacherAlternance.td !== undefined" class="hours_display" min="0" step="0.5">{{resource_sheet.hoursTeacherAlternance.td}}</p>
                  <p v-else class="hours_display" min="0" step="0.5">0</p>
                </div>
              </div>
              <div class="hours_item">
                <label class="hours_label">TP</label>
                <div class="hours_box">
                  <p v-if="resource_sheet.hoursTeacherAlternance.tp !== undefined" class="hours_display" min="0" step="0.5">{{resource_sheet.hoursTeacherAlternance.tp}}</p>
                  <p v-else class="hours_display" min="0" step="0.5">0</p>
                </div>
              </div>
              <div class="hours_total_display">
                <p class="hours_total_text">Nombre d'heures total :
                  <span v-if="resource_sheet.hoursTeacherAlternance.total !== undefined" class="hours_total_value">{{ resource_sheet.hoursTeacherAlternance.total }}</span>
                  <span v-else class="hours_total_value">0</span> /
                  <span v-if="resource_sheet.hoursPNAlternance.total !== undefined" class="hours_total_value">{{ resource_sheet.hoursPNAlternance.total }}</span>
                  <span v-else class="hours_total_value">0</span>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div id="pedagogical_content_section" v-if="resource_sheet.pedagogicalContent !== undefined">
        <p class="section_title">Contenu pédagogique : </p>
        <div class="pedagogical-content">
          <div class="pedagogical-section">
            <div class="pedagogical-header">
              <p class="pedagogical-title">CM</p>
            </div>
            <p class="pedagogical-subtitle">Détailles des contenu pédagogique des CM</p>
            <div class="pedagogical-items-container">
              <div v-if="resource_sheet.pedagogicalContent.cm.length === 0" class="no-content-message">
                Aucun contenu CM
              </div>
              <div v-for="(item, index) in resource_sheet.pedagogicalContent.cm" :key="index" class="pedagogical-items">
                <span class="pedagogical-number">{{ item.order }}</span>
                <p class="pedagogical-input" rows="1">{{item.content}}</p>
              </div>
            </div>
          </div>

          <div class="pedagogical-section">
            <div class="pedagogical-header">
              <p class="pedagogical-title">TD</p>
            </div>
            <p class="pedagogical-subtitle">Détailles des contenu pédagogique des TD</p>
            <div class="pedagogical-items-container">
              <div v-if="resource_sheet.pedagogicalContent.tp.length === 0" class="no-content-message">
                Aucun contenu TD
              </div>
              <div v-for="(item, index) in resource_sheet.pedagogicalContent.tp" :key="index" class="pedagogical-items">
                <span class="pedagogical-number">{{ item.order }}</span>
                <p class="pedagogical-input" rows="1">{{item.content}}</p>
              </div>
            </div>
          </div>

          <div class="pedagogical-section">
            <div class="pedagogical-header">
              <p class="pedagogical-title">TP</p>
            </div>
            <p class="pedagogical-subtitle">Détailles des contenu pédagogique des TP</p>
            <div class="pedagogical-items-container">
              <div v-if="resource_sheet.pedagogicalContent.tp.length === 0" class="no-content-message">
                Aucun contenu TP
              </div>
              <div v-for="(item, index) in resource_sheet.pedagogicalContent.tp" :key="index" class="pedagogical-items">
                <span class="pedagogical-number">{{ item.order }}</span>
                <p class="pedagogical-input" rows="1">{{item.content}}</p>
              </div>
            </div>
          </div>

          <div class="pedagogical-section">
            <div class="pedagogical-header">
              <p class="pedagogical-title">DS</p>
            </div>
            <p class="pedagogical-subtitle">Détailles des contenu pédagogique des DS</p>
            <div class="pedagogical-items-container">
              <div v-if="resource_sheet.pedagogicalContent.ds.length === 0" class="no-content-message">
                Aucun contenu DS
              </div>
              <div v-for="(item, index) in resource_sheet.pedagogicalContent.ds" :key="index" class="pedagogical-items">
                <span class="pedagogical-number">{{ item.order }}</span>
                <p class="pedagogical-input" rows="1">{{item.content}}</p>
              </div>
            </div>
          </div>

        </div>
      </div>

      <div id="form" v-if="resource_sheet.tracking !== undefined">
        <p class="section_title">Suivi de la ressource / module</p>
        <div>
          <p>Retour de l'équipe pédagogique et des acteurs impactés</p>
          <p v-if="resource_sheet.tracking.pedagogicalFeedback != undefined" id="text_area_styled" class="tracking-textarea">{{resource_sheet.tracking.pedagogicalFeedback}}</p>
          <p v-else id="text_area_styled" class="tracking-textarea">Aucun retour pédagogique</p>
          <p>Retour des étudiants</p>
          <p v-if="resource_sheet.tracking.studentFeedback != undefined" id="text_area_styled" class="tracking-textarea">{{resource_sheet.tracking.studentFeedback}}</p>
          <p v-else id="text_area_styled" class="tracking-textarea">Aucun retour des étudiants</p>
          <p>Amélioration(s) à mettre en oeuvre</p>
          <p v-if="resource_sheet.tracking.improvementSuggestions != undefined" id="text_area_styled" class="tracking-textarea">{{resource_sheet.tracking.improvementSuggestions}}</p>
          <p v-else id="text_area_styled" class="tracking-textarea">Aucun retour pédagogique</p>
        </div>
      </div>

    </div>
  </div>
</template>

<style>

#main {
  margin: 3vw 14vw;
  justify-content: center;
}

#ref_resource {
  color: black;
  font-weight: bold;
  font-size: 1.5vw;
}

#resource_sheet_display {
  background-color: #3D4375;
  border-radius: 10px;
  color: white;
  padding: 3vw;
}

#panel_NP {
  width: 97%;
  justify-self: center;
  background-color: rgba(0,0,0,0.35);
  border-radius: 15px;
  color: var(--main-theme-secondary-color);
  display: flex;
  flex-direction: column;
  margin-top: 2vw;
}


.pedagogical-items {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 0.8vw;
  margin-bottom: 0.8vw;
  background-color: rgba(200, 200, 200, 0.3);
  border-radius: 8px;
  padding: 0.6vw 0.8vw;
  min-height: 2.5vw;
  box-sizing: border-box;
  width: 100%;
}
</style>
