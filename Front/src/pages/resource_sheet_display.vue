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
        <p>{{ ueLabels && ueLabels.length > 0 ? ueLabels.join(', ') : '###' }}</p>
        <p class="title">{{resource_sheet.resourceName}}</p>
        <p>DEP : {{resource_sheet.department}}</p>
      </div>

      <div class="ref_Section">
        <p>Réf. Ressource : {{resource_sheet.resourceLabel}}</p>
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
</style>
