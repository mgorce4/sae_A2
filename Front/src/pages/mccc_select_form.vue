<script setup>
import { onMounted, ref } from 'vue'
import axios from 'axios'

const afficherBoutons = ref([
    [false, false],
])

const saeTable = ref([])
const ueTable = ref([])
const resourceTable = ref([])

onMounted(async () => {
    const saes = await axios.get(`http://localhost:8080/api/v2/mccc/saes/institution/${localStorage.idInstitution}`)
    saeTable.value = saes.data
    const ues = await axios.get(`http://localhost:8080/api/v2/mccc/ues/institution/${localStorage.idInstitution}`)
    ueTable.value = ues.data
    const resources = await axios.get(`http://localhost:8080/api/v2/mccc/resources/institution/${localStorage.idInstitution}`)
    resourceTable.value = resources.data
})

const hasSAEInSemester = (semester) => {
    return saeTable.value.some(sae => sae.semester === semester)
}

const hasResourceInSemester = (semester) => {
    return resourceTable.value.some(resource => resource.semester === semester)
}

const hasUEInSemester = (semester) => {
    return ueTable.value.some(ue => ue.semester === semester)
}

const getStatusForSemester = (semester) => {
    const hasResource = hasResourceInSemester(semester)
    const hasSAE = hasSAEInSemester(semester)
    
    if (hasResource || hasSAE) {
        return 'Rempli'
    }
    return 'Vierge'
}

const goToRessourceSheet = (url, semester) => {
  window.location.hash = `${url}?id=${semester}`
}
</script>

<template>
    <div id="form_select_page">
        <div style="display: flex; align-items: center; height: 1vw;">
            <button class="back_arrow" onclick="document.location.href='#/mccc-select-path'">←</button>
            <p class="back" >Retour</p>
        </div>

    <div v-for="(year, index) in afficherBoutons" v-bind:key="index" class="blue_rect">
      <p class="semester_display">Année {{ index + 1 }} :</p>

            <div class="container-fluid spe" style="align-items: normal;">
                <div class="semester_rect" v-for="(btn, index2) in year" v-bind:key="index2" v-on:mouseover="afficherBoutons[index][index2] = true" v-on:mouseout="afficherBoutons[index][index2] = false">
                    <p class="semester_display">Semestre {{ 2 * index + index2 + 1 }}</p>
                    <p class="status_display" v-show="!btn">{{ getStatusForSemester(2 * index + index2 + 1) }}</p>
                    <div v-show="btn" class="container-fluid spe">
                        <button v-show="btn" class="btn_form_acces" @click="goToRessourceSheet('#/form-mccc-UE', (2*index+index2+1))">UE</button>
                        <button v-show="hasResourceInSemester(2*index+index2+1) || hasUEInSemester(2*index+index2+1)" class="btn_form_acces" @click="goToRessourceSheet('#/form-mccc-ressources', (2*index+index2+1))">Ressource</button>
                        <button v-show="hasSAEInSemester(2*index+index2+1) || hasUEInSemester(2*index+index2+1)" class="btn_form_acces" @click="goToRessourceSheet('#/form-mccc-sae', (2*index+index2+1))">SAÉ</button>
                    </div>
                </div>
            </div>

        </div>
      </div>
    </div>
  </div>
</template>

<style>
#form_select_page {
  margin: 3vw 14vw;
  justify-content: center;
  color: var(--main-theme-tertiary-color);
}

.back {
  font-size: 1.5vw;
  font-weight: bold;
  color: var(--main-theme-tertiary-color);
  margin-left: 1.5vw;
}

.blue_rect {
  width: 90%;
  height: fit-content;
  margin: 2vw;
  padding: 1vw 2vw 2vw 2vw;
  border-radius: 1vw;
  background-color: var(--main-theme-background-color);
  color: var(--main-theme-secondary-color);
}

.semester_rect {
  width: 40%;
  height: fit-content;
  margin: 0 5% 6% 5%;
  padding: 1% 2% 3% 2%;
  border-radius: 1.5vw;
  background-color: var(--sub-section-background-color);
  color: var(--main-theme-secondary-color);
}

.semester_display {
  margin: 0.5vw 0;
  padding: 0.5vw;
  text-align: left;
  font-size: 1.5vw;
  font-weight: bold;
}

.status_display {
  text-align: right;
  margin: 0 5% 0 0;
  font-size: 1.3vw;
}

.btn_form_acces {
  width: 25%;
  height: fit-content;
  margin: 10% 0 0 0;
  padding: 2% 0;
  border-radius: 0.5vw;
  border-width: 0;
  background-color: var(--clickable-background-color);
  color: var(--main-theme-secondary-color);
  text-decoration: none;
  text-align: center;
  font-size: 0.75vw;
}

.btn_form_acces:hover {
  cursor: pointer;
}
</style>
