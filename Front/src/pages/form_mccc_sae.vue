<script setup>
    import { onMounted, ref, computed } from 'vue'
    import axios from 'axios'
    import { status } from '../main'
    
    status.value = "Administration"

    let display_more_area = ref(false)

    const saeLinkResources = ref([])
    const teachers = ref([])
    const saeHours = ref([])
    const ueCoef = ref([])

    /* Extract ID from hash URL parameters */
    const getQueryParam = (param) => {
    const hash = window.location.hash
    const queryString = hash.split('?')[1]
    if (!queryString) return null
    const params = new URLSearchParams(queryString)
    return params.get(param)
    }

    const semesterNumber = ref(getQueryParam('id'))
    console.log("semestre saé : ", semesterNumber.value)
    
    onMounted(async () => {
        axios.get('http://localhost:8080/api/sae-link-resources').then(response => (saeLinkResources.value = response.data))
        axios.get('http://localhost:8080/api/main-teachers-for-resource').then(response => (teachers.value = response.data))
        axios.get('http://localhost:8080/api/sae-hours').then(response => (saeHours.value = response.data))
        axios.get('http://localhost:8080/api/ue-coefficient-saes').then(response => (ueCoef.value = response.data))
    })

    const joinSaesTables = computed(() => {
        // Get teachers for current institution
        const teachersForInstitution = teachers.value.filter(teacher => teacher.user?.institution?.idInstitution == localStorage.idInstitution)
        
        // Get resource IDs from those teachers
        const resourceIds = new Set(teachersForInstitution.map(teacher => teacher.idResource))
        
        // Filter SAEs by semester and resources from current institution
        const saesWithHours = saeLinkResources.value
            .filter(sae => resourceIds.has(sae.idResource) && sae.resource?.semester == semesterNumber.value)
            .map((saeLinkResource) => {
                const hours = saeHours.value.find((sh) => sh.idSAEHours === saeLinkResource.idSAE)
                return {
                    ...saeLinkResource,
                    saeHours: hours
                }
            })
        
        // Join with UE coefficients
        return saesWithHours.map((sae) => {
            const coef = ueCoef.value.filter((uec) => uec.sae?.idSAE === sae.idSAE)
            return {
                ...sae,
                ueCoefSae: coef
            }
        })
    })
</script>

<template>
    <div id="form_mccc_sae"> 
        <div class="return_arrow">
            <button class="back_arrow" onclick="document.location.href='#/mccc-select-form'">←</button>
            <p>Retour</p>
        </div>
        <div class="background_form_mccc">
            <div class="form">
                <div class="header_form_mccc">
                    <p class="title">Situation d'Apprentissage Évaluée</p>
                </div>
                <div class="dark_bar">
                    <p>Ajouter une SAÉ</p>
                    <button class="button_more" v-on:click="display_more_area = true">+</button>
                </div>
                <form v-show="display_more_area" method="post" v-on:submit.prevent="">
                    <div>Div ajout SAÉ</div>
                </form>
                <div v-for="(value, index) in joinSaesTables" v-bind:key="index" class="added_content_mccc">
                    <div class="dark_bar">
                        <p>{{ value.label }}</p>
                    </div>
                    <div class="panel_form_mccc container-fluid spa">
                        <div class="left_side">
                            <p>Code apogee : {{ value.sae.apogeeCode }}</p>
                            <p>Nombre d'heures (formation initiale) : {{ value.saeHours.hours }}</p>
                        </div>
                        <div  class="right_side">
                            <p>Coefficients UE : </p>
                            <table>
                                <tr>
                                    <th v-for="(labelUe, index2) in value.ueCoefSae" v-bind:key="index2">{{ labelUe.ue.label }}</th>
                                </tr>
                                <tr>
                                    <td v-for="(coefUe, index3) in value.ueCoefSae" v-bind:key="index3">{{ coefUe.coefficient }}</td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style>
#form_mccc_sae{
  margin: 3vw 14vw;
  justify-content: center;
}
</style>