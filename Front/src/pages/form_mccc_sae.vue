<script setup>
    import { onMounted, ref, computed } from 'vue'
    import axios from 'axios'
    import { status } from '../main'
    
    status.value = "Administration"

    let display_more_area = ref(false)

    const saeTableV2 = ref([])

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

    // Filter SAE data by semester
    const filteredSaeTableV2 = computed(() => {
        return saeTableV2.value.filter((sae) => {
            return sae.semester == semesterNumber.value
        })
    })
    onMounted(async () => {
        const response = await axios.get(`http://localhost:8080/api/v2/mccc/saes/institution/${localStorage.idInstitution}`)
        saeTableV2.value = response.data
    })
</script>

<template>
    <p>{{ saeTableV2 }}</p>
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
                <div v-for="(value, index) in filteredSaeTableV2" v-bind:key="index" class="added_content_mccc">
                    <div class="dark_bar">
                        <p>{{ value.label }}</p>
                    </div>
                    <div class="panel_form_mccc container-fluid spa">
                        <div class="left_side">
                            <p>Code apogee : {{ value.apogeeCode }}</p>
                            <p>Nombre d'heures (formation initiale) : {{ value.hours }}</p>
                            <div class="container-fluid">
                            <input id="btn_cancel_UE" class="btn1" type="reset" value="Supprimer" @click="del(ue.ueNumber)">
                            <input id="btn_save_UE" class="btn1" type="submit" value="Modifier" @click="modify">
                            </div>
                        </div>
                        <div  class="right_side">
                            <p>Coefficients UE : </p>
                            <table>
                                <tr>
                                    <th v-for="(labelUe, index2) in value.ueCoefficients" v-bind:key="index2">{{ labelUe.ueLabel }}</th>
                                </tr>
                                <tr>
                                    <td v-for="(coefUe, index3) in value.ueCoefficients" v-bind:key="index3">{{ coefUe.coefficient }}</td>
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