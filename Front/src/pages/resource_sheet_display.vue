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
    <div class="component spb">
      <div id="return_arrow">
        <button id="back_arrow" onclick="document.location.href='#/dashboard-administration'">←</button>
        <p>Retour</p>
      </div>

      <p id="ref_resource">Référent module : {{resource_sheet.mainTeacher}}</p>
    </div>


    <div id="resource_sheet_display">
      <div class="header_Form">
        <p>Réf. UE : </p>
        <p>{{ ueLabels && ueLabels.length > 0 ? ueLabels.join(', ') : '' }}</p>
        <p class="title">{{resource_sheet.resourceName}}</p>
        <p>DEP : {{resource_sheet.department}}</p>
      </div>

      <div class="ref_Section">
        <p>Réf. Ressource : {{resource_sheet.resourceLabel}}</p>
      </div>

      <div id="panel_NP">
        <div class="dark_bar" style="margin: 0;">
          Objectif de la resource :
        </div>

        <div style="padding: 1.5vw">
          <p class="skill-input skill-input-description">{{resource_sheet.objective}}</p>
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
            <div v-for="skill in resource_sheet.skills" :key="skill.id" class="skill-row">
              <div class="skill-inputs">
                <p class="skill-input skill-input-label">{{skill.label}}</p>
                <p class="skill-input skill-input-description" rows="2">{{skill.description}}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div id="sae_alignement">
        <p class="section_title">SAE(s) Concérnées : </p>
        <div class="sae_switches_container">
          <div v-for="sae in resource_sheet.linkedSaes" :key="sae.id" class="sae_switch_item">
            <label class="switch">
              <input type="checkbox" :checked="sae.isLinked" disabled>
              <span class="slider"></span>
            </label>
            <span class="sae_label" :title="`ID: ${sae.id}, ApogeeCode: ${sae.apogeeCode}, Label: ${sae.label}`">{{ sae.label }}</span>
          </div>
        </div>
      </div>

      <div id="align_items_inline_center">
        <div id="align_items_column_left">
          <p class="subsection_title">Mots clés : </p>
          <div class="keywords-container">
            <div v-for="keyword in resource_sheet.keywords" :key="keyword" class="keyword-item" style="justify-content: center">
              <p class="keyword-input" style="margin-top: 0px; margin-bottom: 0px;flex: 0.5">{{keyword}}</p>
            </div>
          </div>
        </div>
        <div id="align_items_column_left">
          <p class="subsection_title">Modalités de mise en œuvre :</p>
          <div class="modalities-list">
            <div v-for="modality in resource_sheet.modalities" :key="modality" class="modality-item" >
              <p class="modality-textarea" rows="3">{{modality}}</p>
            </div>
          </div>
        </div>
      </div>

      <div id="hours_section"  v-if="resource_sheet.hoursTeacher !== undefined">
        <p class="section_title">Répartition des heures (volume étudiant) :</p>

        <p class="subsection_title" style="color: white; margin-top: 1.5vw; margin-bottom: 0.5vw;">Formation Initiale</p>
        <div class="hours_container">
          <div class="hours_row">
            <div class="hours_item">
              <label class="hours_label">CM</label>
              <div class="hours_box">
                <p class="hours_display" min="0" step="0.5">{{resource_sheet.hoursTeacher.cm}}</p>
              </div>
            </div>
            <div class="hours_item">
              <label class="hours_label">TD</label>
              <div class="hours_box">
                <p class="hours_display" min="0" step="0.5">{{resource_sheet.hoursTeacher.td}}</p>
              </div>
            </div>
            <div class="hours_item">
              <label class="hours_label">TP</label>
              <div class="hours_box">
                <p class="hours_display" min="0" step="0.5">{{resource_sheet.hoursTeacher.tp}}</p>
              </div>
            </div>
            <div class="hours_total_display">
              <p class="hours_total_text">Nombre d'heures total :
                <span class="hours_total_value">{{ resource_sheet.hoursTeacher.total }}</span> /
                <span class="hours_total_value">{{ resource_sheet.hoursPN.total }}</span>
              </p>
            </div>
          </div>
        </div>

        <div v-if="resource_sheet.hoursTeacherAlternance !== null">
          <p class="subsection_title" style="color: white; margin-top: 1.5vw; margin-bottom: 0.5vw;">Alternance</p>
          <div class="hours_container">
            <div class="hours_row">
              <div class="hours_item">
                <label class="hours_label">CM</label>
                <div class="hours_box">
                  <p class="hours_display" min="0" step="0.5">{{resource_sheet.hoursTeacherAlternance.cm}}</p>
                </div>
              </div>
              <div class="hours_item">
                <label class="hours_label">TD</label>
                <div class="hours_box">
                  <p class="hours_display" min="0" step="0.5">{{resource_sheet.hoursTeacherAlternance.td}}</p>
                </div>
              </div>
              <div class="hours_item">
                <label class="hours_label">TP</label>
                <div class="hours_box">
                  <p class="hours_display" min="0" step="0.5">{{resource_sheet.hoursTeacherAlternance.tp}}</p>
                </div>
              </div>
              <div class="hours_total_display">
                <p class="hours_total_text">Nombre d'heures total :
                  <span class="hours_total_value">{{ resource_sheet.hoursTeacherAlternance.total }}</span> /
                  <span class="hours_total_value">{{ resource_sheet.hoursPNAlternance.total }}</span>
                </p>
              </div>
            </div>
          </div>
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
</style>
