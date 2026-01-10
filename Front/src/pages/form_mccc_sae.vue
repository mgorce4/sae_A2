<script setup>
    import { onMounted, ref, computed } from 'vue'
    import axios from 'axios'
    import { status } from '../main'
    
    status.value = "Administration"

    let display_more_area = ref(false)
    let display_modify_area = ref(false)
    let display_add_ue = ref(false)

    let modifySaeLabel = ref('')
    let modifySaeApogeeCode = ref('')
    let modifySaeHours = ref('')
    let ueCoefficients = ref([])

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

    function modifySae(sae) {
        modifySaeLabel.value = sae.label
        modifySaeApogeeCode.value = sae.apogeeCode
        modifySaeHours.value = sae.hours
        ueCoefficients.value = sae.ueCoefficients
        display_modify_area.value = true
    }
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
                    <div class="dark_bar"><p>Ajout d'une nouvelle SAÉ</p></div>
                    <div class="panel_form_mccc container-fluid spa">
                        <div class="left_side">
                            <label for="label_sae">Nom de la SAÉ :</label>
                            <input id="label_sae" type="text" v-model="newSaeLabel" required>

                            <label for="apogee_code_sae">Code Apogée :</label>
                            <input id="apogee_code_sae" type="text" v-model="newSaeApogeeCode" required>

                            <label for="hours_sae">Nombre d'heures (formation initiale) :</label>
                            <input id="hours_sae" type="number" v-model="newSaeHours" required>
                        </div>
                        <div class="right_side">
                            <input class="btn1" type="reset" value="Annuler" @click="display_more_area = false">
                            <input class="btn1" type="submit" value="Sauvegarder" @click="saveNewSae">
                        </div>
                    </div>
                </form>

                <form v-show="display_modify_area" method="post" v-on:submit.prevent="">
                    <div class="dark_bar"><p>Modification d'une SAÉ</p></div>
                    <div class="panel_form_mccc container-fluid">
                        <div class="left_side container-fluid cfh spa">
                            <div class="container-fluid spb">
                                <label for="label_sae_modify">Nom de la SAÉ :</label>
                                <input class="mccc_input" id="label_sae_modify" type="text" v-model="modifySaeLabel" required>
                            </div>
                            <div class="container-fluid spb">
                                <label for="apogee_code_sae_modify">Code Apogée :</label>
                                <input class="mccc_input" id="apogee_code_sae_modify" type="text" v-model="modifySaeApogeeCode" required>
                            </div>
                            <div class="container-fluid spb">
                                <label for="hours_sae_modify">Nombre d'heures (formation initiale) :</label>
                                <input class="mccc_input" id="hours_sae_modify" type="number" v-model="modifySaeHours" required>
                            </div>

                            <div class="container-fluid spa">
                                <input class="btn1" type="reset" value="Annuler" @click="display_modify_area = false; display_add_ue = false">
                                <input class="btn1" type="submit" value="Sauvegarder" @click="saveModifiedSae">
                            </div>
                        </div>
                        <table class="ueCoefficient">
                            <tr>
                                <td>U.E. affectée(s) : </td>
                                <th class="display_coef_label" v-for="(labelUe, index2) in ueCoefficients" v-bind:key="index2">{{ labelUe.ueLabel }}</th>
                                <td v-show="display_modify_area && !display_add_ue" @click="display_add_ue = true">
                                    <p class="button_more button_ue">+</p>
                                </td>
                                <th class="display_coef_label" v-show="display_modify_area && display_add_ue">...</th>
                            </tr>
                            <tr>
                                <td>Coefficient : </td>
                                <td class="display_coef_ue" v-for="(coefUe, index3) in ueCoefficients" v-bind:key="index3">{{ coefUe.coefficient }}</td>
                                <td class="display_coef_ue" v-show="display_modify_area && display_add_ue">...</td>
                            </tr>
                            <tr>
                                <td></td>
                                <td v-for="(index4) in ueCoefficients" v-bind:key="index4">
                                    <p class="button_more button_ue">X</p>
                                </td>
                                <td v-show="display_modify_area && display_add_ue" @click="display_add_ue = false">
                                    <p class="button_more button_ue">✓</p>
                                </td>
                            </tr>
                        </table>
                    </div>
                </form>

                <div v-for="(value, index) in filteredSaeTableV2" v-bind:key="index" class="added_content_mccc">
                    <div class="dark_bar">
                        <p>{{ value.label }}</p>
                    </div>
                    <div class="panel_form_mccc container-fluid">
                        <div class="left_side container-fluid cfh spa">
                            <div class="container-fluid spb">
                                <label>Code apogee : </label>
                                <p class="mccc_input">{{ value.apogeeCode }}</p>
                            </div>
                            <div class="container-fluid spb">
                                <label>Nombre d'heures (formation initiale) : </label>
                                <p class="mccc_input">{{ value.hours }}</p>
                            </div>
                            <div class="container-fluid spa">
                                <input id="btn_cancel_UE" class="btn1" type="reset" value="Supprimer">
                                <input id="btn_save_UE" class="btn1" type="button" value="Modifier" @click="modifySae(value)">
                            </div>
                        </div>
                        <div  class="right_side">
                            <table class="ueCoefficient">
                                <tr>
                                    <td>U.E. affectée(s) : </td>
                                    <th class="display_coef_label" v-for="(labelUe, index2) in value.ueCoefficients" v-bind:key="index2">{{ labelUe.ueLabel }}</th>
                                </tr>
                                <tr>
                                    <td>Coefficient : </td>
                                    <td class="display_coef_ue" v-for="(coefUe, index3) in value.ueCoefficients" v-bind:key="index3">{{ coefUe.coefficient }}</td>
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

.left_side {
    width: 45%;
    margin: 1vw 5vw 1vw 2vw;
}

.mccc_input {
    border: 0.1vw solid var(--main-theme-terciary-color);
    background-color: var(--div-rect-background-color);
    border-radius: 0.5vw;
    padding: 0.5vw;
    margin: 0.5vw 0;
    width: fit-content;
    min-width: 10vw;
    text-align: center;
    color: var(--main-theme-secondary-color);
}

.ueCoefficient {
    border-collapse: separate;
    border-spacing: 0.6vw;
    height: 3.5vw;
    text-align: center;
    align-items: center;
}

.display_coef_label, .display_coef_ue {
    width: 2vw;
    height: 1.25vw;
    border: 0.1vw solid var(--main-theme-terciary-color);
    background-color: var(--div-rect-background-color);
    padding: 0.5vw;
}

.display_coef_label {
    border-top-left-radius: 0.8vw;
    border-top-right-radius: 0.8vw;
}

.display_coef_ue {
    border-bottom-left-radius: 0.8vw;
    border-bottom-right-radius: 0.8vw;
}

.button_ue {
    width: 2vw !important;
    height: 2vw !important;
    margin: 0 auto;
}
</style>